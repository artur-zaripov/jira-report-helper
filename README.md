# jira-report-helper
Simple tool (command line only) for creating reports from Jira via API. Works only with tasks that was "In Progress" by current user.

Working with:
* Scala 2.12.2
* SBT 0.13.15

Tested on:
* Jira v7

Initial setup _must_ be made in `src/main/resources/application.properties` (Jira URL, username, password, amount of results)

Run with:

`sbt clean run`