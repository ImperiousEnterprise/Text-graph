package Services

import Models.{AppUser, Group, GroupTableDef}
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by adeyemi on 1/25/17.
  */
trait GroupService {

  def add(group: Group)

  def delete(id: Int): Future[Int]

  def get(groupName: String): Future[Option[Group]]

  def getByNameList() : Future[Seq[String]]

  def getAllRoom_ids() : Future[Seq[Int]]

  def getAllRooms() : Future[Seq[Group]]
}
