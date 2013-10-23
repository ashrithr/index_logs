package com.cloudwick.solr
import jp.sf.amateras.solr.scala.{Order, SolrClient}

/**
 * Searches solr index for specified query using solr-scala-client
 * @param solrURL solr server url to connect to
 * @author ashrith 
 */
class SearchLogs(solrURL:String) {
  val solrClient = new SolrClient(solrURL)

  /**
   * executes a given solr query
   * @param query
   * @return
   */
  def executeQuery(query: String, rowsCount: Int) = {
    solrClient.query(query)
      .rows(rowsCount)
      .fields("ip", "timestamp", "response", "bytes_sent", "request_page", "browser")
      .sortBy("timestamp", Order.asc)
      .getResultAsMap()
  }

  /**
   * pretty prints a solr document
   * @param document
   */
  def prettyPrint(document: Map[String, Any]) = {
    println()
    document.foreach { doc =>
      println(s"\t${doc._1}: ${doc._2}")
    }
  }
}
