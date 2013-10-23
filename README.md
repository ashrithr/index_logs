Index Apache http Log Events using Solr
--------------------------------------

Indexes random generated apache http log events to solr and also searches user-provided queries

###Creating collection solr on single solr core

This example requires to create a collection called `logs` using the following steps:

```
cp -r $SOLR_HOME/example $SOLR_HOME/logs
cp -r $SOLR_HOME/tweets/solr/collection1 $SOLR_HOME/tweets/solr/logs
rm -r $SOLR_HOME/tweets/solr/collection1
echo 'name=logs' > $SOLR_HOME/tweets/solr/logs/core.properties
```

where, `$SOLR_HOME` is path where solr is installed

Note:

* `schema.xml` from logs collection should be replaced with `resources/schema.xml` from the project dir
* `solrconfig.xml` from logs collection should be replaced with `resources/solrconfig.xml` from the project dir

##Running

###Starting a Solr instance

```
cd $SOLR_HOME/logs && java -jar start.jar
```

###Packaging a jar

For packaging a jar with dependencies, [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html) and
[sbt-assembly](https://github.com/sbt/sbt-assembly) are required. Once required tools are installed run the following
command from project root

```
sbt assembly
```

###Ruuning the application

Finally, to run the application default shows the application

```
bin/solrc --help
index_logs 0.1
Usage: index_logs [options]

  -m <index|search> | --mode <index|search>
        operation mode ('index' will index logs & 'search' will perform search)
  -u <value> | --solrServerUrl <value>
        url for connecting to solr server instance
  -e <value> | --eventsPerSec <value>
        number of log events to generate per sec
  -s <value> | --ipSessionCount <value>
        number of times a ip can appear in a session
  -l <value> | --ipSessionLength <value>
        size of the session
  -c | --cleanPreviousIndex
        deletes the existing index on solr core
  -q <value> | --query <value>
        solr query to execute, defaults to: '*:*'
  --help
        prints this usage text
```

* To index the documents with all defaults, which will generate events at a rate of `1/sec` with session count and length
of `5` and indexes documents to default solr url of `http://localhost:8983/solr/logs`

    ```
    bin/solrc --mode index
    ```
* To index log events at a rate of `10/sec` and also clear previous index data

    ```
    bin/solrc --mode index --eventsPerSec 10 --cleanPreviousIndex
    ```
* To search log events

    ```
    bin/solrc --mode search --query '*:*'
    ```