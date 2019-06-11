package api.infrastructure.repository.doobie

import cats.data.OptionT
import cats.effect.Bracket
import cats.implicits._
import doobie._
import doobie.implicits._
import io.circe.parser.decode
import io.circe.syntax._
import api.domain.users.{Role, User, UserRepositoryAlgebra}
import tsec.authentication.IdentityStore
import SQLPagination._

private object UserSQL {

  // H2 does not support JSON data type.
  implicit val roleMeta: Meta[Role] =
    Meta[String].imap(decode[Role](_).leftMap(throw _).merge)(_.asJson.toString)


  def insert(user: User): Update0 = sql"""
    INSERT INTO USERS (ROLE, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, HASH, PHONE)
    VALUES (${user.role}, ${user.userName}, ${user.firstName}, ${user.lastName}, ${user.email}, ${user.hash}, ${user.phone})
    RETURNING ID
  """.update // NOTE: Manually returning ID since postgresql is putting double quotes on "ID" causing error

  def update(user: User, id: Long): Update0 = sql"""
    UPDATE USERS
    SET ROLE = ${user.role}, FIRST_NAME = ${user.firstName}, LAST_NAME = ${user.lastName},
        EMAIL = ${user.email}, HASH = ${user.hash}, PHONE = ${user.phone}
    WHERE ID = $id
  """.update

  def select(userId: Long): Query0[User] = sql"""
    SELECT ID, ROLE, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, HASH, PHONE
    FROM USERS
    WHERE ID = $userId
  """.query

  def byUserName(userName: String): Query0[User] = sql"""
    SELECT ID, ROLE, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, HASH, PHONE
    FROM USERS
    WHERE USER_NAME = $userName
  """.query[User]

  def delete(userId: Long): Update0 = sql"""
    DELETE FROM USERS WHERE ID = $userId
  """.update

  val selectAll: Query0[User] = sql"""
    SELECT ID, ROLE, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, HASH, PHONE
    FROM USERS
  """.query
}

class DoobieUserRepositoryInterpreter[F[_]: Bracket[?[_], Throwable]](val xa: Transactor[F])
extends UserRepositoryAlgebra[F]
with IdentityStore[F, Long, User] { self =>
  import UserSQL._

  def create(user: User): F[User] =
    insert(user).withUniqueGeneratedKeys[Long]("ID").map(id => user.copy(id = id.some)).transact(xa)

  def update(user: User): OptionT[F, User] =
    OptionT.fromOption[F](user.id).semiflatMap { id =>
      UserSQL.update(user, id).run.transact(xa).as(user)
    }

  def get(userId: Long): OptionT[F, User] = OptionT(select(userId).option.transact(xa))

  def findByUserName(userName: String): OptionT[F, User] =
    OptionT(byUserName(userName).option.transact(xa))

  def delete(userId: Long): OptionT[F, User] = get(userId).semiflatMap(user =>
    UserSQL.delete(userId).run.transact(xa).as(user)
  )

  def deleteByUserName(userName: String): OptionT[F, User] =
    findByUserName(userName).mapFilter(_.id).flatMap(delete)

  def list(pageSize: Int, offset: Int): F[List[User]] =
    paginate(pageSize, offset)(selectAll).to[List].transact(xa)
}

object DoobieUserRepositoryInterpreter {
  def apply[F[_]: Bracket[?[_], Throwable]](xa: Transactor[F]): DoobieUserRepositoryInterpreter[F] =
    new DoobieUserRepositoryInterpreter(xa)
}
