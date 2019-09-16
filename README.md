# 日志异常消息通知的spring-boot-start框架：logpolice-spring-boot-starter


## 背景：

对于项目工程来说，bug是不可能避免的，生产环境并不能像本地环境一样方便调试，在使用者发现bug之前开发者自己先发现并提前解决肯定上上策，但是有些框架异常并非自己预期，
这时候可以考虑基于log.error()主动触发异常提示开发者，并精确获取异常堆栈信息，在获取异常消息推送的避免消息轰炸，可以根据推送策略自定义配置



## 系统需求

![jdk版本](https://img.shields.io/badge/java-1.8%2B-red.svg?style=for-the-badge&logo=appveyor)
![maven版本](https://img.shields.io/badge/maven-3.2.5%2B-red.svg?style=for-the-badge&logo=appveyor)
![spring boot](https://img.shields.io/badge/spring%20boot-2.0.3.RELEASE%2B-red.svg?style=for-the-badge&logo=appveyor)

## 当前版本

![目前工程版本](https://img.shields.io/badge/version-1.0.0-green.svg?style=for-the-badge&logo=appveyor)


## 快速接入(默认本地缓存&钉钉推送)
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
3. 在``application.properties``或者``application.yml``中做如下的配置：
```
    logpolice.enabled=true
    logpolice.dingding.web-hook=https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxxxxx

```
4. 钉钉配置：[钉钉机器人](https://open-doc.dingtalk.com/microapp/serverapi2/krgddi "自定义机器人")
5. 以上配置好以后就可以写demo测试啦，首先配置logback.xml
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
核心代码，引用com.logpolice.port.LogSendAppender：
```
    <appender name="LogDingDingAppender" class="com.logpolice.port.LogSendAppender"/>

    <root level="ERROR">
        <appender-ref ref="LogDingDingAppender"/>
    </root>
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
log.error()写入异常，推送效果（钉钉/邮箱）

![效果](/src/main/resources/微信截图_20190916162148.png)
![效果](/src/main/resources/微信截图_20190916162204.png)
![效果](/src/main/resources/微信截图_20190916194724.png)

log.error()未写入异常，推送效果（钉钉/邮箱）

![效果](/src/main/resources/微信截图_20190916163218.png)
![效果](/src/main/resources/微信截图_20190916194628.png)


## 消息策略
1. 推送类型（钉钉/邮件，默认钉钉）
```
    #logpolice.notice-send-type=ding_ding 默认值
    logpolice.notice-send-type=mail
```

2. 推送策略（超时时间/超频次数，默认超时）
```
    logpolice.frequency-type=timeout 默认值
    logpolice.timeInterval=5 默认值
```
```
    logpolice.frequency-type=show_count
    logpolice.show-count=10
```

3. 日志数据重置时间，异常白名单
```
    logpolice.clean-time-interval=3600
    logpolice.exception-white-list=java.lang.ArithmeticException,java.lang.ArithmeticException2
```


## redis接入（多实例共享异常数据）
1. 修改application.properties 异常redis开关
```
    logpolice.enable-redis-storage=true
    logpolice.exception-redis-key=xxx_xxxx_xxxx:
```
2. 需要引入spring-boot-starter-data-redis
```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
```
3. application.properties 新增redis配置
 ```
     spring.redis.database=0
     spring.redis.host=xx.xx.xx.xxx
     spring.redis.port=6379
     spring.redis.password=xxxx
 ```


## 邮件接入
1. 有邮件通知的话需要在``pom.xml``中加入如下依赖
```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
```
2. application.properties 新增邮件配置，(163，qq 不同邮箱配置可能有差异)
```
    logpolice.notice-send-type=mail
    logpolice.mail.from=发送者@qq.com
    logpolice.mail.to=接收者@163.com
    logpolice.mail.cc=
    logpolice.mail.bcc=
    
    spring.mail.host=smtp.qq.com
    spring.mail.username=发送者@qq.com
    spring.mail.password=xxxxxxxx
    spring.mail.default-encoding=UTF-8
    spring.mail.properties.mail.smtp.ssl.enable=true
    spring.mail.properties.mail.imap.ssl.socketFactory.fallback=false
    spring.mail.properties.mail.smtp.ssl.socketFactory.class=com.fintech.modules.base.util.mail.MailSSLSocketFactory
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    spring.mail.properties.mail.smtp.starttls.required=true
```

有任何好的建议可以联系 qq:379198812，感谢支持