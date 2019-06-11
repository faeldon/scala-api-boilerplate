package api.domain

import java.util.UUID
import doobie.postgres.implicits._
import doobie.{Get, Put}
import api.domain.syntax._
import shapeless.tag.@@

trait DomainModelSchema {

  /**
    * Gets
    */
  implicit def taggedStringGet[T]: Get[String @@ T] = Get[String].map(_.refined[T])
  implicit def taggedUUIDGet[T]: Get[UUID @@ T] = Get[UUID].map(_.refined[T])

  /**
    * Puts
    */
  implicit def taggedStringPut[T]: Put[String @@ T] = Put[String].contramap(_.asInstanceOf[String])
  implicit def taggedUUIDPut[T]: Put[UUID @@ T] = Put[UUID].contramap(_.asInstanceOf[UUID])

}
