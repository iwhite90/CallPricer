package controllers

import javax.inject.{Inject, Singleton}

import model.CustomerCallDetails
import org.joda.time.{DateTime, Seconds}
import play.api.mvc.{Action, Controller}
import utils.PricingUtils

@Singleton
class CallPriceController @Inject() extends Controller {

  val callCosts: scala.collection.mutable.Map[Int, Seq[Double]] = scala.collection.mutable.Map[Int, Seq[Double]]()

  def addCall(custId: Int, callType: String, length: Double) = Action {
    if (callCosts.get(custId).isDefined) {
      callCosts.put(custId, callCosts.get(custId).get :+ PricingUtils.getPrice(callType, length))
    } else {
      callCosts += custId -> List(PricingUtils.getPrice(callType, length))
    }

    Ok
  }

  def getCostHistory(custId: Int) = Action {
    Ok(callCosts(custId).map(_.toInt).mkString("\n"))
  }

  def getTotalCost(custId: Int) = Action {
    Ok(callCosts(custId).foldLeft(0.0)(_ + _).toInt.toString)
  }

  def prizeDraw(custId: Int) = Action {
    isCustomerEligibleForPrizeDraw(custId) match {
      case true => Ok("Customer entered for prize draw")
      case _ => Ok("Customer not eligible for prize draw")
    }
  }

  var prizeDrawCustomers: List[Int] = List[Int]()

  private def isCustomerEligibleForPrizeDraw(custId: Int): Boolean = {
    // At least 5 calls
    // At least 3 calls > 20p
    // At least 1 call > 50p

    val xs = callCosts(custId)
    val customerCallDetails = CustomerCallDetails()

    for {
      _ <- numCalls(xs, customerCallDetails)
      _ <- numOverTwentyPence(xs, customerCallDetails)
      _ <- numOverFiftyPence(xs, customerCallDetails)
    } yield customerCallDetails

    if (customerCallDetails.validForPrizeDraw) {
      prizeDrawCustomers = custId :: prizeDrawCustomers
      return true
    }
    false
  }

  private def numCalls(xs: Seq[Double], customerCallDetails: CustomerCallDetails): Option[Int] = {
    val numCalls = xs.length
    customerCallDetails.numCalls = Some(numCalls)
    Some(numCalls)
  }

  private def numOverTwentyPence(xs: Seq[Double], customerCallDetails: CustomerCallDetails): Option[Int] = {
    val numCallsGreaterThanTwentyPence = xs.filter(_ > 20).length
    customerCallDetails.numCallsGreaterThanTwentyPence = Some(numCallsGreaterThanTwentyPence)
    Some(numCallsGreaterThanTwentyPence)
  }

  private def numOverFiftyPence(xs: Seq[Double], customerCallDetails: CustomerCallDetails): Option[Int] = {
    val numCallsGreaterThanFiftyPence = xs.filter(_ > 50).length
    customerCallDetails.numCallsGreaterThanFiftyPence = Some(numCallsGreaterThanFiftyPence)
    Some(numCallsGreaterThanFiftyPence)
  }

}
