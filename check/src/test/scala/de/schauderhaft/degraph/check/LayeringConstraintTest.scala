package de.schauderhaft.degraph.check

import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import de.schauderhaft.degraph.model.SimpleNode
import ConstraintViolationTestUtil._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LayeringConstraintTest extends FunSuite {
  val c = LayeringConstraint("t", IndexedSeq(LenientLayer("a"), LenientLayer("b"), LenientLayer("c")))

  test("simple violation free graph returns empty Set of violations") {
    c.violations(MockSliceSource("t", "a" -> "b", "b" -> "c")) should be(Set())
  }

  test("it's ok to skip layers") {
    c.violations(MockSliceSource("t", "a" -> "c")) should be(Set())
  }

  test("reverse dependency is reported as a violation") {
    dependenciesIn(c.violations(MockSliceSource("t", "b" -> "a"))) should
      be(Set((SimpleNode("t", "b"), SimpleNode("t", "a"))))
  }
  test("dependencies in other layers are ignored") {
    c.violations(MockSliceSource("x", "b" -> "a")) should be(Set())
  }

  test("dependency to unknown is ok when from last") {
    c.violations(MockSliceSource("t", "c" -> "x")) should be(Set())
  }

  test("dependency from unknown is ok when to first") {
    c.violations(MockSliceSource("t", "x" -> "a")) should be(Set())
  }
  test("dependency to unknown is ok when from middle") {
    c.violations(MockSliceSource("t", "b" -> "x")) should be(Set())
  }

  test("dependency from unknown is ok when to middle") {
    c.violations(MockSliceSource("t", "x" -> "b")) should be(Set())
  }

  test("shortString with single element layers") {
    LayeringConstraint("type", IndexedSeq(StrictLayer("a"), LenientLayer("b"))).
      shortString should be("a -> b")
  }
  test("shortString with multi element layers") {
    LayeringConstraint("type", IndexedSeq(StrictLayer("a", "x"), LenientLayer("b", "y"))).
      shortString should be("[a, x] -> (b, y)")
  }
}
