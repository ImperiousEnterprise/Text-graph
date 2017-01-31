package Services

import Models.{ChatworkUser, ChatworkUserTableDef}
import com.google.inject.Inject
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/25/17.
  */
class ChatworkUserServiceImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ChatworkUserService{

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val users = TableQuery[ChatworkUserTableDef]

  def add(user: ChatworkUser) = {
    dbConfig.db.run(users returning users.map(_.id) += user).map(id => user.copy(id = Some(id))).recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(userName: String): Future[Option[ChatworkUser]] = {
    dbConfig.db.run(users.filter(_.name === userName).result.headOption)
  }

  def get_chatwork_id(id: Int): Future[Option[ChatworkUser]] = {
    dbConfig.db.run(users.filter(_.account_id === id).result.headOption)
  }

  def getListofIds() : Future[Seq[Int]] = {
    val query = for (u <- users) yield u.account_id
    val setquery = query.result
    val execute_query: Future[Seq[Int]] = dbConfig.db.run(setquery)
    execute_query
  }

  def getEveryone() : Future[Seq[ChatworkUser]] = {
    dbConfig.db.run(users.result)
  }

}
