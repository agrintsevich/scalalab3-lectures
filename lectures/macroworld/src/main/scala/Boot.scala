
import macroimpl._

object Boot extends App {
//  println(MacroExamples.simpleMacro(10))
  /*
    println = show
    Expr[Nothing](10)
    Expr[Nothing](10)
    Expr(Literal(Constant(10)))
   */

//  def x(z123: Int): Int = MacroExamples.getParamName
  // method x
  // => 123

  //  println(MacroExamples.getConstructorParams[Foo]) // List(id, name, args)

  @ToString
  case class Foo(id: Int, name: String, args: Option[String])

  /*
  println(Foo(1, "some name", None))
    =>
    Foo[ id = 1
     name = some name
     args = None
    ]

  */

  import Omp._

  val z = (1 to 1000).toVector

  val x = omp {
    z.filter(x => x > 1)
  }

  /*
  Task
    .gatherUnordered(z.grouped(10)
      .map(seq =>
        Task {
          seq.filter(x => x > 1)
        }).toSeq)
      .unsafePerformSync.flatten.to[Vector]
   */


  





}
