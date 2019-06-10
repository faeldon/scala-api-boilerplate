// Makes our code tidy
addSbtPlugin("com.geirsson"       % "sbt-scalafmt"          % "1.5.1")
// Revolver allows us to use re-start and work a lot faster!
addSbtPlugin("io.spray"           % "sbt-revolver"          % "0.9.1")
// Native Packager allows us to create standalone jar
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager"   % "1.3.22")
// Database migrations
addSbtPlugin("io.github.davidmweber" % "flyway-sbt"         % "5.2.0")
// Documentation plugins
addSbtPlugin("org.tpolecat"       % "tut-plugin"            % "0.6.11")

addSbtPlugin("com.47deg"          % "sbt-microsites"        % "0.7.18")

addSbtPlugin("com.typesafe.sbt"   % "sbt-ghpages"           % "0.6.3")

addSbtPlugin("org.scoverage"      % "sbt-scoverage"         % "1.6.0-M3")

addSbtPlugin("net.virtual-void"   % "sbt-dependency-graph"  % "0.9.0")

addSbtPlugin("com.github.gseitz"  % "sbt-release"           % "1.0.10")

addSbtPlugin("com.jsuereth"       % "sbt-pgp"               % "1.1.2")

addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"         % "0.9.0")
// Check if there are new updates in the dependencies
addSbtPlugin("com.timushev.sbt"   % "sbt-updates"           % "0.4.0")

resolvers += "Flyway".at("https://davidmweber.github.io/flyway-sbt.repo")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"
