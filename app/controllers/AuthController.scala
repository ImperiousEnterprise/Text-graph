package controllers

import Models.{AppUser, SilEnv}
import play.api.mvc.Controller
import play.api.i18n.I18nSupport
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.api.actions._

/**
  * Created by adeyemi on 1/24/17.
  */
trait AuthController extends Controller with I18nSupport {
  def silhouette: Silhouette[SilEnv]
  def env: Environment[SilEnv] = silhouette.env

  def SecuredAction = silhouette.SecuredAction
  def UnsecuredAction = silhouette.UnsecuredAction
  def UserAwareAction = silhouette.UserAwareAction

  implicit def securedRequest2User[A](implicit request: SecuredRequest[SilEnv, A]): AppUser = request.identity
  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[SilEnv, A]): Option[AppUser] = request.identity
}
