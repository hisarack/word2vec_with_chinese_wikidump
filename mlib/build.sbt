import com.typesafe.sbt.SbtStartScript
seq(SbtStartScript.startScriptForClassesSettings: _*)

net.virtualvoid.sbt.graph.Plugin.graphSettings

assemblyMergeStrategy in assembly := {
   case PathList("org", "apache", "spark", xs @ _*) => MergeStrategy.first
   case PathList("org", "apache", "hadoop", "yarn", xs @ _*) => MergeStrategy.first
   case PathList("com", "google", "common", xs @ _*) => MergeStrategy.first
   case PathList("javax", "xml", xs @ _*) => MergeStrategy.first
   case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
   case PathList("com", "esotericsoftware", "minlog", xs @ _*) => MergeStrategy.first
   case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
   case PathList("javax", "ws", "rs",  xs @ _*)         => MergeStrategy.first
   case PathList("org", "joda", "compiler", xs @ _*)         => MergeStrategy.first
   case PathList("org", "apache", "jasper", xs @ _*)         => MergeStrategy.first
   case PathList("org", "apache", "tools", "ant", xs @ _*)         => MergeStrategy.first
   case PathList("javax", "xml", "stream", xs @ _*)         => MergeStrategy.first
   case PathList("org", "apache", "commons", xs @ _*)         => MergeStrategy.first
   case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
   case "application.conf" => MergeStrategy.concat
   case "unwanted.txt"     => MergeStrategy.discard
         case x =>
            val oldStrategy = (assemblyMergeStrategy in assembly).value
            oldStrategy(x)
}
