package api.domain

import java.util.UUID
import java.time.Instant

import cats.instances.long._
import cats.instances.string._
import api.util.CirceUtil._
import io.circe.{Decoder, Encoder}
import api.domain.implicits._
import shapeless.tag
import shapeless.tag.@@

trait DomainModelCodecs {

  /**
    * Encoders
    */
  implicit val uuidEncoder: Encoder[UUID] =
    encoderFor[UUID]

  implicit val instantEncoder: Encoder[Instant] =
    encoderFor[Instant]

  implicit def taggedUUIDEncoder[T]: Encoder[UUID @@ T] =
    encoderFor[UUID @@ T]

  implicit def taggedStringEncoder[T]: Encoder[String @@ T] =
    encoderFor[String @@ T]

  implicit def taggedBigDecimalEncoder[T]: Encoder[BigDecimal @@ T] =
    encoderFor[BigDecimal @@ T]

  implicit def taggedLongEncoder[T]: Encoder[Long @@ T] =
    encoderFor[Long @@ T]

  /**
    * Decoders
    */
  implicit val uuidDecoder: Decoder[UUID] =
    decoderFor(UUID.fromString)

  implicit val instantDecoder: Decoder[Instant] =
    decoderFor(Instant.parse)

  implicit def taggedUUIDDecoder[T]: Decoder[UUID @@ T] =
    mappedDecoderFor(UUID.fromString(_))(tag[T].apply)

  implicit def taggedBigDecimalDecoder[T]: Decoder[BigDecimal @@ T] =
    mappedDecoderFor(BigDecimal.apply)(tag[T].apply)

  implicit def taggedStringDecoder[T]: Decoder[String @@ T] =
    mappedDecoderFor(identity)(tag[T].apply)

  implicit def taggedLongDecoder[T]: Decoder[Long @@ T] =
    mappedDecoderFor(_.toLong)(tag[T].apply)

}
