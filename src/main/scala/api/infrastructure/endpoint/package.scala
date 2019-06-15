package api.infrastructure

import api.domain._
import api.domain.users.User
import org.http4s.Response
import tsec.authentication.{AugmentedJWT, SecuredRequest, TSecAuthService}

package object endpoint {
  type AuthService[F[_], Auth] = TSecAuthService[User, AugmentedJWT[Auth, UserId], F]
  type AuthEndpoint[F[_], Auth] = PartialFunction[SecuredRequest[F, User, AugmentedJWT[Auth, UserId]], F[Response[F]]]
}
