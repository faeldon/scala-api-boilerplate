package api.infrastructure.repository.doobie

import java.util.UUID
import org.scalatest._
import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor

import api.PetStoreArbitraries.user
import api.domain.syntax._

class UserQueryTypeCheckSpec extends FunSuite with Matchers with IOChecker {
  override val transactor : Transactor[IO] = testTransactor

  import UserSQL._

  test("Typecheck user queries") {
    val userId = UUID.randomUUID().asUserId
    user.arbitrary.sample.map { u =>
      check(insert(u))
      check(byUserName(u.userName))
      u.id.foreach(id => check(update(u, id)))
    }
    check(selectAll)
    check(select(userId))
    check(delete(userId))
  }
}
