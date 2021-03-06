package Utils

/**
  * Created by adeyemi on 1/25/17.
  */
import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import controllers.routes
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, OptionalSourceMapper}


import scala.concurrent.Future

/**
  * A secured error handler.
  */
class ErrorHandler @Inject() (
                               env: play.api.Environment,
                               config: Configuration,
                               sourceMapper: OptionalSourceMapper,
                               router: javax.inject.Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
    with SecuredErrorHandler {


  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
    def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
      Future.successful(Redirect(routes.ApplicationController.signIn()))
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
   def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
      Future.successful(Redirect(routes.ApplicationController.signIn()))
  }
}