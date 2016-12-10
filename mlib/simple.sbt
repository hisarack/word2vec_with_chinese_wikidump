
name := "Wikipedia Clustering"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.5.0"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.5.0"
libraryDependencies += "org.apache.spark" % "spark-unsafe_2.10" % "1.5.0"

libraryDependencies += "io.spray" %%  "spray-json" % "1.2.6"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies += "com.huaban" % "jieba-analysis" % "1.0.2"
