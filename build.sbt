name := "jira-reports-helper"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += "Atlassian repository" at "https://maven.atlassian.com/content/repositories/atlassian-public/"

libraryDependencies += "com.atlassian.jira" % "jira-rest-java-client-core" % "2.0.0-m15"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"