import scala.sys.process._

ThisBuild / tlBaseVersion := "0.5"
ThisBuild / tlCiReleaseBranches := Seq("main")

lazy val setupNode = WorkflowStep.Use(
  UseRef.Public("actions", "setup-node", "v3"),
  name = Some("Use Node.js"),
  params = Map("node-version" -> "20", "cache" -> "npm")
)

// https://stackoverflow.com/a/55610612
lazy val npmInstall = WorkflowStep.Run(
  List("npm install"),
  name = Some("npm install")
)

ThisBuild / tlCiMimaBinaryIssueCheck := false
ThisBuild / tlCiDocCheck := false
ThisBuild / tlCiScalafmtCheck := false
ThisBuild / tlCiHeaderCheck := false
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("21"))
ThisBuild / githubWorkflowBuildPreamble ++= Seq(setupNode, npmInstall)
ThisBuild / githubWorkflowPublishPreamble ++= Seq(setupNode, npmInstall)

ThisBuild / scalaVersion := "3.7.2"
ThisBuild / crossScalaVersions := Seq("3.7.2")

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
