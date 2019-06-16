package controllers

import javax.inject._
import play.api.libs.json.{Format, JsPath, Json}
import play.api.libs.functional.syntax._
import play.api.mvc._
import models.User
import dao.UserDAO

import scala.concurrent.{ExecutionContext, Future, Promise}

case class UserForm(email: String, expo_token: Option[String], name: Option[String])
object UserForm {
  implicit val format = Json.format[UserForm]
}

@Singleton
class UserController @Inject()(cc: ControllerComponents, userDao: UserDAO)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def create = Action.async(parse.json) { implicit request =>
    val userForm = request.body.as[UserForm]
    val newUser: User = models.User.apply(None, userForm.email, userForm.expo_token, userForm.name)
    userDao.insert(newUser).map(_ => Ok(Json.obj("email" -> newUser.email, "name" -> newUser.name)))
  }

  def index = Action.async {
    userDao.all().map {
      users => Ok(Json.arr(users.map(u => Json.obj(
        "email" -> u.email, "name" -> u.name
      ))))
    }
  }
}
