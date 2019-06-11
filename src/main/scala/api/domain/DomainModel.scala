package api.domain

import java.util.UUID
import shapeless.tag.@@

/**
  * Provides data types safety across the domain model at compile time.
  * Because it's unsafe to pass around arbitrary UUIDs and String.
  */
trait DomainModel {

  sealed trait UserIdT
  sealed trait UserNameT

  type UserId    = UUID @@ UserIdT
  type UserName  = String @@ UserNameT

}
