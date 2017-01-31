package Models

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by adeyemi on 1/22/17.
  */
case class ChatworkMessage (message_id:Int, account: Account , body: String, send_time: Int)
case class Account(account_id: Int, name: String)

object Account {
  implicit val accountReads: Reads[Account] = (
  (JsPath \ "account_id").read[Int] and
  (JsPath \ "name").read[String]
  )(Account.apply _)
}

object ChatworkMessage {
  implicit val chatworkMessageReads: Reads[ChatworkMessage] = (
    (JsPath \ "message_id").read[Int] and
    (JsPath \ "account").read[Account] and
    (JsPath \ "body").read[String] and
    (JsPath \ "send_time").read[Int]
  )(ChatworkMessage.apply _)
}
