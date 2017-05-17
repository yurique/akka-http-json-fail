package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directive0, Directive1, Directives, Route}
import akka.http.scaladsl.server.directives.{CacheConditionDirectives, Credentials}
import akka.http.scaladsl.server.directives.Credentials.{Missing, Provided}
import de.heikoseeberger.akkahttpcirce.{BaseCirceSupport, ErrorAccumulatingUnmarshaller, FailFastUnmarshaller}
import io.circe.Printer
import io.circe.generic.auto._

object Boot extends App with Directives {

  implicit val system = ActorSystem("test")
  import system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout: Timeout = 10.seconds

  new AkkaHttpCirceSupportAccumulating {

    val host = "0.0.0.0"
    val port = 9991

    Http().bindAndHandle(route, host, port)

    def route: Route =
      (pathEndOrSingleSlash & post & entity(as[TestEntity])) { e =>
        complete(e)
      }

  }

  new AkkaHttpCirceSupportFailFast {

    val host = "0.0.0.0"
    val port = 9992

    Http().bindAndHandle(route, host, port)

    def route: Route =
      (pathEndOrSingleSlash & post & entity(as[TestEntity])) { e =>
        complete(e)
      }

  }


}


case class TestEntity(
  field1: String,
  field2: String
)


trait DropNullKeysPrinter { this: BaseCirceSupport =>

  implicit val printer: Printer = Printer.noSpaces.copy(dropNullKeys = true)

}

trait AkkaHttpCirceSupportAccumulating extends BaseCirceSupport
  with DropNullKeysPrinter
  with ErrorAccumulatingUnmarshaller

trait AkkaHttpCirceSupportFailFast extends BaseCirceSupport
  with DropNullKeysPrinter
  with FailFastUnmarshaller
