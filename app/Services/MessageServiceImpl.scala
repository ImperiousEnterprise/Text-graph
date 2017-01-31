package Services

import Models.{Message, MessageTableDef}
import org.joda.time.DateTime

import scala.concurrent.Future
import slick.driver.MySQLDriver.api._
import com.github.tototoshi.slick.MySQLJodaSupport._
import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/25/17.
  */
class MessageServiceImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends MessageService{

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val messages = TableQuery[MessageTableDef]

  def add(message: Message) = {
    dbConfig.db.run(messages returning messages.map(_.id) += message).map(id => message.copy(id = Some(id))).recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    dbConfig.db.run(messages.filter(_.id === id).delete)
  }

  def getToday(group_id : Int) : Future[Seq[Message]] = {
    val query = for(m <- messages.sortBy(_.date.desc) if m.group_id === group_id
      && (m.date >= DateTime.now().withTime(0, 0, 0, 0) && m.date <= DateTime.now().withTime(23, 59, 59, 59))) yield m
    val setquery = query.result
    val execute_query: Future[Seq[Message]] = dbConfig.db.run(setquery)
    execute_query
  }

  def getYesterday(group_id : Int): Future[Seq[Message]] = {
    val query = for(m <- messages.sortBy(_.date.desc) if m.group_id === group_id
      && (m.date >= DateTime.now().withTime(0, 0, 0, 0).minusDays(1) && m.date <= DateTime.now().withTime(23, 59, 59, 59).minusDays(1))) yield m
    val setquery = query.result
    val execute_query: Future[Seq[Message]] = dbConfig.db.run(setquery)
    execute_query
  }

  def getByMonth(month:Int, year: Int, group_id : Int): Future[Seq[Message]] = {
    val current = DateTime.now().withMonthOfYear(month).withYear(year)
    val query = for(m <- messages.sortBy(_.date.desc) if m.group_id === group_id
      && (m.date >= current.withTime(0, 0, 0, 0).withDate(current.getYear,current.getMonthOfYear,1) &&
      m.date <= current.dayOfMonth().withMaximumValue().withTime(23,59,59,59))) yield m
    val setquery = query.result
    val execute_query: Future[Seq[Message]] = dbConfig.db.run(setquery)
    execute_query
    //dbConfig.db.run(messages.filter(_.date == DateTime.now().).result)
  }

  def getLast100Messages(group_id : Int) : Future[Seq[Int]] = {
    //val query = for (m <- messages.sortBy(_.date.desc)) yield m.message_id
    val setquery = messages.filter(_.group_id === group_id).sortBy(_.date.desc).map(m => m.message_id).take(100).result
    val execute_query: Future[Seq[Int]] = dbConfig.db.run(setquery)
    execute_query
  }
}
