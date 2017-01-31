package Services

import Models.{ChatworkUser, ChatworkUserTableDef}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by adeyemi on 1/25/17.
  */
trait ChatworkUserService {

  def add(user: ChatworkUser)

  def delete(id: Int): Future[Int]

  def get(userName: String): Future[Option[ChatworkUser]]

  def get_chatwork_id(id: Int): Future[Option[ChatworkUser]]

  def getListofIds() : Future[Seq[Int]]

  def getEveryone() : Future[Seq[ChatworkUser]]
}
