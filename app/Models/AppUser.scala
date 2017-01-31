package Models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by adeyemi on 1/24/17.
  */

case class AppUser(
                    userID: UUID,
                    loginInfo: LoginInfo,
                    firstName: Option[String],
                    lastName: Option[String],
                    fullName: Option[String],
                    email: Option[String]) extends Identity
