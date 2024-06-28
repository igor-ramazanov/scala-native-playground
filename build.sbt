import _root_.bindgen.interface.Binding
import _root_.bindgen.interface.Flavour
import _root_.bindgen.plugin.BindgenMode
import scala.scalanative.build._

lazy val versions = new {
  val scala = "3.5.0-RC2"
}

def addCommandsAlias(name: String, commands: List[String]) = addCommandAlias(name, commands.mkString(";"))

addCommandsAlias(
  "dependencyCheck",
  List(
    "reload plugins",
    "dependencyUpdates",
    "reload return",
    "dependencyUpdates",
    "undeclaredCompileDependencies",
    "unusedCompileDependencies",
  ),
)

addCommandsAlias(
  "validate",
  List(
    "scalafmtSbtCheck",
    "scalafmtCheckAll",
    "scalafixAll --check",
    "undeclaredCompileDependenciesTest",
    "unusedCompileDependenciesTest",
    "test",
  ),
)

addCommandsAlias("massage", List("scalafixAll", "scalafmtSbt", "scalafmtAll", "Test/compile"))

lazy val dirs = new {
  val bin      = file(sys.env("DEVSHELL_DIR")) / "bin"
  val includes = file(sys.env("C_INCLUDE_PATH"))
}

lazy val magickWand = new {
  private def config(arg: String): Seq[String] = {
    import scala.sys.process._
    val commands = List("MagickWand-config", arg)
    val process  = Process(commands)
    val output   = process.!!.trim()
    val result   = output.split(' ')
    result.toSeq
  }

  val cFlags: Seq[String]  = config("--cflags")
  val ldFlags: Seq[String] = config("--ldflags")
  val header: File         = dirs.includes / "ImageMagick" / "MagickWand" / "MagickWand.h"
}

lazy val commonSettings = List(
  version           := "1.0.0",
  organization      := "tech.igorramazanov.scalanativeplayground",
  scalacOptions     := List(
    "-deprecation",
    "-feature",
    "-java-output-version",
    "22",
    "-new-syntax",
    "-rewrite",
    "-unchecked",
    "-Wconf:src=src_managed/.*:silent",
    "-Werror",
    "-Wnonunit-statement",
    "-Wsafe-init",
    "-Wunused:all",
    "-Wvalue-discard",
  ),
  scalafixOnCompile := true,
  scalaVersion      := versions.scala,
  semanticdbEnabled := true,
  bspEnabled        := true,
  Compile / fork    := true,
  logLevel          := Level.Info,
  bindgenBinary     := dirs.bin / "bindgen",
  bindgenClangPath  := (dirs.bin / "clang").toPath(),
  bindgenFlavour    := Flavour.ScalaNative05,
  bindgenMode       := BindgenMode.ResourceGenerator,
  nativeConfig ~=
    (config =>
      config
        .withBuildTarget(BuildTarget.application)
        .withCheckFatalWarnings(true)
        .withCheckFeatures(true)
        .withCheck(true)
        .withClang((dirs.bin / "clang").toPath())
        .withClangPP((dirs.bin / "clang++").toPath())
        .withCompileOptions(config.compileOptions)
        .withEmbedResources(false)
        .withGC(GC.commix)
        .withIncrementalCompilation(true)
        .withLinkingOptions(config.linkingOptions)
        .withLinkStubs(true)
        .withLTO(LTO.thin)
        .withMode(Mode.releaseFast)
        .withMultithreading(true)
        .withOptimize(true)
    ),
)

lazy val sndfile = project
  .in(file("modules/sndfile"))
  .enablePlugins(ScalaNativePlugin, BindgenPlugin)
  .settings(commonSettings: _*)
  .settings(bindgenBindings := {
    val org = (Compile / organization).value
    List(Binding(header = dirs.includes / "sndfile.h", packageName = s"$org.sndfile"))
      .map(binding => binding.withMultiFile(true).withNoComments(true).withNoLocation(true).withNoConstructor(Set("*")))
  })
  .settings(nativeConfig ~= (config => config.withLinkingOptions(config.linkingOptions ++ Seq("-lsndfile"))))

lazy val magickwand = project
  .in(file("modules/magickwand"))
  .enablePlugins(ScalaNativePlugin, BindgenPlugin)
  .settings(commonSettings: _*)
  .settings(bindgenBindings := {
    val org = (Compile / organization).value
    List(
      Binding(header = magickWand.header, packageName = s"$org.magickwand")
        .withClangFlags(magickWand.cFlags)
        .withCImports(Seq(magickWand.header.toPath().toString()))
    ).map(binding => binding.withMultiFile(true).withNoComments(true).withNoLocation(true).withNoConstructor(Set("*")))
  })
  .settings(
    nativeConfig ~=
      (config =>
        config
          .withCompileOptions(config.compileOptions ++ magickWand.cFlags)
          .withLinkingOptions(config.linkingOptions ++ magickWand.ldFlags)
      )
  )

lazy val root = project.in(file(".")).aggregate(sndfile, magickwand)
