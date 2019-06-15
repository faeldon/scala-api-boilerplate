package api.domain.orders

import java.time.Instant
import api.domain._

case class Order(
    petId: Long,
    shipDate: Option[Instant] = None,
    status: OrderStatus = OrderStatus.Placed,
    complete: Boolean = false,
    id: Option[Long] = None,
    userId: Option[UserId]
)
