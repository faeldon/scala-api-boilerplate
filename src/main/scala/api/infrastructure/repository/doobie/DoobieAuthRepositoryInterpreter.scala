package api.infrastructure.repository.doobie

import java.time.Instant

import api.domain._
import api.domain.implicits._
import cats._
import cats.data._
import cats.effect.Bracket
import cats.implicits._
import doobie._
import doobie.implicits._
import tsec.authentication.{AugmentedJWT, BackingStore}
import tsec.common.SecureRandomId
import tsec.jws.JWSSerializer
import tsec.jws.mac.{JWSMacCV, JWSMacHeader, JWTMacImpure}
import tsec.mac.jca.{MacErrorM, MacSigningKey}

private object AuthSQL {
  implicit val secureRandomIdPut: Put[SecureRandomId] =
    Put[String].contramap((_: Id[SecureRandomId]).widen)

  def insert[A](jwt: AugmentedJWT[A, UserId])(implicit hs: JWSSerializer[JWSMacHeader[A]]): Update0 =
    sql"""INSERT INTO JWT (ID, JWT, USER_ID, EXPIRY, LAST_TOUCHED)
          VALUES (${jwt.id}, ${jwt.jwt.toEncodedString}, ${jwt.identity}, ${jwt.expiry}, ${jwt.lastTouched})
       """.update

  def update[A](jwt: AugmentedJWT[A, UserId])(implicit hs: JWSSerializer[JWSMacHeader[A]]): Update0 =
    sql"""UPDATE JWT SET JWT = ${jwt.jwt.toEncodedString}, USER_ID = ${jwt.identity},
         | EXPIRY = ${jwt.expiry}, LAST_TOUCHED = ${jwt.lastTouched} WHERE ID = ${jwt.id}
       """.stripMargin.update

  def delete(id: SecureRandomId): Update0 =
    sql"DELETE FROM JWT WHERE ID = $id".update

  def select(id: SecureRandomId): Query0[(String, UserId, Instant, Option[Instant])] =
    sql"SELECT JWT, USER_ID, EXPIRY, LAST_TOUCHED FROM JWT WHERE ID = $id"
      .query[(String, UserId, Instant, Option[Instant])]
}

class DoobieAuthRepositoryInterpreter[F[_]: Bracket[?[_], Throwable], A](
  val key: MacSigningKey[A],
  val xa: Transactor[F]
)(implicit
  hs: JWSSerializer[JWSMacHeader[A]],
  s: JWSMacCV[MacErrorM, A]
) extends BackingStore[F, SecureRandomId, AugmentedJWT[A, UserId]] {

  override def put(jwt: AugmentedJWT[A, UserId]): F[AugmentedJWT[A, UserId]] =
    AuthSQL.insert(jwt).run.transact(xa).as(jwt)

  override def update(jwt: AugmentedJWT[A, UserId]): F[AugmentedJWT[A, UserId]] =
    AuthSQL.update(jwt).run.transact(xa).as(jwt)

  override def delete(id: SecureRandomId): F[Unit] =
    AuthSQL.delete(id).run.transact(xa).void

  override def get(id: SecureRandomId): OptionT[F, AugmentedJWT[A, UserId]] =
    OptionT(AuthSQL.select(id).option.transact(xa)).semiflatMap {
      case (jwtStringify, identity, expiry, lastTouched) =>
        JWTMacImpure.verifyAndParse(jwtStringify, key) match {
          case Left(err) => err.raiseError[F, AugmentedJWT[A, UserId]]
          case Right(jwt) => AugmentedJWT(id, jwt, identity, expiry, lastTouched).pure[F]
        }
    }
}

object DoobieAuthRepositoryInterpreter {
  def apply[F[_]: Bracket[?[_], Throwable], A](key: MacSigningKey[A], xa: Transactor[F])(implicit
    hs: JWSSerializer[JWSMacHeader[A]],
    s: JWSMacCV[MacErrorM, A]
  ): DoobieAuthRepositoryInterpreter[F, A] =
    new DoobieAuthRepositoryInterpreter(key, xa)
}
