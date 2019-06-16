package models

case class User(id: Option[Long] = None, email: String, expo_token: Option[String] = None, name: Option[String] = None)
