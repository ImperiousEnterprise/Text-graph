package Services

import Models.{Group, GroupTableDef}
import com.google.inject.Inject
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/25/17.
  */
class GroupServiceImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends GroupService{
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val groups = TableQuery[GroupTableDef]

  def add(group: Group) = {
    dbConfig.db.run(groups returning groups.map(_.id) += group).map(id => group.copy(id = Some(id))).recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    dbConfig.db.run(groups.filter(_.id === id).delete)
  }

  def get(groupName: String): Future[Option[Group]] = {
    dbConfig.db.run(groups.filter(_.name === groupName).result.headOption)
  }
  def getByNameList() : Future[Seq[String]] = {
    val query = for (g <- groups) yield g.name
    val setquery = query.result
    val execute_query: Future[Seq[String]] = dbConfig.db.run(setquery)
    execute_query
  }

  def getAllRoom_ids() : Future[Seq[Int]] = {
    val query = for (g <- groups) yield g.room_id
    val setquery = query.result
    val execute_query: Future[Seq[Int]] = dbConfig.db.run(setquery)
    execute_query
  }

  def getAllRooms() : Future[Seq[Group]] = {
    dbConfig.db.run(groups.result)
  }
}
