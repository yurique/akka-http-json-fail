name := "circe-akka-http-tests"
scalaVersion := "2.12.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaHttpV   = "10.1.0"
  val akkaHttpCirceV = "1.20.0"
  val circeVersion = "0.9.2"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV,
    "org.scalatest"     %% "scalatest" % "3.0.5" % "it,test"
  ) ++ Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

}
