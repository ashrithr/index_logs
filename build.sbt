import AssemblyKeys._

seq(assemblySettings: _*)

name := "index_logs"

organization := "com.cloudwick"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.1"

resolvers ++= Seq(
  "Scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "amateras-repo" at "http://amateras.sourceforge.jp/mvn/"
)

libraryDependencies ++= Seq(
  "org.apache.solr" % "solr-solrj" % "4.3.1",
  "commons-logging" % "commons-logging" % "1.1.1",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "jp.sf.amateras.solr.scala" %% "solr-scala-client" % "0.0.8",
  "com.github.scopt" %% "scopt" % "3.1.0"
)

// Main class for the compiled JAR

mainClass in assembly := Some("com.cloudwick.solr.Main")
