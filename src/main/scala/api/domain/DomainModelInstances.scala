package api.domain

import java.time.Instant
import java.util.UUID
import cats.Show
import shapeless.tag.@@

trait DomainModelInstances {

  implicit val uuidShow: Show[UUID] =
    new Show[UUID] {
      def show(t: UUID): String = t.toString
    }

  implicit val instantShow: Show[Instant] =
    new Show[Instant] {
      def show(t: Instant): String = t.toString
    }

  implicit val bigDecimalShow: Show[BigDecimal] =
    new Show[BigDecimal] {
      def show(t: BigDecimal): String = t.toString
    }

  implicit def taggedShow[A, T](implicit ev: Show[A]): Show[A @@ T] =
    new Show[A @@ T] {
      def show(t: A @@ T): String = ev.show(t)
    }

}
