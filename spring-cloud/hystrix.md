
![](assets/6e8316eb.png)  
maven配置  
```
<dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
       </dependency>
       <!--监控仪表板-->
       <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <!--spring-boot-actuator-->
           <artifactId>spring-boot-starter-actuator</artifactId>
       </dependency>
```
#### 基本使用
```java
@SpringBootApplication
@EnableHystrix
@RestController
@Configuration
public class HystrixServer {

    public static void main(String[] args) {
        SpringApplication.run(HystrixServer.class,args);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    
    //配置详解
    //https://github.com/Netflix/Hystrix/wiki/Configuration#metrics.rollingStats.timeInMilliseconds
    @HystrixCommand(fallbackMethod = "fallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "5000"),
            @HystrixProperty(name="hystrix.command.default.metrics.rollingStats.timeInMilliseconds",value="1000")
    },threadPoolProperties = {
            @HystrixProperty(name="coreSize",value="1"),
            @HystrixProperty(name="maxQueueSize",value="10")
    })
    @GetMapping("/hystrix/test")
    public void getInstance(){

    }
    //throwable是可选的
    public void fallbackMethod(Throwable throwable){

    }
}
//断路器配置
//circuitBreaker.enabled //默认true
//circuitBreaker.requestVolumeThreshold //窗口内统计请求个数的阈值，例如设为20，那么一个窗口内只有19个请求，即使全部失败，断路器也不会打开
//circuitBreaker.sleepWindowInMilliseconds//窗口大小，默认5秒为一个窗口
//circuitBreaker.errorThresholdPercentage//一个窗口内失败比例，默认50%,失败超过50%,断路器打开
//circuitBreaker.forceOpen
//circuitBreaker.forceClosed
```
#### 资源隔离
* THREAD（线程隔离）使用该方式，HystrixCommand将在单独的线程上执行，并发请求受到线程池中的线程数量的限制。
* SEMAPHORE(信号量隔离)，使用该方式，HystrixCommand在调用的线程执行，开销较小，适用非网络调用
```
@HystrixProperty(name="execution.isolation.strategy",value = "SEMAPHORE"),
```
#### feign使用hystrix  
```java
@FeignClient(value = "supplyserver",fallbackFactory = FallBackFactory.class)
public interface FeignHttpClient {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHi(@RequestParam(value = "name") String name);
}

@Component
class FallBackFactory implements FallbackFactory<FeignHttpClient>{

    @Override
    public FeignHttpClient create(Throwable throwable) {//捕捉异常
        return new FeignHttpClient() {
            @Override
            public String sayHi(String name) {
                return "hello world";
            }
        };
    }
}
```
#### hystrix监控
1、hystrix.stream  
```java
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <!--spring-boot-actuator-->
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```
application.yml  
```
server:
  port: 8085
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
spring:
  application:
    name: hystrixserver
#actuator设置
management:
  endpoints:
    web:
      #base-path: / #修改访问路径  2.0之前默认是/   2.0默认是 /actuator  可以通过这个属性值修改
      exposure:
        include: '*'  #开放所有页面节点  默认只开启了health、info两个节点
```
hystrixCommand触发hystrix监控
```java
@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
@RestController
@Configuration
public class HystrixServer {

    public static void main(String[] args) {
        SpringApplication.run(HystrixServer.class,args);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "1000")
    },threadPoolProperties = {
            @HystrixProperty(name="coreSize",value="1"),
            @HystrixProperty(name="maxQueueSize",value="10")
    })
    @GetMapping("/hystrix/test")
    public void getInstance(){
        String s = restTemplate.getForObject("http://eurekaclient/instance/test", String.class);
        System.out.println("response:" + s);
    }

    public void fallbackMethod(Throwable throwable){
        System.out.println("fallback method...");
    }
}
```
2 turbine

```
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <!--spring-boot-actuator-->
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```
* turbine.appConfig ：配置Eureka中的serviceId列表，表明监控哪些服务
* turbine.aggregator.clusterConfig ：指定聚合哪些集群，多个使用”,”分割，默认为default。可使用http://.../turbine.stream?cluster={clusterConfig之一}访问
* turbine.clusterNameExpression ： 1. clusterNameExpression指定集群名称，默认表达式appName；此时：turbine.aggregator.clusterConfig需要配置想要监控的应用名称；2. 当clusterNameExpression: default时，turbine.aggregator.clusterConfig可以不写，因为默认就是default；3. 当clusterNameExpression: metadata[‘cluster’]时，假设想要监控的应用配置了eureka.instance.metadata-map.cluster: ABC，则需要配置，同时turbine.aggregator.clusterConfig: ABC
* turbine.combine-host-port参数设置为true，可以让同一主机上的服务通过主机名与端口号的组合来进行区分，默认情况下会以host来区分不同的服务，这会使得在本机调试的时候，本机上的不同服务聚合成一个服务来统计。

application.yml
```
server:
  port: 8086
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
spring:
  application:
    name: turbineserver

#springboot2.0. 的配置项为：
#actuator端口
management:
  #  server:
  #    port: 9007
  endpoints:
    web:
      #      base-path: /monitor #修改访问路径  2.0之前默认是/   2.0默认是 /actuator  可以通过这个属性值修改
      exposure:
        include: '*'  #开放所有页面节点  默认只开启了health、info两个节点

turbine:
  aggregator:
    cluster-config: default     #需要监控的服务集群名
  app-config: hystrixserver    #需要监控的服务名
  cluster-name-expression: new String("default")
#  instanceUrlSuffix:
#      default: actuator/hystrix.stream    #key是clusterConfig集群的名字，value是hystrix监控的后缀，springboot2.0为actuator/hystrix.stream
```
启动
```java
@SpringBootApplication
@EnableTurbine
@EnableHystrixDashboard
public class TurbineServer {

    public static void main(String[] args) {
        SpringApplication.run(TurbineServer.class,args);
    }
}
```
