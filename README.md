### Demo Asynchronous Messaging System using Spring Boot & Kafka

#### Precondition (explain mac os environment)
- install kafka (it has dependency zookeeper. so your system also will download zookeeper)
```
brew install kafka.
```

- start zookeeper
```
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties OR zkServer start
```

- start kafka
```
kafka-server-start /usr/local/etc/kafka/server.properties
```

- register service zookeeper and kafka.
```
brew services start zookeeper
brew services start kafka
```

- create kafka topic
```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic voyager-apollo
```

#### WARNING
- During server start, you might be facing connection broken issue.
```
[2018-08-28 16:24:41,166] WARN [Controller id=0, targetBrokerId=0] Connection to node 0 could not be established. Broker may not be available. (org.apache.kafka.clients.NetworkClient)
[2018-08-28 16:24:41,268] WARN [Controller id=0, targetBrokerId=0] Connection to node 0 could not be established. Broker may not be available. (org.apache.kafka.clients.NetworkClient)
```

- To fix this issue, you need to change the server.properties file.
```
vi /usr/local/etc/kafka/server.properties
```

and then eliminate comment this server settings and update the value from
```
listeners=PLAINTEXT://localhost:9092
```

#### Test Consume using CLI
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic voyager-apollo --from-beginning
```
