package api.domain.authentication

import api.domain.users.{Role, User}
import tsec.passwordhashers.PasswordHash

final case class LoginRequest(
  userName: String,
  password: String
)

final case class SignupRequest(
  role: Role,
  userName: String,
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  phone: String
){
  def asUser[A](hashedPassword: PasswordHash[A]) : User = User(
    None,
    role,
    userName,
    firstName,
    lastName,
    email,
    hashedPassword.toString,
    phone
  )
}
