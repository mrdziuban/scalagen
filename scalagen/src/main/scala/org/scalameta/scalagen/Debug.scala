package org.scalameta.scalagen

import scala.meta._

object debug {
  private case class PrettyPrinter(level: Int, inQuotes: Boolean, backslashed: Boolean) {
    val indent = List.fill(level)("  ").mkString

    def transform(char: Char): (PrettyPrinter, String) = {
      val (pp, f): (PrettyPrinter, PrettyPrinter => String) = char match {
        case '"' if inQuotes && !backslashed => (copy(inQuotes = false), _ => s"$char")
        case '"' if !inQuotes => (copy(inQuotes = true), _ => s"$char")
        case '\\' if inQuotes && !backslashed => (copy(backslashed = true), _ => s"$char")

        case ',' if !inQuotes => (this, p => s",\n${p.indent}")
        case '(' if !inQuotes => (copy(level = level + 1), p => s"(\n${p.indent}")
        case ')' if !inQuotes => (copy(level = level - 1), p => s"\n${p.indent})")
        case _ => (this, _ => s"$char")
      }
      (pp, f(pp))
    }
  }

  private def prettyPrint(raw: String): String =
    raw.foldLeft((PrettyPrinter(0, false, false), new StringBuilder(""))) { case ((pp, sb), char) =>
      val (newPP, res) = pp.transform(char)
      (newPP, sb.append(res))
    }._2.toString.replaceAll("""\(\s+\)""", "()")

  private def showTree(tree: Tree, pretty: Boolean): String =
    if (pretty) prettyPrint(tree.structure) else tree.structure

  // best way to inspect a tree, just call this
  def apply(name: String, tree: Tree, pretty: Boolean = true): Unit =
    println(s"====\n$name ${tree.pos}:\n${(new Throwable).getStackTrace.mkString("\n")}\n${tree.syntax}\n${showTree(tree, pretty)}")
}
