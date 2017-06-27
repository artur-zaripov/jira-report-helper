import java.net.URI
import java.util

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.typesafe.config.ConfigFactory
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load

    val jiraURL = config.getString("jiraURL")
    val username = config.getString("username")
    val password = config.getString("password")
    val query = "status was \"In Progress\" by currentUser() order by created DESC"
    val MAX_RESULTS = config.getInt("maxResults")

    val factory = new AsynchronousJiraRestClientFactory()
    val restClient = factory.createWithBasicHttpAuthentication(
      new URI(jiraURL),
      username,
      password
    )
    try {
      val report = mutable.SortedMap[String, Set[String]]()

      println("Getting last " + MAX_RESULTS + " issues...")
      val searchResult = restClient.getSearchClient.searchJql(query, MAX_RESULTS, 0, null).claim

      println("Processing issues...")
      searchResult.getIssues.forEach(issue => {

        val expandedIssue = restClient.getIssueClient.getIssue(
          issue.getKey,
          new util.HashSet[Expandos](util.Arrays.asList(Expandos.CHANGELOG))
        ).claim

        expandedIssue.getChangelog.forEach(log => {
          if (username == log.getAuthor.getName)
            log.getItems.forEach(item =>
              if (item.getField == "status" && (item.getFromString == "In Progress" || item.getToString == "In Progress")) {
                val date = DateTimeFormat.forPattern("yyyy.MM.dd").print(log.getCreated)
                report(date) = report.getOrElse(date, Set()) ++ Set(expandedIssue.getKey + " - " + expandedIssue.getSummary)
              }
            )
        })

        println("Processed issue " + expandedIssue.getKey)
      })

      println("\nReport:")
      report.foreach(activity =>
        activity._2.foreach(issue =>
          println(activity._1 + " " + issue)
        )
      )
    } finally {
      restClient.close()
    }
  }
}