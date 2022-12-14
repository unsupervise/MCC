package BlockClustering

import Common.Tools._
import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.stats.distributions.{Gamma, RandBasis}
import breeze.stats.distributions.Rand.FixedSeed.randBasis
import breeze.storage.Zero
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.ListBuffer

class ClusteringTest extends AnyFunSuite {
  implicit val optDenseVectZero: Zero[DenseVector[Double]] = Zero(DenseVector(0.0))
  implicit val randbasis: RandBasis = RandBasis.withSeed(54321)

  val Data = List(
    List(DenseVector(1D), DenseVector(1D)),
    List(DenseVector(2D), DenseVector(1D)),
    List(DenseVector(3D), DenseVector(1D)),
    List(DenseVector(1D), DenseVector(1D)),
    List(DenseVector(2D), DenseVector(1D)),
    List(DenseVector(3D), DenseVector(1D)),
    List(DenseVector(1D), DenseVector(1D)),
    List(DenseVector(2D), DenseVector(1D)),
    List(DenseVector(3D), DenseVector(1D))
  )

  test("Default constructor") {
    {

      val clustering = new Clustering(Data, alpha = Some(10D))


      assert(clustering.n == 9)
      assert(clustering.p == 2)
      assert(clustering.d == 1)

      assert(clustering.rowPartition == List.fill(9)(0))
      assert(clustering.colPartition == List(0, 1))

      assert(clustering.countRowCluster == ListBuffer(9))
      assert(clustering.countColCluster == ListBuffer(1, 1))
      assert(clustering.NIWParamsByRow.head.head.checkNIWParameterEquals(clustering.prior.update(Data.map(_.head).toList)))
      assert(! clustering.updateAlphaFlag)
      assert(clustering.actualAlphaPrior.scale == 1D & clustering.actualAlphaPrior.shape == 1D)
      assert(clustering.actualAlpha == 10D)

    }
  }

  test("Constructor with optional arguments") {
    {

      val initRowPartition = List(0,0,0,0,1,1,1,1,1)
      val initColPartition = List(0,1)
      val clustering = new Clustering(Data, alphaPrior = Some(Gamma(1D,2D)(randbasis)),
        initByUserRowPartition = Some(initRowPartition),
        initByUserColPartition = Some(initColPartition))

      assert(clustering.actualAlphaPrior == Gamma(1D,2D)(randbasis))
      assert(clustering.actualAlpha == 2D)
      assert(clustering.rowPartition == initRowPartition)
      assert(clustering.colPartition == initColPartition)

      assert(clustering.NIWParamsByRow.length == 2)
      assert(clustering.NIWParamsByRow.head.length == 2)

      val redundantAlphaArgumentsError = intercept[IllegalArgumentException] {
        new Clustering(Data, alpha = Some(1D), alphaPrior = Some(Gamma(1D,2D)(randbasis)))
      }
      assert(redundantAlphaArgumentsError.getMessage == "requirement failed: Providing both alphaRow or alphaRowPrior is not supported: remove one of the two parameters.")

    }
  }

//  test("Functional test") {
//    {
//      val modes = List(DenseVector(1.9, 1.9),DenseVector(1.4, -1.4),DenseVector(-1.7, 1.7),DenseVector(-1.7, -1.7))
//
//      val covariances = List(
//        DenseMatrix(0.25, 0.2, 0.2, 0.25).reshape(2,2),
//        DenseMatrix(0.17, 0.1, 0.1, 0.17).reshape(2,2),
//        DenseMatrix(0.12, 0.0, 0.0, 0.12).reshape(2,2),
//        DenseMatrix(0.3, 0.0, 0.0, 0.3).reshape(2,2))
//
//      val data = Common.DataGeneration.randomMixture(modes, covariances, List(20, 20, 20, 20)).map(List(_))
//
//      val clustering = new Clustering(data, alpha = Some(10D))
//
//      val resultClustering = clustering.run(100)
//
//      resultClustering._1.foreach(println)
//    }
//  }

}
