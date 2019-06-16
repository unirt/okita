package models
import java.sql.Date

import org.joda.time.DateTime

case class User(id: Option[Long] = None, email: String, expo_token: Option[String] = None, name: Option[String] = None)
case class Event(id: Option[Long] = None, title: String, content: Option[String] = None, date: Date, start_time: DateTime, end_time: DateTime, owner_id: Long)
case class Participant(id: Option[Long] = None, status: Int = 0, event_id: Long, user_id: Long)
