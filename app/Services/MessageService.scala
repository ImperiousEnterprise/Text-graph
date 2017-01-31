package Services

import java.sql.Timestamp

import Models.{Message, MessageTableDef}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by adeyemi on 1/25/17.
  */
trait MessageService {

  def add(message: Message)
  def delete(id: Int): Future[Int]

  def getToday(group_id : Int) : Future[Seq[Message]]

  def getYesterday(group_id : Int): Future[Seq[Message]]

  def getByMonth(month:Int,year: Int, group_id : Int): Future[Seq[Message]]

  def getLast100Messages(group_id : Int) : Future[Seq[Int]]

}
