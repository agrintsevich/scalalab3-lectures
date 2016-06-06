package macroimpl

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.{StaticAnnotation, compileTimeOnly}

// addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
@compileTimeOnly("enable macro paradise to expand macro annotations")
class ToString extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ToStringImpl.impl
}

class ToStringImpl(val c: Context) {
  import c.universe._

  /*
    annottees =>

    List(Expr[Nothing](case class Foo extends scala.Product with scala.Serializable {
      <caseaccessor> <paramaccessor> val id: Int = _;
      <caseaccessor> <paramaccessor> val name: String = _;
      <caseaccessor> <paramaccessor> val args: Option[String] = _;
      def <init>(id: Int, name: String, args: Option[String]) = {
        super.<init>();
        ()
      }
    }))

   */

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    // head!!!
    //http://docs.scala-lang.org/overviews/quasiquotes/syntax-summary
    val newClass = annottees.head.tree match {
      case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =>

        if (!mods.hasFlag(Flag.CASE)) {
          c.abort(c.enclosingPosition, "case class only")
        }
        /*
        List of ValDev
        paramss: List(List(
            <caseaccessor> <paramaccessor> val id: Int = _,
            <caseaccessor> <paramaccessor> val name: String = _,
            <caseaccessor> <paramaccessor> val args: Option[String] = _))
         */



        val parameters = paramss.flatten.map {
          // ${name.toString} = ${Ident(name)}
          // mods: Modifiers, name: TermName, tpt: Tree, rhs: Tree
          case ValDef(modifiers, name, tpt, rhs) =>
            val n = s" $name = "
            q"""
               $n + $name + "\n"
            """
        }.reduce((a, b) => q"""  $a + $b  """)
        // => "Foo[".$plus("id=").$plus(id).$plus("name =").$plus(name))

        val klassName = tpname.toTermName.toString


        val toStringBody = q"""
          override def toString = $klassName + "[" + $parameters + "]"
        """

        q"""$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends
            { ..$earlydefns } with ..$parents { $self =>
            ..$stats
            ..$toStringBody
          }
        """


      case _ =>
        c.abort(c.enclosingPosition, "case class only")
    }

    /*
      case class Foo extends scala.Product with scala.Serializable {
        <caseaccessor> <paramaccessor> val id: Int = _;
        <caseaccessor> <paramaccessor> val name: String = _;
        <caseaccessor> <paramaccessor> val args: Option[String] = _;
        def <init>(id: Int, name: String, args: Option[String]) = {
          super.<init>();
          ()
        };
        override def toString = Foo["id" = id.$bslash(n), "name" = name.$bslash(n), "args" = args.$bslash(n)]
      }

     */

    c.Expr[Any](newClass)

  }
}
