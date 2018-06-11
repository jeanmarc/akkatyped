import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}

import scala.concurrent.Await
import scala.concurrent.duration._

object ChatRoom {
  val rnd = scala.util.Random
  sealed trait RoomCommand
  final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand
  final case class LeaveRoom(handler: ActorRef[SessionCommand]) extends RoomCommand

  sealed trait SessionEvent
  final case class SessionGranted(handle: ActorRef[SessionCommand]) extends SessionEvent
  final case class SessionDenied(reason: String) extends SessionEvent
  final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  trait SessionCommand
  final case class PostMessage(message: String) extends SessionCommand
  private final case class NotifyClient(message: MessagePosted) extends SessionCommand
  final case object LeaveRoom extends SessionCommand

  private final case class PublishSessionMessage(screenName: String, message: String)
    extends RoomCommand

  val behavior: Behavior[RoomCommand] =
    chatRoom(Set.empty)

  private def chatRoom(sessions: Set[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { (ctx, msg) ⇒
      msg match {
        case GetSession(screenName, client) ⇒
          // create a child actor for further interaction with the client
          val ses = ctx.spawn(
            session(ctx.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name))
          client ! SessionGranted(ses)
          chatRoom(sessions + ses)
        case PublishSessionMessage(screenName, message) ⇒
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions foreach (_ ! notification)
          Behaviors.same
        case LeaveRoom(handler) =>
          val remaining = sessions - handler
          if (remaining.size > 0) {
            chatRoom(remaining)
          } else {
            println( "room is now empty. Quitting...")
            Behavior.stopped
          }
      }
    }

  private def session(
                       room:       ActorRef[RoomCommand],
                       screenName: String,
                       client:     ActorRef[SessionEvent]): Behavior[SessionCommand] =
    Behaviors.setup { ctx =>
      ctx.watch(client)
      Behaviors.receive[SessionCommand] { (myCtx, msg) =>
        msg match {
          case LeaveRoom =>
            room ! LeaveRoom(ctx.self)
            Behaviors.stopped
          case PostMessage(message) ⇒
            // from client, publish to others via the room
            room ! PublishSessionMessage(screenName, message)
            Behaviors.same
          case NotifyClient(message) ⇒
            // published from the room
            client ! message
            Behaviors.same
      }}.receiveSignal {
        case (_, Terminated(`client`)) =>
          room ! LeaveRoom(ctx.self)
          Behaviors.stopped
      }
    }
}

import ChatRoom._


object MainChatroom extends App {

  def connectedGabbler(nick: String, roomHandler: ActorRef[SessionCommand]): Behavior[SessionEvent] = {
    val rnd = scala.util.Random
    Behaviors.receiveMessage {
      case SessionDenied(reason) =>
        println(s"Session denied to $nick: $reason")
        Behaviors.stopped
      case SessionGranted(handle) ⇒
        handle ! PostMessage(s"Hello World again from $nick!")
        connectedGabbler(nick, handle)
      case MessagePosted(screenName, message) ⇒
        screenName match {
          case `nick` =>
            Behavior.same
          case _ =>
            println(s"$nick has seen message posted by '$screenName': $message")
            if (message.startsWith("Bye")) {
              println(s"$nick sees $screenName leaving, so I better leave too")
              roomHandler ! PostMessage(s"Bye, I am leaving too ($nick)")
              roomHandler ! LeaveRoom
              Behaviors.stopped
            } else {
              if (rnd.nextDouble() < 0.95) {
                roomHandler ! PostMessage(s"Hi $screenName, how are you?")
                Behaviors.same
              } else {
                println(s"$nick has had enough, bye")
                roomHandler ! PostMessage(s"Bye, I am leaving ($nick)")
                roomHandler ! LeaveRoom
                Behaviors.stopped
              }
            }
        }
    }
  }

  def namedGabbler(nick: String): Behavior[SessionEvent] = {
    Behaviors.receiveMessage {
      case SessionDenied(reason) =>
        println(s"Session denied to $nick: $reason")
        Behaviors.stopped
      case SessionGranted(handle) ⇒
        handle ! PostMessage(s"Hello World from $nick!")
        connectedGabbler(nick, handle)
      case MessagePosted(screenName, message) ⇒
        println(s"$nick has seen message posted by '$screenName': $message")
        println(s"$nick cannot respond yet (no handler)")
        Behaviors.same
    }
  }

  val main: Behavior[NotUsed] =
    Behaviors.setup { ctx ⇒
      val chatRoom = ctx.spawn(ChatRoom.behavior, "chatroom")
      val trollRef = ctx.spawn(namedGabbler("Troll"), "troll")
      val groupieRef = ctx.spawn(namedGabbler("Groupie"), "groupie")
      val gabblerRef = ctx.spawn(namedGabbler("The Duke"), "gabbler")
      ctx.watch(chatRoom)
      chatRoom ! GetSession("The Duke", gabblerRef)
      chatRoom ! GetSession("Groupie", groupieRef)
      chatRoom ! GetSession("Troll", trollRef)

      Behaviors.receiveSignal {
        case (_, Terminated(ref)) ⇒
          Behaviors.stopped
      }
    }

  val system = ActorSystem(main, "ChatRoomDemo")
  Await.result(system.whenTerminated, 60.seconds)
}
