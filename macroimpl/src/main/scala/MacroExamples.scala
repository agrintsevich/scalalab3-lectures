package macroimpl

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context



object MacroExamples {
  def simpleMacro(x: Int): Int = macro MacroExamplesImpl.simpleMacroImpl

  def getParamName: Int = macro MacroExamplesImpl.getParamNameImpl

  def getConstructorParams[T]: List[String] =
    macro MacroExamplesImpl.getConstructorParamsImpl[T]


}

class MacroExamplesImpl(val c: Context) {
  import c.universe._

  def simpleMacroImpl(x: c.Expr[Int]): c.Expr[Int] = {

    println(x)
    println(show(x))
    println(showRaw(x))

    c.Expr[Int](q"$x")
  }

  def getParamNameImpl: c.Expr[Int] = {
    // lexical context
    val owner = c.internal.enclosingOwner
    println(owner)

    if (!owner.isMethod) {
      c.error(c.enclosingPosition, "Method only")
    }

    val paramName = owner.asMethod.paramLists.flatten.head.asTerm.name.toString


    println(paramName)


    c.Expr[Int](q"10")
  }

  def getConstructorParamsImpl[T : c.WeakTypeTag]: c.Expr[List[String]] = {

    val symbol = c.symbolOf[T] // TypeSymbol

    if (!(symbol.isClass && symbol.asClass.isCaseClass)) {
      c.error(c.enclosingPosition, "case class only")
    }

    val list = c.weakTypeOf[T].decl(termNames.CONSTRUCTOR).alternatives.collect {
      case m: MethodSymbol => m.paramLists.flatten
    }.flatten
      .map(_.name.toString)


    c.Expr[List[String]](q"$list")
  }


}





