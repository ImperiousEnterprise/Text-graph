package Models.DAO

import java.util.UUID

import Models.AppUser
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import slick.lifted.{TableQuery, Tag}
import slick.driver.MySQLDriver.api._

/**
  * Created by adeyemi on 1/24/17.
  */
trait DBTableDefinitions {
  case class DBUser(
                      userID: String,
                      firstName: Option[String],
                      lastName: Option[String],
                      fullName: Option[String],
                      email: Option[String])

  class DBUsers(tag: Tag) extends Table[DBUser](tag, "AppUser") {
    def id = column[String]("id", O.PrimaryKey)

    def firstName = column[Option[String]]("firstName")

    def lastName = column[Option[String]]("lastName")

    def fullName = column[Option[String]]("fullName")

    def email = column[Option[String]]("email")

    override def * = (id, firstName, lastName, fullName, email) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(
                          id: Option[Long],
                          providerID: String,
                          providerKey: String
                        )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "Logininfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def providerID = column[String]("providerID")

    def providerKey = column[String]("providerKey")

    override def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
                              userID: String,
                              loginInfoId: Long
                            )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "Userlogininfo") {
    def userID = column[String]("AppUserID")

    def loginInfoId = column[Long]("loginInfoId")

    override def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
                             hasher: String,
                             password: String,
                             salt: Option[String],
                             loginInfoId: Long
                           )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "Passwordinfo") {
    def hasher = column[String]("hasher")

    def password = column[String]("password")

    def salt = column[Option[String]]("salt")

    def loginInfoId = column[Long]("loginInfoId")

    override def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info (
                            id: Option[Long],
                            token: String,
                            secret: String,
                            loginInfoId: Long
                          )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "Oauth1info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info (
                            id: Option[Long],
                            accessToken: String,
                            tokenType: Option[String],
                            expiresIn: Option[Int],
                            refreshToken: Option[String],
                            loginInfoId: Long
                          )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "Oauth2info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("logininfoId")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  val slickDBUsers = TableQuery[DBUsers]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]

  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
