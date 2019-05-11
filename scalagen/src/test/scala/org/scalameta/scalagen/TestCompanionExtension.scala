package org.scalameta.scalagen

import scala.meta._
import scala.meta.contrib._

class TestCompanionExtension extends GeneratorSuite {

  test("Companion extension works with companion absent") {
    val src: Source = source"@PrintHiInCompanion case class Foo()"

    val expected: Source =
      source"""case class Foo()

               object Foo {
                 def hi = println("hi")
               }
             """

    val res = generate(src, PrintHiInCompanion)

    withClue(res.syntax) {
      assert(expected isEqual res)
    }
  }

  test("Companion extension works with multiple case classes") {
    val src = source"""
      object types {
        @PrintHiInCompanion case class Foo()
        @PrintHiInCompanion case class Bar()
      }
    """

    val expected =
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
      """

    val res = generate(src, PrintHiInCompanion)

    withClue(res.syntax) {
      assert(expected isEqual res)
    }
  }

  test("Companion extension preserves order") {
    val src = source"""
      object types {
        @PrintHiInCompanion case class Foo()
        case class Bar()
        @PrintHiInCompanion case class Baz()
      }
    """

    val expected =
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
      """

    val res = generate(src, PrintHiInCompanion)

    withClue(res.syntax) {
      assert(expected isEqual res)
    }
  }

  test("Companion extension works with companion present") {
    val src: Source =
      source"""@PrintHiInCompanion
               case class Foo()

               object Foo {
                 def foo = ???
               }
             """

    val expected: Source =
      source"""case class Foo()

               object Foo {
                 def foo = ???
                 def hi = println("hi")
               }
             """

    val res = generate(src, PrintHiInCompanion)

    withClue(res.syntax) {
      assert(expected isEqual res)
    }
  }
}
