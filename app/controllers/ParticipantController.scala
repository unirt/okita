package controllers

import java.sql.Date

import models.{Event, Participant, User}
import dao.{EventDAO, ParticipantDAO, UserDAO}
import javax.inject._
import org.joda.time.DateTime
import play.api.mvc._
import play.api.libs.json.{Format, JsPath, JsString, Json}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ParticipantController @Inject()(cc: ControllerComponents, eventDao: EventDAO, userDao: UserDAO, participantDao: ParticipantDAO)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def show(event_id: Long) = Action.async {
    participantDao.findByEventId(event_id).map {
      pp => Ok(Json.arr(pp.map(p => Json.obj(
        "id"->p.id, "event_id"->p.event_id, "user_id"->p.user_id, "status"->p.status
      ))))
    }
  }

  def okita(event_id: Long, user_id: Long) = Action.async {
    participantDao.findByEventAndUser(event_id, user_id).map {
      pp => pp match {
        case Some(p) => {
          val id: Long = p.id.getOrElse(-1)
          if (id == -1) {
            NotAcceptable(Json.obj("error"->"failed"))
          } else {
            participantDao.updateStatus(id, 1)
            Accepted(Json.obj(
              "message"->"you are okita!", "id"->p.id, "event_id"->p.event_id, "user_id"->p.user_id, "status"->1
            ))
          }
        }
        case None => NotAcceptable(Json.obj("error"->"failed"))
      }
    }

  }

  def index = Action.async {
    participantDao.all().map {
      pp => Ok(Json.arr(pp.map(p => Json.obj(
        "id"->p.id, "event_id"->p.event_id, "user_id"->p.user_id, "status"->p.status
      ))))
    }
  }
}
