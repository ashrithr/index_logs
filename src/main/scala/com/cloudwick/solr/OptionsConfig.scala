package com.cloudwick.solr

/**
 * Case class for wrapping scopt options parser options
 * @author ashrith 
 */
case class OptionsConfig (mode:String = "index", solrServerUrl: String = "http://localhost:8983/solr/logs",
                          eventsPerSec: Int = 1, ipSessionCount: Int = 5, ipSessionLength: Int = 5,
                          cleanPreviousIndex: Boolean = false, solrQuery:String = "*:*", queryCount:Int = 10)
