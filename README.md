# Traffic limits
###Hadoop project
___
CDH is Cloudera’s 100% open source platform distribution, including Apache Hadoop.
Cloudera helps perform end-to-end Big Data workflows.


###CDH Components Set up:

+ _Cloudera QuickStart_ VM 5.13.0.0 _VirtualBox_
+ _Apache Hadoop_ including (Core Hadoop):
    + _HDFS_ -  a fault-tolerant and self-healing distributed filesystem designed to turn a cluster of industry-standard servers into a massively scalable pool of storage. (accepts data in any format regardless of schema)
    + _MapReduce_ - designed to process unlimited amounts of data of any type that’s stored in HDFS by dividing workloads into multiple tasks across servers that are run in parallel. 
    + _YARN_ - provides open source resource management for Hadoop.
+ _Kafka_ 4.1.0

####Integrated across the platform

Core Hadoop, including HDFS, MapReduce, and YARN, is part of the foundation of Cloudera’s platform. 
All platform components have access to the same data stored in HDFS and participate in shared resource management via YARN. 
Hadoop, as part of Cloudera’s platform, also benefits from simple deployment and administration (through _Cloudera Manager_) and shared compliance-ready security and governance (through _Apache Sentry_ and _Cloudera Navigator_) — all critical for running in production.

![Cloudera Manager](https://www.cloudera.com/content/dam/www/marketing/images/diagrams/cloudera-enterprise-detailed.png)




![Cloudera Manager]()

+ Hive Database 

Table with traffic limitations per hour

![Limits per hour]()
