package api.domain.users

import cats.data.OptionT
import api.domain._

trait UserRepositoryAlgebra[F[_]] {
  def create(user: User): F[User]

  def update(user: User): OptionT[F, User]

  def get(userId: UserId): OptionT[F, User]

  def delete(userId: UserId): OptionT[F, User]

  def findByUserName(userName: String): OptionT[F, User]

  def deleteByUserName(userName: String): OptionT[F, User]

  def list(pageSize: Int, offset: Int): F[List[User]]
}
