import sbt._
import sbt.Keys._

object ScalalabBuild extends Build with SbtUtils {

  lazy val lectures_di = project("patterns", "lectures/patterns").settings(
    Seq(libraryDependencies ++= mongo)
  )

  lazy val lectures_shapeless = project("shapeless", "lectures/shapeless")
      .settings(
        Seq(libraryDependencies ++= Seq(shapeless) ++ mongo)
      )

  lazy val lectures_fs = project("futures-and-streams", "lectures/futures-and-streams")
    .settings(
      Seq(libraryDependencies ++= akka ++ Seq(async))
    )

  lazy val lectures_actors = project("actors", "lectures/actors").settings(
    Seq(libraryDependencies ++= akka)
  )

  lazy val lectures_scalaz = project("scalaz", "lectures/scalaz").settings(
    Seq(libraryDependencies ++= Seq(scalaz))
  )

  lazy val macro_impl = project("macroimpl", "macroimpl").settings(
    Seq(libraryDependencies ++= Seq(
      "org.scalamacros" % "paradise_2.11.0" % "2.1.0-M5",
      scalazConcurrent
    ))
  ).settings(
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
  )

  lazy val lectures_macro = project("macroworld", "lectures/macroworld")
    .settings(
      Seq(libraryDependencies ++= Seq(scalazConcurrent)),
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
    ).dependsOn(macro_impl)

  lazy val main = project("scalalab3", ".").settings(
    Seq(
      name := "scalalab3",
      libraryDependencies ++= Seq()
    )
  ) dependsOn (
    lectures_di,
    lectures_shapeless,
    lectures_fs,
    lectures_actors,
    lectures_scalaz,
    lectures_macro
  )


}
