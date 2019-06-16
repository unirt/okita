package dao

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.Participant

import scala.concurrent.{ExecutionContext, Future}

class ParticipantDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)( implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  class ParticipantsTable(tag: Tag) extends Table[Participant](tag, "participant") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def status = column[Int]("status")
    def event_id = column[Long]("event_id")
    def user_id = column[Long]("user_id")

    def * = (id.?, status, event_id, user_id) <> (Participant.tupled, Participant.unapply)
  }

  private val Participants = TableQuery[ParticipantsTable]

  def all(): Future[Seq[Participant]] = db.run(Participants.result)
  def findByEventId(event_id: Long): Future[Seq[Participant]] = {
    db.run(Participants.filter(_.event_id === event_id).result)
  }
  def findByUserId(user_id: Long): Future[Seq[Participant]] = {
    db.run(Participants.filter(_.user_id === user_id).result)
  }
  def findByEventAndUser(event_id: Long, user_id: Long): Future[Option[Participant]] = {
    db.run(Participants.filter(p => p.event_id === event_id && p.user_id === user_id).result.headOption)
  }

  def insert(participant: Participant): Future[Unit] = db.run(Participants += participant).map { _ => () }
  def insert(participants: Seq[Participant]): Future[Unit] = db.run(Participants ++= participants).map { _ => ()}

  def updateStatus(id: Long, status: Int): Future[Unit] = {
    db.run(Participants.filter(_.id === id).map(p => p.status).update(status).map(_ => ()))
  }

  def delete(id: Long): Future[Unit] = db.run(Participants.filter(_.id === id).delete.map(_ => ()))
}
