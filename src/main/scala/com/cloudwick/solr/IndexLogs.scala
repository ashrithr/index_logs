package com.cloudwick.solr

import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.common.SolrInputDocument
import scala.collection.mutable.ArrayBuffer

/**
 * Indexes apache http log events to solr using solr library
 * @param serverURL solr server url to connect
 * @author ashrith 
 */
class IndexLogs(serverURL: String) {
  val server = new HttpSolrServer(serverURL);

  /**
   * Generates random uuid
   * @return
   */
  def uuid = java.util.UUID.randomUUID.toString

  /**
   * Wraps LogEvent into SolrDocument
   * @param logEvent
   * @return
   */
  def getSolrDocument(logEvent:LogEvent) = {
    val document = new SolrInputDocument()
    document.addField("id", uuid)
    document.addField("ip", logEvent.ip)
    document.addField("timestamp", logEvent.timestamp)
    document.addField("request_page", logEvent.request)
    document.addField("response", logEvent.responseCode)
    document.addField("bytes_sent", logEvent.responseSize)
    document.addField("browser", logEvent.userAgent)
    document
  }

  /**
   * Deletes existing index
   * @return
   */
  def cleanup = {
    server.deleteByQuery("*:*")
    server.commit()
  }

  /**
   * Indexes events & commits them
   * @param logEvents
   * @return
   */
  def send(logEvents:ArrayBuffer[LogEvent]) = {
    logEvents.foreach(logEvent => server.add(getSolrDocument(logEvent)))
    server.commit()
  }

  /**
   * Indexes single log event
   * @param logEvent
   * @return
   */
  def indexDocument(logEvent: LogEvent) = {
    server.add(getSolrDocument(logEvent))
  }

  /**
   * Commits documents to solr index
   * @return
   */
  def commitDocuments = {
    server.commit()
  }

}
