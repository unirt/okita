package controllers

import java.sql.{Date, Timestamp}

import models.{Event, Participant, User}
import dao.{EventDAO, ParticipantDAO, UserDAO}
import javax.inject._
import org.joda.time.DateTime
import play.api.mvc._
import play.api.libs.json.{Format, JsPath, JsString, Json}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class EventForm(title: String, content: Option[String], date: Date, start_time: String, end_time: String, owner_id: Long, participants_email: Seq[String])
object EventForm {
  implicit val format = Json.format[EventForm]
}

@Singleton
class EventController @Inject()(cc: ControllerComponents, eventDao: EventDAO, userDao: UserDAO, participantDao: ParticipantDAO)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def create = Action.async(parse.json) { implicit request =>
    val eventForm = request.body.as[EventForm]
    val newEvent: Event = models.Event.apply(None, eventForm.title, eventForm.content, eventForm.date, DateTime.parse(eventForm.start_time), DateTime.parse(eventForm.end_time), eventForm.owner_id)

    eventDao.insert(newEvent).map { _ =>
      val ce: Option[Event] = Await.result(eventDao.getLatestByOwner(eventForm.owner_id), Duration.Inf)
      ce match {
        case Some(e) => {
          val newParts: Seq[Participant] = eventForm.participants_email.map { p =>
            val user: Option[User] = Await.result(userDao.findByEmail(p), Duration.Inf)
            user match {
              case Some(u) => Participant.apply(None, 0, e.id.getOrElse(1), u.id.getOrElse(1))
            }
          }
          participantDao.insert(newParts)
          Created(Json.obj("id"->e.id, "title"->e.title, "content"->e.content, "date"->e.date, "start_time"->e.start_time.toString, "end_time"->e.end_time.toString, "owner_id"->e.owner_id ))
        }
        case None => NotAcceptable(Json.obj("error"->"failed"))
      }
    }
  }

  def index = Action.async {
    eventDao.all().map {
      events => Ok(Json.arr(events.map(e => Json.obj(
        "id"->e.id, "title"->e.title, "content"->e.content, "content"->e.content, "date"->e.date, "start_time"->e.start_time.toString, "end_time"->e.end_time.toString, "owner_id"->e.owner_id )
      )))
    }
  }
}
