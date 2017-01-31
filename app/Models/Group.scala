package Models

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/20/17.
  */
case class Group (id:Option[Int] = None, name:String, room_id: Int)


class GroupTableDef(tag: Tag) extends Table[Group](tag, "Group") {

  def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
  def name = column[String]("group_name")
  def room_id = column[Int]("room_id")

  override def * =
    (id.?, name, room_id) <> ((Group.apply _).tupled, Group.unapply)
}


object Group{

  implicit val resReads: Reads[Group] = (
    (JsPath \ "id").readNullable[Int] and
    (JsPath \ "name").read[String] and
    (JsPath \ "room_id").read[Int]
    )(Group.apply _)

}
