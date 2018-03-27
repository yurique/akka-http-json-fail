package example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.{ErrorAccumulatingCirceSupport, FailFastCirceSupport}
import io.circe.{Decoder, Encoder}
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.server._

class CirceHttpSupportTest extends FlatSpec
  with Matchers
  with ScalatestRouteTest
  with Directives {

  it should "reject non-validated request with accumulating support" in new ErrorAccumulatingCirceSupport {
    val route: Route = (pathEndOrSingleSlash & post & entity(as[Test])) { e => complete(e) }
    Post("/", Test("bbbbb")) ~> route ~> check {
      rejection shouldBe a[MalformedRequestContentRejection]
    }
  }

  it should "accept validated request with accumulating support" in new ErrorAccumulatingCirceSupport {
    val route: Route = (pathEndOrSingleSlash & post & entity(as[Test])) { e => complete(e) }
    Post("/", Test("bbb")) ~> route ~> check {
      status shouldBe StatusCodes.OK
    }
  }

  it should "reject non-validated request with fail-fast support" in new FailFastCirceSupport {
    val route: Route = (pathEndOrSingleSlash & post & entity(as[Test])) { e => complete(e) }
    Post("/", Test("bbbbb")) ~> route ~> check {
      rejection shouldBe a[MalformedRequestContentRejection]
    }
  }

  it should "accept validated request with fail-fast support" in new FailFastCirceSupport {
    val route: Route = (pathEndOrSingleSlash & post & entity(as[Test])) { e => complete(e) }
    Post("/", Test("bbb")) ~> route ~> check {
      status shouldBe StatusCodes.OK
    }
  }

}

final case class Test(a: String)


object Test {

  import io.circe.generic.semiauto._

  implicit val decodeTest: Decoder[Test] = deriveDecoder[Test].validate(
    _.downField("a").focus.flatMap(_.asString).exists(_.length == 3),
    "a.length must == 3"
  )

  implicit val encodeTest: Encoder[Test] = deriveEncoder[Test]

}
