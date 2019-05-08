package scala.meta.gen

import scala.tools.nsc.{Global, Phase}
import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.TypingTransformers

abstract class ScalagenCompilerPlugin(override val global: Global) extends Plugin {
  import global._

  override lazy val description: String = s"Generates code with generators: ${generators.mkString(", ")}"

  // scalagen generators to run
  def generators: List[Generator]

  // Use to create code that shortcuts in ENSIME and ScalaIDE
  def isIde: Boolean      = global.isInstanceOf[tools.nsc.interactive.Global]
  def isScaladoc: Boolean = global.isInstanceOf[tools.nsc.doc.ScaladocGlobal]

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
    if (pretty) prettyPrint(showRaw(tree)) else showRaw(tree)

  def debug(name: String, tree: Tree, pretty: Boolean = true): Unit =
    println(s"====\n$name ${tree.id} ${tree.pos}:\n${showCode(tree)}\n${showTree(tree, pretty)}")

  private def phase = new PluginComponent with TypingTransformers {
    override val phaseName: String = ScalagenCompilerPlugin.this.name
    override val global: ScalagenCompilerPlugin.this.global.type = ScalagenCompilerPlugin.this.global
    override final def newPhase(prev: Phase): Phase = new StdPhase(prev) {
      override def apply(unit: CompilationUnit): Unit =
        newTransformer(unit).transformUnit(unit)
    }
    override val runsAfter: List[String]  = "parser" :: Nil
    override val runsBefore: List[String] = "namer" :: Nil

    private def newTransformer(unit: CompilationUnit) =
      new TypingTransformer(unit) {
        override def transform(tree: Tree): Tree =
          run(super.transform(tree))
      }

    private def run(tree: Tree): Tree = {
      debug("tree", tree)
      tree
    }
  }

  override lazy val components: List[PluginComponent] = List(phase)
}

