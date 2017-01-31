package Services

import Models._
import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by adeyemi on 1/20/17.
  */
class UrlService @Inject() (ws: WSClient,configuration: play.api.Configuration,
                            implicit val context: ExecutionContext) {

  def room(): Future[Seq[Group]] ={
    val request: WSRequest = ws.url("https://api.chatwork.com/v1/rooms")
    val complexRequest: WSRequest =
      request.withHeaders("X-ChatWorkToken" -> configuration.underlying.getString("chatwork.accesskey"))
    val futureResponse: Future[Seq[Group]] = complexRequest.get().map {
      response =>
        (response.json.validate[Seq[Group]].get)
      }
    futureResponse
  }

  def users() : Future[Seq[ChatworkUser]] = {
    val request: WSRequest = ws.url("https://api.chatwork.com/v1/contacts")
    val complexRequest: WSRequest =
      request.withHeaders("X-ChatWorkToken" -> configuration.underlying.getString("chatwork.accesskey"))
    val futureResponse: Future[Seq[ChatworkUser]] = complexRequest.get().map {
      response =>
        (response.json.validate[Seq[ChatworkUser]].get)
    }
    futureResponse
  }

  def messages(roomNumber: String): Future[Seq[ChatworkMessage]] = {
    val request: WSRequest = ws.url(s"https://api.chatwork.com/v1/rooms/${roomNumber}/messages?force=1")
    val complexRequest: WSRequest =
      request.withHeaders("X-ChatWorkToken" -> configuration.underlying.getString("chatwork.accesskey"))
    val futureResponse: Future[Seq[ChatworkMessage]] = complexRequest.get().map {
      response =>
        (response.json.validate[Seq[ChatworkMessage]].get)
    }

    futureResponse
  }

}
