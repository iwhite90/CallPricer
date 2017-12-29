package utils

object PricingUtils {

  def getPrice(callType: String, minutes: Double): Double = {
    callType match {
      case "trunk" => getPrice(trunkPriceFunction, minutes)
      case "longDistance" => getPrice(longDistancePriceFunction, minutes)
    }
  }

  def getPrice(f: Double => Double, l: Double) = {
    f(l)
  }

  /*
   * Trunk calls cost 2p per minute. Calls of at least 10 minutes get a 10% discount.
   */
  val trunkPriceFunction = (l: Double) => {
    if (l < 10) {
      l * 2
    } else {
      l * 2 * 0.9
    }
  }

  /*
   * Long distance calls cost 5p per minute. Calls of at least 10 minutes get a 10% discount.
   */
  val longDistancePriceFunction = (l: Double) => {
    if (l < 10) {
      l * 5
    } else {
      l * 5 * 0.9
    }
  }
}
