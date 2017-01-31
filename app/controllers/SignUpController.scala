package controllers

/**
  * Created by adeyemi on 1/25/17.
  */
import java.util.UUID

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers._
import Formz.SignUpForm
import Models.{AppUser, SilEnv}
import Services.AppUserService
import com.google.inject.Inject
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action

import scala.concurrent.Future

/**
  * The sign up controller.
  *
  * @param messagesApi The Play messages API.
  * @param userService The user service implementation.
  * @param authInfoRepository The auth info repository implementation.
  * @param passwordHasher The password hasher implementation.
  */
class SignUpController @Inject() (
                                   val silhouette: Silhouette[SilEnv],
                                   val messagesApi: MessagesApi,
                                   userService: AppUserService,
                                   authInfoRepository: AuthInfoRepository,
                                   passwordHasher: PasswordHasher)
  extends AuthController {

  /**
    * Registers a new user.
    *
    * @return The result to display.
    */
  def signUp = Action.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signUp(form))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Future.successful(Redirect(routes.ApplicationController.signUp()).flashing("error" -> Messages("user.exists")))
          case None =>
            val authInfo = passwordHasher.hash(data.password)
            val user = AppUser(
              userID = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = Some(data.firstName),
              lastName = Some(data.lastName),
              fullName = Some(data.firstName + " " + data.lastName),
              email = Some(data.email)
            )
            for {
              user <- userService.save(user)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              value <- env.authenticatorService.init(authenticator)
              result <- env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
            } yield {
              env.eventBus.publish(SignUpEvent(user, request))
              env.eventBus.publish(LoginEvent(user, request))
              result
            }
        }
      }
    )
  }
}
