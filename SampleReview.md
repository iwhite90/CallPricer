CallPricer issues

- There is a logic bug due to rounding errors. The sum of all the costs in a cost history may not add up to the total cost.
- The application isn't comprehensively tested. There should be tests for edge cases which would have picked up the logic bug.
- There are too many acceptance tests. A lot of the logic being tested should be extracted out into unit tests instead.
- The ApplicationSpec class is testing bad requests, the index page, and the CallPriceController. It could be split into more cohesive test classes.
- The CallPriceController is doing too much. The controller should only be concerned with handling requests and responses, and should delegate business logic to other classes.
- The application state is being stored in memory. It should probably be persisted somewhere in case of application restarts.
- There is no dependency injection, which makes dependencies tightly coupled and hard to test in isolation.
- The REST API is not idiomatic. The addCall route should be a PUT, not a GET.
- Passing the CustomerCallDetails class around and mutating its state. It is better to keep things immutable when possible.
- Unnecessary use of Option in CustomerCallDetails just complicates the code.
- isCustomerEligibleForPrizeDraw method actually does two things: checks whether the customer is eligible, and if so then adds the customer to the prize draw. Either rename the method to describe what it actually does, or better yet split it into separate methods.
- Calling callCosts(custId) will blow up if the custId key is not in the map.
- There is a pointless for comprehension in isCustomerEligibleForPrizeDraw.
- numCalls, numOverTwentyPence and numOverFiftyPence return Option[Int] which is not used for anything.
- validForPrizeDraw logic probably shouldn't belong to the CustomerCallDetails class, thinking about the Single Responsibility Principle.
- Clunky use of match expression in prizeDraw method. More readble just to use if...else.
- Mixing levels of abstraction within blocks of code. For instance, "if (customerCallDetails.validForPrizeDraw)" (high level of abstraction), "prizeDrawCustomers = custId :: prizeDrawCustomers" (low level of abstraction).
- Code duplication in numCalls, numOverTwentyPence and numOverFiftyPence.
- Code duplication in trunkPriceFunction and longDistancePriceFunction.
- Magic numbers in trunkPriceFunction and longDistancePriceFunction. Make these well named constants.
- Short and undescriptive variable names in PricingUtils.
- Could simplfy xs.filter(_ > ?).length to xs.count(_ > ?).
- addCall method is hard to understand. It could be refactored into steps at a higher level of abstraction to make it more human readable. Also it is using two different mechanisms to add to a map (put and +=). It would be cleaner to be consistent and just use one approach.
- General untidiness, such as leaving the auto-generated comments in the ApplicationSpec class, unused jodatime import, etc.


To solve some of these issues, you could:

- Create a repository class to manage application state, and inject it where needed.
- Create a service layer to handle business logic, and inject it into the controller.
- Write unit tests against these smaller cohesive classes, and keep acceptance tests for checking that these classes are working together properly.
- Write error handling around code that can currently throw exceptions, such as getting values from maps, or write it in a way that doesn't require error handling.
- General refactoring to improve code quality and more idiomatic use of Scala.


If you could only fix one thing...
- The logic bug due to rounding errors, as it will cause confusion and complaints from customers.
