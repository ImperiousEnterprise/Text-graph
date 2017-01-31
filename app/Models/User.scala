package Models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsPath, Reads}

import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by adeyemi on 1/19/17.
  */

case class ChatworkUser (id:Option[Int] = None, name:String, account_id:Int)


class ChatworkUserTableDef(tag: Tag) extends Table[ChatworkUser](tag, "ChatWorkUser") {

  def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
  def name = column[String]("user_name")
  def account_id = column[Int]("chatwork_id")

  override def * =
    (id.?, name, account_id) <> ((ChatworkUser.apply _).tupled, ChatworkUser.unapply)
}


object ChatworkUser{

  implicit val userReads: Reads[ChatworkUser] = (
    (JsPath \ "id").readNullable[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "account_id").read[Int]
    )(ChatworkUser.apply _)

}
