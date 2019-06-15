package api.infrastructure.endpoint

import cats.implicits._
import api.domain._
import api.domain.implicits._
import api.domain.users._
import cats.effect._
import org.http4s._
import tsec.authentication.{AugmentedJWT, IdentityStore, JWTAuthenticator, SecuredRequestHandler, buildBearerAuthHeader}
import tsec.jws.mac.{JWSMacCV, JWTMac}
import tsec.mac.jca.HMACSHA256

import scala.concurrent.duration._

class AuthTest[F[_]: Sync](userRepo: UserRepositoryAlgebra[F] with IdentityStore[F, UserId, User])(implicit cv: JWSMacCV[F, HMACSHA256]) {

  private val symmetricKey = HMACSHA256.unsafeGenerateKey
  private val jwtAuth: JWTAuthenticator[F, UserId, User, HMACSHA256] = JWTAuthenticator.unbacked.inBearerToken(1.day, None, userRepo, symmetricKey)
  val securedRqHandler: SecuredRequestHandler[F, UserId, User, AugmentedJWT[HMACSHA256, UserId]] = SecuredRequestHandler(jwtAuth)

  private def embedInBearerToken(r: Request[F], a: AugmentedJWT[HMACSHA256, UserId]): Request[F] = {
    r.putHeaders {
      val stringify = JWTMac.toEncodedString(a.jwt)
      buildBearerAuthHeader(stringify)
    }
  }

  def embedToken(user: User, r: Request[F]): F[Request[F]] = {
    for {
      u <- userRepo.create(user)
      token <- jwtAuth.create(u.id.get)
    } yield embedInBearerToken(r, token)
  }
}
