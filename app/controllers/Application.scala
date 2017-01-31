package controllers



import java.util.concurrent.TimeUnit

import Formz.{SignInForm, SignUpForm}
import Models.{Edge, _}
import Services.{ChatworkUserService, GroupService, MessageService}
import com.google.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import org.joda.time.DateTime
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json, Writes}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * The basic application controller.
  *
  * @param messagesApi The Play messages API.
  * @param socialProviderRegistry The social provider registry.
  */
class ApplicationController @Inject() (
                                        val messagesApi: MessagesApi,
                                        val silhouette: Silhouette[SilEnv],
                                        groupService: GroupService,
                                        messageService: MessageService,
                                        chatworkUserService: ChatworkUserService,
                                        socialProviderRegistry: SocialProviderRegistry)
  extends AuthController {

  /**
    * Handles the index action.
    *
    * @return The result to display.
    */
  def index = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.home(request.identity)))
  }

  /**
    * Handles the Sign In action.
    *
    * @return The result to display.
    */
  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
    }
  }

  /**
    * Handles the Sign Up action.
    *
    * @return The result to display.
    */
  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }

  /**
    * Handles the Sign Out action.
    *
    * @return The result to display.
    */
  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    env.eventBus.publish(LogoutEvent(request.identity, request))

    env.authenticatorService.discard(request.authenticator, result)
  }

  def listchats = SecuredAction.async{ implicit request =>
    groupService.getAllRooms() map { group => Ok(views.html.chats(request.identity,group))}
  }

  def getgraph(group_id :Int, date : String) = SecuredAction.async{ implicit request =>
    val current  = DateTime.now()

    val messages = date match{
      case "today" => messageService.getToday(group_id)
      case "yesterday" => messageService.getYesterday(group_id)
      case "thismonth" => messageService.getByMonth(current.getMonthOfYear, current.getYear,group_id)
      case "lastmonth" => messageService.getByMonth(current.minusMonths(1).getMonthOfYear,current.minusMonths(1).getYear,group_id)
    }
    val chatuser = chatworkUserService.getEveryone()
    val r = scala.util.Random


    val nod : Future[Seq[ListBuffer[Node]]] = chatuser map {user => var node = ListBuffer[Node](); user map{
      u => node += Node(u.account_id.toString, u.name,r.nextInt(user.size),r.nextInt(user.size),4)}}

    val mem : Future[Seq[ListBuffer[Edge]]] = messages map {mes => var edge = ListBuffer[Edge](); mes map {m =>
      edge += Edge(increment(edge.length),m.by_user_id.toString, m.to_user_id.toString)}}

    for {
      n <- nod
      e <- mem
    } yield Ok(Json.obj("nodes" -> removedEmployee(n.head,first(e.headOption)), "edges" -> first(e.headOption)))

  }

  def first[List[Edge]](maybe: Option[ListBuffer[Edge]]): scala.collection.immutable.List[Edge] = maybe match {
    case Some(xs) => xs.toList
    case None => ListBuffer[Edge]().toList
  }
  def removedEmployee(maybe: ListBuffer[Node], edges : List[Edge]) : scala.collection.immutable.List[Node] = {
    var node = ListBuffer[Node]() ++ maybe
    val r = scala.util.Random

    var listofInts : ListBuffer[String] = {
      var list = ListBuffer[String]();
      maybe map {m => list += m.id} ;
      list
    }

    edges map { e =>
      if(!listofInts.contains(e.source)) {
        listofInts += e.source
        node += Node(e.source, "Removed Member", r.nextInt(maybe.size), r.nextInt(maybe.size), 4)
      }else if(!listofInts.contains(e.target)){
        listofInts += e.target
        node += Node(e.target, "Removed Member",r.nextInt(maybe.size),r.nextInt(maybe.size),4)
      }
    }
    node.toList

  }

  def increment(inc: Int) : String = {
    "e" + (inc).toString
  }

}