package org.scalameta.scalagen

import org.scalatest.Assertion
import scala.meta._
import scala.meta.contrib._
import scala.meta.gen.Generator

class TestCompanionExtension extends GeneratorSuite {

  def testEqual(src: Source, expected: Source, extraGenerators: Generator*): Assertion = {
    val res = generate(src, (PrintHiInCompanion +: extraGenerators):_*)
    withClue(res.syntax)(assert(expected.isEqual(res)))
  }

  test("Companion extension works with companion absent") {
    testEqual(
      source"@PrintHiInCompanion case class Foo()",
      source"""case class Foo()

               object Foo {
                 def hi = println("hi")
               }
             """)
  }

  test("Companion extension works with companion present") {
    testEqual(
      source"""@PrintHiInCompanion
               case class Foo()

               object Foo {
                 def foo = ???
               }
             """,
      source"""case class Foo()

               object Foo {
                 def foo = ???
                 def hi = println("hi")
               }
             """)
  }

  test("Companion extension works with multiple case classes") {
    testEqual(
      source"""
        object types {
          @PrintHiInCompanion case class Foo()
          @PrintHiInCompanion case class Bar()
        }
      """,
      source"""
        object types {
          case class Foo()
          object Foo {
            def hi = println("hi")
          }

          case class Bar()
          object Bar {
            def hi = println("hi")
          }
        }
      """)
  }

  test("Companion extension preserves order") {
    testEqual(
      source"""
        object types {
          @PrintHiInCompanion case class Foo()
          case class Bar()
          @PrintHiInCompanion case class Baz()
        }
      """,
      source"""
        object types {
          case class Foo()
          object Foo {
            def hi = println("hi")
          }

          case class Bar()

          case class Baz()
          object Baz {
            def hi = println("hi")
          }
        }
      """)
  }

  // test("Companion extension works with param generator") {
  //   testEqual(
  //     source"@PrintHiInCompanion case class Foo(@NonNull x: String)",
  //     source"""
  //       case class Foo(x: String) {
  //         assert(x != null)
  //       }
  //       object Foo {
  //         def hi = println("hi")
  //       }
  //     """,
  //     NonNull)
  // }

  test("Companion extension works with multiple types") {
    testEqual(
      source"""
        object types {
          @PrintHiInCompanion sealed trait TestTrait
          case object A extends TestTrait
          @PrintHiInCompanion case class B() extends TestTrait

          @PrintHiInCompanion type TestType = String

          @PrintHiInCompanion case class TestClass()

          trait TestTrait2
        }
      """,
      source"""
        object types {
          sealed trait TestTrait
          object TestTrait {
            def hi = println("hi")
          }
          case object A extends TestTrait
          case class B() extends TestTrait
          object B {
            def hi = println("hi")
          }

          type TestType = String
          object TestType {
            def hi = println("hi")
          }

          case class TestClass()
          object TestClass {
            def hi = println("hi")
          }

          trait TestTrait2
        }
      """)
  }
}
