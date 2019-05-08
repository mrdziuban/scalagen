package scala.meta.gen

import scala.meta._
import scala.tools.nsc.Global

class PrintHiPlugin(override val global: Global) extends ScalagenCompilerPlugin(global) {
  println(s"FUCK IM HERE")

  object PrintHiInCompanion extends CompanionGenerator("PrintHiInCompanion") {
    override def extendCompanion(c: Defn.Class): List[Stat] = {
      val hi: Lit.String = Lit.String("hi")
      val hiMethod: Defn.Def = q"def hi = println($hi)"

      hiMethod :: Nil
    }
  }

  val name: String = "PrintHiPlugin"
  def generators: List[Generator] = List(PrintHiInCompanion)
}
