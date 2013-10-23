package com.cloudwick.solr

/**
 * Case class for wrapping log event
 * @author ashrith 
 */
case class LogEvent(ip:String, timestamp:String, request:String, responseCode:Int, responseSize:Int, userAgent:String)
