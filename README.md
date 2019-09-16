# 日志异常消息通知的spring-boot-start框架：prometheus-spring-boot-starter


## 背景：

对于项目工程来说，bug是不可能避免的，生产环境并不能像本地环境一样方便调试，在使用者发现bug之前开发者自己先发现并提前解决肯定上上策，但是有些框架异常并非自己预期，
这时候可以考虑基于log.error()主动触发异常提示开发者，并精确获取异常堆栈信息

## 系统需求

![jdk版本](https://img.shields.io/badge/java-1.8%2B-red.svg?style=for-the-badge&logo=appveyor)
![maven版本](https://img.shields.io/badge/maven-3.2.5%2B-red.svg?style=for-the-badge&logo=appveyor)
![spring boot](https://img.shields.io/badge/spring%20boot-2.0.3.RELEASE%2B-red.svg?style=for-the-badge&logo=appveyor)

## 当前版本

![目前工程版本](https://img.shields.io/badge/version-1.0.0-green.svg?style=for-the-badge&logo=appveyor)


## 快速接入
（默认钉钉推送，本地缓存。有需求可以更改配置，邮箱或redis异常存储）
1. 工程``mvn clean install``打包本地仓库。
2. 在引用工程中的``pom.xml``中做如下依赖
```
    <dependency>
        <groupId>com.logpolice</groupId>
        <artifactId>logpolice-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
```
3. 在``application.properties``或者``application.yml``中做如下的配置：（至于以上的配置说明后面的章节会讲到）
```
logpolice.enabled=true
logpolice.dingding.token=xxxxxxxxxxx

```
4. 钉钉配置：[钉钉机器人](https://open-doc.dingtalk.com/microapp/serverapi2/krgddi "自定义机器人")
5. 以上配置好以后就可以写demo测试啦，首先配置logback.xml，引用com.logpolice.port.LogSendAppender：
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoder 默认配置为PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="LogDingDingAppender" class="com.logpolice.port.LogSendAppender"/>

    <root level="ERROR">
        <appender-ref ref="LogDingDingAppender"/>
    </root>
</configuration>
```
然后编写测试类，需要主动打印exception堆栈信息，否则日志获取不到：
```
    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = DemoApplication.class)
    public class DemoApplicationTest1s {
    
        private Logger log = LoggerFactory.getLogger(DemoApplicationTest1s.class);
    
        @Test
        public void test1() {
            try {
                int i = 1 / 0;
            } catch (Exception e) {
                log.error("哈哈哈哈，param:{}, error:{}", 1, e);
            }
        }
    
    }
```