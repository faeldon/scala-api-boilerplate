package api.domain.users

import api.domain._
import cats.data.EitherT

trait UserValidationAlgebra[F[_]] {
  def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit]

  def exists(userId: Option[UserId]): EitherT[F, UserNotFoundError.type, Unit]
}
