# kettle-plugin-parent

> 采集kettle执行过程中，作业和转换中的SQL语句，发送到kafka中。
## 打包
`mvn clean package -DskipTests`
打包后会在plugin目录生成对应zip包。使用插件时，上传插件包到kettle的plugin目录下进行解压。
## metadata-collector 插件
> 收集转换时的SQL任务执行语句，传输到Kafka 所以需要kafka配置。在kettle 目录下放入 kafka.properties进行配置
**kafka.properties**
```properties
bootstrap.servers=localhost:9092
topic=TOPIC_METADATA_LINEAGE
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

**文件结构**

```txt
data-integration
|── kafka.properties
|── plugins/kettle-lineage-collector-plugin
    ├── kettle-lineage-collector-core-9.2.0.0-66.jar
    ├── lib
    │   ├── jackson-databind-2.9.1.jar
    │   └── kafka-clients-1.1.1.jar
    └── version.xml
```


**备注本插件在kettle版本>=8已测试通过**



## 调试kettle
进入kettle工程目录，打开spoon.sh 放开注释
```shell script
OPT="$OPT -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```
### 调试入口
* spoon.sh 入口 `org.pentaho.di.ui.spoon.Spoon`
* Kitchen 入口 `org.pentaho.di.kitchen.Kitchen`
