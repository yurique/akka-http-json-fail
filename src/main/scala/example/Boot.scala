package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce._
import io.circe.generic.auto._

object Boot extends App with Directives {

  implicit val system = ActorSystem("test")
  import system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout: Timeout = 10.seconds

  new ErrorAccumulatingCirceSupport {

    val host = "0.0.0.0"
    val port = 9991

    Http().bindAndHandle(route, host, port)

    def route: Route =
      (pathEndOrSingleSlash & post & entity(as[TestEntity])) { e =>
        complete(e)
      }

  }

  new FailFastCirceSupport {

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
