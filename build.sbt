name := "test-slapper"

organization := "jp.saisse"

version := "0.1"

compileOrder := CompileOrder.JavaThenScala

resolvers += "Restlet Repository" at "http://maven.restlet.org"

libraryDependencies += "org.restlet.jse" % "org.restlet" % "2.0.10" withSources()

libraryDependencies += "junit" % "junit" % "4.10" withSources()

libraryDependencies += "com.novocode" % "junit-interface" % "0.7"

libraryDependencies += "org.springframework" % "spring-core" % "3.1.0.RELEASE" withSources()

libraryDependencies += "org.springframework" % "spring-context" % "3.1.0.RELEASE" withSources()

libraryDependencies += "org.springframework" % "spring-beans" % "3.1.0.RELEASE" withSources()

libraryDependencies += "org.springframework" % "spring-test" % "3.1.0.RELEASE" withSources()
