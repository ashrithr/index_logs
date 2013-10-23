package com.cloudwick.solr

/**
 * Driver Class
 * @author ashrith 
 */
object Main extends App {
  /*
   * Command Line Options Parser
   */
  val optionsParser = new scopt.OptionParser[OptionsConfig]("index_logs") {
    head("index_logs", "0.1")
    opt[String]('m', "mode") required() valueName("<index|search>") action { (x, c) =>
      c.copy(mode = x)
    } validate { x:String =>
      if (x == "index" || x == "search") success else failure("value of '--mode' must be either 'index' or 'search'")
    } text("operation mode ('index' will index logs & 'search' will perform search)")
    opt[String]('u', "solrServerUrl") action { (x, c) =>
      c.copy(solrServerUrl = x)
    } text("url for connecting to solr server instance")
    opt[Int]('e', "eventsPerSec") action { (x, c) =>
      c.copy(eventsPerSec = x)
    } text("number of log events to generate per sec")
    opt[Int]('s', "ipSessionCount") action { (x, c) =>
      c.copy(ipSessionCount = x)
    } text("number of times a ip can appear in a session")
    opt[Int]('l', "ipSessionLength") action { (x, c) =>
      c.copy(ipSessionLength = x)
    } text("size of the session")
    opt[Unit]('c', "cleanPreviousIndex") action { (_, c) =>
      c.copy(cleanPreviousIndex = true)
    } text("deletes the existing index on solr core")
    opt[String]('q', "query") action { (x, c) =>
      c.copy(solrQuery = x)
    } text("solr query to execute, defaults to: '*:*'")
    opt[Int]("queryCount") action { (x, c) =>
      c.copy(queryCount = x)
    } text("number of documents to return on executed query, default:10")
    help("help") text("prints this usage text")
  }

  optionsParser.parse(args, OptionsConfig()) map { config =>
    /*
     * shutdown hook to handle CTRL+C
     */
    sys.addShutdownHook({
      println
      println("ShutdownHook called")
      if(config.mode == "index") commitToIndex(config.solrServerUrl)
    })

    println("Successfully parsed command line args" + config)
    if (config.mode == "index") {
      try {
        indexLogs(config.solrServerUrl,
          config.eventsPerSec,
          config.ipSessionCount,
          config.ipSessionLength,
          config.cleanPreviousIndex
        )
      } catch {
        case e: Exception => e.printStackTrace
      } finally {
        println
        println("Flushing to solr")
        commitToIndex(config.solrServerUrl)
      }
    } else {
      searchLogs(config.solrServerUrl, config.solrQuery, config.queryCount)
    }
  } getOrElse {
    println("Failed to parse command line args")
  }

  def indexLogs(solrURL:String,
                numEvents: Int,
                sessionCount: Int,
                sessionLength: Int,
                cleanPreviousIndex:Boolean) = {
    println("Generating log events @ " + Console.BLUE + s"${numEvents}/sec " + Console.WHITE +
      "with session count of: " + Console.BLUE + sessionCount + Console.WHITE +
      " and session length of: " + Console.BLUE + sessionLength + Console.RESET)
    println("Indexing log events to Solr server @ " + Console.BLUE + solrURL + Console.RESET)
    println("Press CTRL+C to stop generating events and commit them to solr index")

    val ipGenerator = new IPGenerator(sessionCount, sessionLength)
    val logEventGen = new LogGenerator(ipGenerator)
    val indexer = new IndexLogs(solrURL)

    val sleepTime = 1000 / numEvents
    val batchSize = 10
    var messagesCount = 1

    // clean existing index if user sets the flag
    if(cleanPreviousIndex) {
      println("Cleaning up existing index")
      indexer.cleanup
    }

    // keep on generating events and index them to solr
    while(true) {
      (1 to batchSize).foreach{ _ =>
        indexer.indexDocument(logEventGen.eventGenerate)
        Thread.sleep(sleepTime)
        messagesCount += 1
        print(s"\rMessages Indexed: ${messagesCount}")
      }
    }
  }

  def commitToIndex(solrURL:String) = {
    println("Committing documents to solr index")
    val indexer = new IndexLogs(solrURL)
    indexer.commitDocuments
  }

  def searchLogs(solrUrl:String, solrQuery:String, queryCount:Int) = {
    println("Connecting to Solr server @ " + Console.BLUE + solrUrl + Console.RESET)
    println("Querying solr with query: " + Console.BLUE + solrQuery + Console.RESET)
    val searcher = new SearchLogs(solrUrl)
    val results = searcher.executeQuery(solrQuery, queryCount)
    results.documents.foreach { logEvent: Map[String, Any] =>
      searcher.prettyPrint(logEvent)
    }
  }
}
