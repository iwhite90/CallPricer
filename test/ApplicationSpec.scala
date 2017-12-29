import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentAsString(home) must include ("Your new application is ready.")
    }

  }

  "CallPriceController" should {

    "charge 2p per minute for trunk calls shorter than 10 minutes" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/9.9")).get

      val response = route(app, FakeRequest(GET, "/totalCost/1")).get

      contentAsString(response) mustBe "19"
    }

    "charge 2p per minute and then apply a 10% discount for trunk calls over 10 minutes" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/10.0")).get

      val response = route(app, FakeRequest(GET, "/totalCost/1")).get

      contentAsString(response) mustBe "18"
    }

    "charge 5p per minute for long distance calls shorter than 10 minutes" in {
      route(app, FakeRequest(GET, "/addCall/1/longDistance/2.0")).get

      val response = route(app, FakeRequest(GET, "/totalCost/1")).get

      contentAsString(response) mustBe "10"
    }

    "charge 5p per minute and then apply a 10% discount for long distance calls over 10 minutes" in {
      route(app, FakeRequest(GET, "/addCall/1/longDistance/10.0")).get

      val response = route(app, FakeRequest(GET, "/totalCost/1")).get

      contentAsString(response) mustBe "45"
    }

    "charge for partial minutes, rounding down to the nearest pence" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/0.6")).get

      val response = route(app, FakeRequest(GET, "/totalCost/1")).get

      contentAsString(response) mustBe "1"
    }

    "return a statement of all charges for a customer" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/10.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/20.0")).get

      val response = route(app, FakeRequest(GET, "/costHistory/1")).get

      contentAsString(response) mustBe "18\n36"
    }

    "return a total of all charges for a customer" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/10.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/20.0")).get

      val response = route(app, FakeRequest(GET, "/totalCost/1")).get

      contentAsString(response) mustBe "54"
    }

    "identify when a customer is eligible for a prize draw" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/1.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/1.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/20.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/20.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/40.0")).get

      val response = route(app, FakeRequest(GET, "/prizeDraw/1")).get

      contentAsString(response) mustBe "Customer entered for prize draw"
    }

    "identify when a customer is not eligible for a prize draw" in {
      route(app, FakeRequest(GET, "/addCall/1/trunk/1.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/1.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/1.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/20.0")).get
      route(app, FakeRequest(GET, "/addCall/1/trunk/40.0")).get

      val response = route(app, FakeRequest(GET, "/prizeDraw/1")).get

      contentAsString(response) mustBe "Customer not eligible for prize draw"
    }
  }

}
