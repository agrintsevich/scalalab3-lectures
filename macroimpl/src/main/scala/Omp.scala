package macroimpl

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context


object Omp {
  def omp[T](comp: T): T = macro OmpImpl.impl[T]
}

object OmpImpl {
  def impl[T: c.WeakTypeTag](c: Context)(comp: c.Expr[T]): c.Tree = {
    import c.universe._

    val result = comp.tree match {
      // z.filter(func)
      // List(List(((x: Int) => x.>(1))))
      case  q"$expr.$tname(...$exprss)" =>
        q"""
          scalaz.concurrent.Task.gatherUnordered($expr.grouped(10)
          .map(seq => scalaz.concurrent.Task { seq.$tname(...$exprss) }).toSeq)
          .unsafePerformSync.flatten.to[${c.symbolOf[T]}]
        """

      case _ =>
        c.abort(c.enclosingPosition, s"cannot to optimise ${comp.tree}")
    }

    result
  }
}
