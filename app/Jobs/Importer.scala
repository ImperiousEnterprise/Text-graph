package Jobs

import java.lang.NullPointerException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import Services.{ChatworkUserService, GroupService, MessageService, UrlService}
import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.{AbstractModule, Inject}
import com.google.inject.name.Named
import Models._
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/20/17.
  */

class Scheduler @Inject() (val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    Duration(100, TimeUnit.MICROSECONDS), Duration(10, TimeUnit.MINUTES), schedulerActor, "updateMessages")
  system.scheduler.schedule(
    Duration(50, TimeUnit.MICROSECONDS), Duration(5, TimeUnit.MINUTES), schedulerActor, "updateRoom")
  system.scheduler.schedule(
    Duration(0, TimeUnit.MICROSECONDS), Duration(3, TimeUnit.MINUTES), schedulerActor, "updateUsers")
}


class JobModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[SchedulerActor]("scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}

class SchedulerActor @Inject() (groupService: GroupService,
                                chatworkUserService: ChatworkUserService,
                                messageService: MessageService,
                                urlService : UrlService) extends Actor {
  def receive = {
    case "updateMessages" => updateMessages()
    case "updateRoom" => updateRoom()
    case "updateUsers" => updateUsers()
  }

  def updateMessages(): Unit ={
    Logger.debug("Updating Messages Started")
    val retrievedGroupIds : Future[Seq[Int]] = groupService.getAllRoom_ids()
    retrievedGroupIds onComplete {
      case Success(list) => {
          Logger.debug("Finished Getting Group Ids for Message")
          list map {
            id => fetchMessagesfromGroupId(id)
          }
          Logger.debug("Finished Fetching Messages for Group Ids")
        }
      case Failure(e) => {Logger.error("Updating Messages had this error:" + e.getCause.getMessage)}
    }
    Logger.debug("Updating Messages Finished")
  }

  def fetchMessagesfromGroupId(id: Int): Unit ={
    val messageBygroup = urlService.messages(id.toString)
    messageBygroup map {
      messageList =>
        Logger.debug("Finsihed Getting Messages for group id :" + id)
        messageList map {
        mes => addMessage(mes,id)
      }
        Logger.debug("Finsihed Adding Messages for group id :" + id)
    }recover {
      case _:NullPointerException => Logger.error("NullPointerException for fetching group id " + id)
    }
  }

  def addMessage(chatwork: ChatworkMessage, group_id : Int) : Unit= {
    val messageList : Future[Seq[Int]] = messageService.getLast100Messages(group_id)
    messageList map { messages =>
      if (!messages.contains(chatwork.message_id)){
        val body = chatwork.body.split("\n")
        for (bod <- body)
          if (getChatId(bod) != 0)
            messageService.add(Message(None, group_id, chatwork.account.account_id, getChatId(bod), convertToDateTime(chatwork.send_time), chatwork.message_id))
      }
    }recover{
      case _: NullPointerException => Logger.error("NullPointError with group id on getting last 100 messages" + group_id )
    }
  }
  def convertToDateTime(time: Int) : DateTime = {
    new DateTime( ( time.asInstanceOf[Long] * 1000L ), DateTimeZone.forID( "Asia/Tokyo" ) )
  }

  def getChatId(sub: String) : Int = {
    if(contains(sub,"rp aid=")) {
      sub.substring(sub.indexOf("rp aid=")+7, sub.indexOf("to")).replaceAll(" ","").toInt
    }else if(contains(sub,"To:")){
      try{
        val subtr = sub.substring(sub.indexOf("To:")+3)
        subtr.substring(0,subtr.indexOf("]")).toInt
      }catch{
        case _:Throwable => Logger.error("String IndexOut of bounds or Number format error " +
          "for : " + sub); 0
      }
    }else{
      0
    }
  }

  def contains(str:String, sub : String) : Boolean = {
    val pattern = "\\b"+sub+"\\b"
    val p = Pattern.compile(pattern)
    p.matcher(str).find()
  }
  def updateUsers(): Unit ={
    Logger.debug("Updating User running")
    val retrievedUsers : Future[Seq[Int]] = chatworkUserService.getListofIds()
    val currentUsers : Future[Seq[ChatworkUser]] = urlService.users()

    currentUsers onComplete{
      case Success (currentUsers) => currentUsers map { v => addUser(v, retrievedUsers)}
      case Failure(e) => {Logger.error("UpdateUsers had this error:" + e.getMessage)}
    }

    Logger.debug("Updating User Finished")

  }

  def addUser(user: ChatworkUser, list : Future[Seq[Int]]) : Unit = {
    list onComplete {
      case Success(list) => val exist = list.contains(user.account_id); if (!exist) {chatworkUserService.add(user)}
      case Failure(e) => Logger.error("AddUsers had this error:" + e.getMessage)
    }
  }

  def updateRoom(): Unit ={
    Logger.debug("Updating Room running")
    val retrievedGroup : Future[Seq[String]] = groupService.getByNameList()
    val currentGroup : Future[Seq[Group]] = urlService.room()

    currentGroup onComplete {
      case Success(currentGroup) => currentGroup map {v => addGroup(v, retrievedGroup)}
      case Failure(e) => Logger.error("Updating Room had this error:" + e.getMessage)
    }

    Logger.debug("Updating Room Finished")

  }

  def addGroup(add_group : Group, list : Future[Seq[String]]): Unit ={
    list onComplete {
      case Success(list) => val exist = list.contains(add_group.name); if (!exist) {groupService.add(add_group)}
      case Failure(e) => Logger.error("AddingRoom had this error:" + e.getMessage)
    }
  }
}


