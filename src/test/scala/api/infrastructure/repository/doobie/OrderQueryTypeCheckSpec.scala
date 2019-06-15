package api.infrastructure.repository.doobie

import java.util.UUID

import org.scalatest._
import cats.effect.IO
import cats.implicits._
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor

import api.PetStoreArbitraries.order
import api.domain.syntax._

class OrderQueryTypeCheckSpec extends FunSuite with Matchers with IOChecker {
  import OrderSQL._

  override val transactor : Transactor[IO] = testTransactor

  test("Typecheck order queries") {
    check(delete(1L))
    check(select(1L))

    val userId = UUID.randomUUID().asUserId
    order(userId.some).arbitrary.sample.map { o =>
      check(insert(o))
    }
  }
}
