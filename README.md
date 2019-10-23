# Traffic limits

#### Подготовительные работы:

+ Скачать и установить Cloudera Quickstart VM https://www.cloudera.com/downloads/quickstart_vms/5-13.html

+ Запустить сервисы HDFS, YARN, Spark, Hive, Kafka

#### Задача:

1. Создать в Hive БД traffic_limits с таблицей limits_per_hour. Таблица должна содержать 3 колонки: limit_name, limit_value, effective_date. Задать 2 лимита: min=1024, max=1073741824. В колонку effective_date внести дату, начиная с которой эти лимиты вступают в силу.
2. Написать приложение на Spark Streaming, которое, используя любую общедоступную библиотеку для обработки трафика (Pcap4J, jpcap, etc), будет считать объем захваченного трафика за 5 минут и в случае выхода за пределы минимального и максимального значения будет посылать сообщение в Kafka в топик alerts. Сообщение должно посылаться всякий раз, когда объем трафика за 5 минут пересекает любое из пороговых значений
3. Приложение должно обновлять пороговые значения каждые 20 минут (следует брать значения с максимальной effective_date)
4. Написать unit тесты
5. Предусмотреть возможность считать только тот трафик, который отправляется/принимается на/с определенного IP-адреса, который задается в качестве аргумента при сабмите. По умолчанию (если IP не указан) должен учитываться весь трафик.
6. Предусмотреть возможность обновления пороговых значений сразу после их обновления в базе данных.
___

### CDH Components Set up:

+ _Cloudera QuickStart_ VM 5.13.0.0 _VirtualBox_
+ _Core Apache Hadoop_:
    + _HDFS_ -  a fault-tolerant and self-healing distributed filesystem designed to turn a cluster of industry-standard servers into a massively scalable pool of storage. (accepts data in any format regardless of schema)
    + _MapReduce_ - designed to process unlimited amounts of data of any type that’s stored in HDFS by dividing workloads into multiple tasks across servers that are run in parallel. 
    + _YARN_ - provides open source resource management for Hadoop.
+ Additional Apache services for Big Data workflow:
    + _Kafka_ 4.1.0 - распределенная и потоковая обработка, а также брокер сообщений.
    + _Spark_ – фреймворк для кластерных вычислений и крупномасштабной обработки данных.
    _Spark Streaming_ для обработки потоковых данных.
    + _Hive_ - СУБД для аналитики.

## Cloudera Manager resources requirements

![Cloudera Manager]()

## Hive Database 

Table with traffic limitations per hour

![Limits per hour]()
