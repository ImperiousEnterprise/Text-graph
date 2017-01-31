package Models.DAO

/**
  * Created by adeyemi on 1/24/17.
  */
import java.util.UUID

import Models.AppUser
import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future

/**
  * Give access to the user object.
  */
trait AppUserDAO {

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[AppUser]]

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID): Future[Option[AppUser]]

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: AppUser): Future[AppUser]
}
