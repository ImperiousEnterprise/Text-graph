package controllers



import Formz.SignInForm
import Models.SilEnv
import Services.AppUserService
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * The credentials auth controller.
  *
  * @param messagesApi The Play messages API.
  * @param userService The user service implementation.
  * @param authInfoRepository The auth info repository implementation.
  * @param credentialsProvider The credentials provider.
  * @param socialProviderRegistry The social provider registry.
  * @param configuration The Play configuration.
  * @param clock The clock instance.
  */
class CredentialsAuthController @Inject() (val silhouette: Silhouette[SilEnv],
                                            val messagesApi: MessagesApi,
                                            userService: AppUserService,
                                            authInfoRepository: AuthInfoRepository,
                                            credentialsProvider: CredentialsProvider,
                                            socialProviderRegistry: SocialProviderRegistry,
                                            configuration: Configuration,
                                            clock: Clock)
  extends AuthController {

  /**
    * Authenticates a user against the credentials provider.
    *
    * @return The result to display.
    */
  def authenticate = Action.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signIn(form, socialProviderRegistry))),
      data => {
        val credentials = Credentials(data.email, data.password)
        val rememberMe = data
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              val c = configuration.underlying
              for{
                authenticator <- env.authenticatorService.create(loginInfo).map(authenticatorWithRememberMe(_, rememberMe.rememberMe))
                cookie <- env.authenticatorService.init(authenticator)
                result <- env.authenticatorService.embed(cookie, Redirect(routes.ApplicationController.index))
              } yield {
                env.eventBus.publish(LoginEvent(user, request))
                result
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("invalid.credentials"))
        }
      }
    )
  }

  private def authenticatorWithRememberMe(authenticator: CookieAuthenticator, rememberMe: Boolean) = {
    if (rememberMe) {
      val c = configuration.underlying
      authenticator.copy(
        expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
        idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
        cookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")
      )
    } else
      authenticator
  }
}
