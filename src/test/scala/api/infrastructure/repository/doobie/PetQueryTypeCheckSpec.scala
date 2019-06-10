package api.infrastructure.repository.doobie

import org.scalatest._
import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import api.PetStoreArbitraries.pet
import cats.data.NonEmptyList
import cats.syntax.applicative._


class PetQueryTypeCheckSpec extends FunSuite with Matchers with IOChecker {
  override val transactor : Transactor[IO] = testTransactor

  import PetSQL._

  test("Typecheck pet queries") {
    pet.arbitrary.sample.map{ p =>
      check(selectByStatus(p.status.pure[NonEmptyList]))
      check(insert(p))
      p.id.foreach(id => check(PetSQL.update(p, id)))
    }

    check(selectTagLikeString("example".pure[NonEmptyList]))
    check(select(1L))
    check(selectAll)
    check(delete(1L))
    check(selectByNameAndCategory("name", "category"))
  }
}
