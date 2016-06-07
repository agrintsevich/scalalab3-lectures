object Boot extends App {
  def p(x: Any) = println(x)
  implicit class AnyExt(x: Any) {
    def |>(f: Any => Unit) = f(x)
  }

  import scalaz._
  import Scalaz._

  object compostion {
    val f = (x: Int) => x + 1
    val g = (x: Int) => x * 10

    val fg = f compose g // => f(g(10))

    val gf = f andThen g // => g(f(10))

    val f1 = (x: Int) => x.toString.some

    val f2 = (x: String) => s"$x!!!".some

//    f1 compose f2

    val f1f2 = Kleisli(f1) >=> Kleisli(f2)

    val f1f2__ = (x: Int) => f1.map(x => x.flatMap(f2))(x)

    val f1f2_ = (x: Int) => f1(x) match {
      case Some(result) => f2(result)
      case None => None
    }
    // f1f2 = f1f2_
    println(f1f2__(123))
    println(f1f2(123))
  }
//  compostion

  object monadExample {
    // монадные законы + пара методов (flatMap, unit)
    // unit = constructor
    // M[_], M[_, _], M[_,_,_ ..._]
    Option(1) // apply = unit

    for {
      x <- Option(1)
      y <- Option(1)
    } yield x + y

    // x.flatMap(x1 => y.)
    case class User(name: String)

    def java_method(x: String): User = {
      if (x == "foo") {
        null
      } else {
        new User("asd")
      }
    }

    Option(java_method("foo")).getOrElse(User("default user"))

    // functor = map + functor laws
    // F[_]

    val c = (Option(1) |@| Option(2)) { (a, b) => a + b }
    println((List(1,2,3) |@| List(1)){ _ + _ })
    println(c)
    // List[Option] => Option[List]
    val xs = List(1.some, 2.some, 3.some)

    println(xs.sequenceU)

    /*
     for {
       x <- calc
       y <- calc2
       z <- calc3
     }

     |@|
     */



  }
//  monadExample

  object eitherMonad {

    val ok = \/-(1)
    val error = -\/("error")
    val error1 = "error".left

    val fe = \/.fromTryCatchNonFatal(throw new Exception("boom!"))
    println(fe)

    val d: \/[String, Int] = \/-(1)
    // val d1: Either[String. Int] = Right(1)

    ok.rightMap(x => x + 1)
    error.leftMap(x => s"$x!!!")
    ok.bimap(x => s"$x!!!", x => x + 1)
    Option(1).map(x => x + 1)

  }
//  eitherMonad

  object applicative {
    // R = model
    // Future
    // R : \/ ?
    type HttpResponse[R] = String \/ R
    type T[X] = String \/ X
    val xs: List[Option[T[Int]]] =
      List(
        1.right.some,
        2.right.some,
        none
      )

    val xs1 = xs.map(x => x.map(_.rightMap(y => y + 1)))
    println(xs1)

    val comp = Applicative[List] compose Applicative[Option] compose Applicative[T]

    val xs2 = comp(xs)(_ + 1)
    println(xs2)

  }
//  applicative

  object validation {
    case class User(name: String, age: Int)
    type Error = String \/ User

    def commonValidation(user: User): Error = {
      if (user.name.isEmpty) {
        "name is empty".left[User]
      } else {
        // check age
        user.right[String]
      }
    }

    def commonValidation1(user: User): Error = {
      val errors = scala.collection.mutable.ArrayBuffer[String]()
      if (user.name.isEmpty) {
       errors += "name is empty"
      }
      if (user.age < 16) {
        errors += "too young"
      }

      if (errors.nonEmpty) {
        errors.mkString(", ").left // String \/ Nothing / User
      } else {
        user.right
      }
    }

    def checkName(user: User): ValidationNel[String, User] = {
      if (user.name.isEmpty) {
        "is empty".failureNel[User]
      } else {
        user.successNel[String]
      }
    }

    def checkAge(user: User): ValidationNel[String, User] = {
      if (user.age < 16) {
        "age".failureNel[User]
      } else {
        user.successNel[String]
      }
    }


    def validationOnCreate(user: User) = {
      (checkName(user) |@| checkAge(user)) { (a, b) =>
        user
      }.toEither
    }

    val u = User("das", 21)
    println(validationOnCreate(u))

  }
//  validation

  object foldableExample {
    val right = 1.right[String] // String \/ Int

    val fo = Foldable[Option]
    val opt1 = 1.some
    val opt2 = none[String]

    val r = fo.foldMap(opt1)(_ + 1) // => 2
    val r1 = fo.foldMap(opt2)(_ + "!!!") // ""

    // Monoid = + bin assoc operation, 0, ""
    case class MyProduct(price: Int)
    val m = MyProduct(1)

    implicit val monoidProduct = new Monoid[MyProduct] {
      override def zero: MyProduct = MyProduct(0)

      override def append(f1: MyProduct, f2: => MyProduct) = {
        MyProduct(f1.price + f2.price)
      }
    }

//    val r2 = fo.foldMap(MyProduct(1).some)(_ |+| MyProduct(1))
    println(MyProduct(1) |+| MyProduct(2))

    val fl = Foldable[List]

    val r3 = fl.foldMap(List(MyProduct(1),
      MyProduct(2), MyProduct(3)))(m =>
        m |+| MyProduct(100)
    )

    println(r3)
  }
  foldableExample





























}
