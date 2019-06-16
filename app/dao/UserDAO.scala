package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile


class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)( implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  class UsersTable(tag: Tag) extends Table[User](tag, "User") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def expo_token = column[Option[String]]("expo_token")
    def name = column[Option[String]]("name")

    def * = (id.?, email, expo_token, name) <> ((User.apply _).tupled, User.unapply _)
  }

  private val Users = TableQuery[UsersTable]

  def all(): Future[Seq[User]] = db.run(Users.result)
  def findById(id: Long): Future[Option[User]] = db.run(Users.filter(_.id === id).result.headOption)

  def insert(user: User): Future[Unit] = db.run(Users += user).map { _ => () }
  def insert(users: Seq[User]): Future[Unit] = db.run(this.Users ++= users).map { _ => () }

  def update(id: Long, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(Some(id))
    db.run(Users.filter(_.id === id).update(userToUpdate).map(_ => ()))
  }

  def delete(id: Long): Future[Unit] = db.run(Users.filter(_.id === id).delete.map(_ => ()))
}