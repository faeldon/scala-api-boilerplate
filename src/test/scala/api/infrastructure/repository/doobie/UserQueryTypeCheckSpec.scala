package api.infrastructure.repository.doobie

import org.scalatest._
import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor

import api.PetStoreArbitraries.user

class UserQueryTypeCheckSpec extends FunSuite with Matchers with IOChecker {
  override val transactor : Transactor[IO] = testTransactor

  import UserSQL._

  test("Typecheck user queries") {
    user.arbitrary.sample.map { u =>
      check(insert(u))
      check(byUserName(u.userName))
      u.id.foreach(id => check(update(u, id)))
    }
    check(selectAll)
    check(select(1L))
    check(delete(1L))
  }
}
