package Models

import java.sql.Timestamp

import org.joda.time.{DateTime, DateTimeZone}
import slick.driver.MySQLDriver.api._
import com.github.tototoshi.slick.MySQLJodaSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/20/17.
  */
case class Message (id:Option[Int] = None, group_id:Int, by_user_id: Int, to_user_id:Int, date: DateTime, message_id: Int)


class MessageTableDef(tag: Tag) extends Table[Message](tag, "Message") {
  def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
  def group_id = column[Int]("group_id")
  def by_user_id = column[Int]("by_user_id")
  def to_user_id = column[Int]("to_user_id")
  def date = column[DateTime]("date")
  def message_id = column[Int]("message_id")

  override def * =
    (id.?, group_id, by_user_id, to_user_id, date, message_id) <> ((Message.apply _).tupled, Message.unapply)
}


object Message{
  implicit val instantColumnType: BaseColumnType[DateTime] =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime, DateTimeZone.UTC)
    )
}
