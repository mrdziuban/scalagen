package scala.meta.gen.implicits

import scala.meta._

trait Params {
  implicit class XtensionParams[A <: Tree](t: A) {
    def modParamss(f: List[List[Term.Param]] => List[List[Term.Param]]): A =
      (t match {
        case d: Decl.Def => d.copy(paramss = f(d.paramss))
        case c: Defn.Class => c.copy(ctor = c.ctor.modParamss(f))
        case d: Defn.Def => d.copy(paramss = f(d.paramss))
        case m: Defn.Macro => m.copy(paramss = f(m.paramss))
        case c: Ctor.Primary => c.copy(paramss = f(c.paramss))
        case c: Ctor.Secondary => c.copy(paramss = f(c.paramss))
        case t: Type.Method => t.copy(paramss = f(t.paramss))
        case _ => t
      }).asInstanceOf[A]

    def replaceParam(p: Term.Param): A =
      modParamss(_.map(_.map(_ match {
        case Term.Param(_, Term.Name(name), _, _) if name == p.name.value => p
        case x => x
      })))
  }
}
