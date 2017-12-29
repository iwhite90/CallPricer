package model

case class CustomerCallDetails(var numCalls: Option[Int] = None,
                               var numCallsGreaterThanTwentyPence: Option[Int] = None,
                               var numCallsGreaterThanFiftyPence: Option[Int] = None) {

  def validForPrizeDraw = {
    numCalls.getOrElse(0) >= 5 &&
      numCallsGreaterThanTwentyPence.getOrElse(0) >= 3 &&
      numCallsGreaterThanFiftyPence.getOrElse(0) >= 1
  }
}
