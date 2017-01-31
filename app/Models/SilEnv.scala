package Models

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

/**
  * Created by adeyemi on 1/24/17.
  */
trait SilEnv extends Env {
  type I = AppUser
  type A = CookieAuthenticator
}