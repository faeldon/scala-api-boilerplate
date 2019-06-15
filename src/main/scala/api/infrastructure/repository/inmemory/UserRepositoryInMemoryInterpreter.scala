package api.infrastructure.repository.inmemory

import java.util.UUID

import cats.implicits._
import cats.Applicative
import cats.data.OptionT

import api.domain._
import api.domain.syntax._
import api.domain.users.{User, UserRepositoryAlgebra}
import tsec.authentication.IdentityStore

import scala.collection.concurrent.TrieMap

class UserRepositoryInMemoryInterpreter[F[_]: Applicative]
  extends UserRepositoryAlgebra[F]
    with IdentityStore[F, UserId, User] {

  private val cache = new TrieMap[UserId, User]

  def create(user: User): F[User] = {
    val id = UUID.randomUUID().asUserId
    val toSave = user.copy(id = id.some)
    cache += (id -> toSave)
    toSave.pure[F]
  }

  def update(user: User): OptionT[F, User] = OptionT {
    user.id.traverse{ id =>
      cache.update(id, user)
      user.pure[F]
    }
  }

  def get(id: UserId): OptionT[F, User] =
    OptionT.fromOption(cache.get(id))

  def delete(id: UserId): OptionT[F, User] =
    OptionT.fromOption(cache.remove(id))

  def findByUserName(userName: String): OptionT[F, User] =
    OptionT.fromOption(cache.values.find(u => u.userName == userName))

  def list(pageSize: Int, offset: Int): F[List[User]] =
    cache.values.toList.sortBy(_.lastName).slice(offset, offset + pageSize).pure[F]

  def deleteByUserName(userName: String): OptionT[F, User] =
    OptionT.fromOption(
      for {
        user <- cache.values.find(u => u.userName == userName)
        removed <- cache.remove(user.id.get)
      } yield removed
    )
}

object UserRepositoryInMemoryInterpreter {
  def apply[F[_]: Applicative]() =
    new UserRepositoryInMemoryInterpreter[F]
}
