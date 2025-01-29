import scala.sys.process._

ThisBuild / tlBaseVersion := "0.3"
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / organization := "edu.gemini"
ThisBuild / organizationName := "Association of Universities for Research in Astronomy, Inc. (AURA)"
ThisBuild / licenses += ((
  "BSD-3-Clause",
  new URL("https://opensource.org/licenses/BSD-3-Clause")
))
ThisBuild / homepage := Some(
  url("https://github.com/gemini-hlsw/lucuma-primereact-designer")
)
ThisBuild / developers := List(
  Developer(
    "cquiroz",
    "Carlos Quiroz",
    "cquiroz@gemini.edu",
    url("http://www.gemini.edu")
  ),
  Developer(
    "jluhrs",
    "Javier Lührs",
    "jluhrs@gemini.edu",
    url("http://www.gemini.edu")
  ),
  Developer(
    "sraaphorst",
    "Sebastian Raaphorst",
    "sraaphorst@gemini.edu",
    url("http://www.gemini.edu")
  ),
  Developer(
    "swalker2m",
    "Shane Walker",
    "swalker@gemini.edu",
    url("http://www.gemini.edu")
  ),
  Developer(
    "tpolecat",
    "Rob Norris",
    "rnorris@gemini.edu",
    url("http://www.tpolecat.org")
  ),
  Developer(
    "rpiaggio",
    "Raúl Piaggio",
    "rpiaggio@gemini.edu",
    url("http://www.gemini.edu")
  ),
  Developer(
    "toddburnside",
    "Todd Burnside",
    "tburnside@gemini.edu",
    url("http://www.gemini.edu")
  )
)

lazy val setupNode = WorkflowStep.Use(
  UseRef.Public("actions", "setup-node", "v3"),
  name = Some("Use Node.js"),
  params = Map("node-version" -> "16", "cache" -> "npm")
)

// https://stackoverflow.com/a/55610612
lazy val npmInstall = WorkflowStep.Run(
  List("npm install"),
  name = Some("npm install")
)

ThisBuild / tlCiMimaBinaryIssueCheck := false
ThisBuild / tlCiDocCheck := false
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))
ThisBuild / githubWorkflowBuildPreamble ++= Seq(setupNode, npmInstall)
ThisBuild / githubWorkflowPublishPreamble ++= Seq(setupNode, npmInstall)

ThisBuild / scalaVersion := "3.3.5"
ThisBuild / crossScalaVersions := Seq("3.3.5")

Global / onChangedBuildSource := ReloadOnSourceChanges

enablePlugins(NoPublishPlugin)

lazy val generateStyles = taskKey[Seq[File]]("Generate prime-react styles")

lazy val publish = project
  .in(file("publish"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "lucuma-prime-styles",
    generateStyles := {
      val log = streams.value.log
      val baseDir = (Compile / resourceManaged).value / "lucuma-css"
      (s"./compile-lucuma-themes.bash $baseDir" lineStream).toList.map { line =>
        val parts = line.split("/")
        val file = baseDir / parts.last
        log.info(s"Generated ${file}")
        file
      }
    },
    Compile / resourceGenerators += generateStyles
  )
