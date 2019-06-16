package dao

import java.sql.{Date, Timestamp}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.Event
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}


class EventDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)( implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  implicit val jodatimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    { jodatime => new Timestamp(jodatime.getMillis) },
    { sqltime => new DateTime(sqltime.getTime) }
  )
  class EventsTable(tag: Tag) extends Table[Event](tag, "event") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def content = column[Option[String]]("content")
    def date = column[Date]("date")
    def start_time = column[DateTime]("start_time")
    def end_time = column[DateTime]("end_time")
    def owner_id = column[Long]("owner_id")

    def * = (id.?, title, content, date, start_time, end_time, owner_id) <> (Event.tupled, Event.unapply)
  }

  private val Events = TableQuery[EventsTable]

  def all(): Future[Seq[Event]] = db.run(Events.result)
  def findById(id: Long): Future[Option[Event]] = db.run(Events.filter(_.id === id).result.headOption)
  def getLatestByOwner(owner_id: Long): Future[Option[Event]] = db.run(Events.filter(_.owner_id === owner_id).sortBy(_.id.desc).take(1).result.headOption)

  def insert(event: Event): Future[Unit] = db.run(Events += event).map { _ => () }

  def delete(id: Long): Future[Unit] = db.run(Events.filter(_.id === id).delete.map(_ => ()))
}
