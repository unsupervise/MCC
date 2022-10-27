package Common


import Common.Tools._
import breeze.linalg.{DenseMatrix, DenseVector, inv}
import breeze.numerics.log
import breeze.stats.distributions.MultivariateGaussian
import breeze.storage.Zero
import org.scalatest.funsuite.AnyFunSuite

class ToolsTest extends AnyFunSuite {
  test("log factorial") {
    {
      assertResult(logFactorial(5D))(log(factorial(5D)))
    }
  }
}