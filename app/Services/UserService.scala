package Services

import Models.AppUser
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import scala.concurrent.Future

/**
  * Created by adeyemi on 1/20/17.
  */
trait AppUserService extends IdentityService[AppUser] {

  def retrieve(loginInfo: LoginInfo): Future[Option[AppUser]]

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: AppUser): Future[AppUser]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[AppUser]
}