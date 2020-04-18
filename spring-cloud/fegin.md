Feign是spring cloud接口式的restful客户端，使用简单的配置和注解就可以实现微服务之间的http通信，集成了ribbon和hystrix提供了负载均衡和断路器功能。  
maven依赖  
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
@EnableFeignClients //在启动类上加  

一 使用feign  
```java
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class FeignServer {
    public static void main(String[] args) {
        SpringApplication.run(FeignServer.class,args);
    }
}

@FeignClient(value = "supplyserver")
public interface FeignHttpClient {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHi(@RequestParam(value = "name") String name);
}

@RestController
public class HiService {
    @Autowired
    FeignHttpClient feignHttpClient;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return feignHttpClient.sayHi( name );
    }

}

```

二、自定义配置  
Spring Cloud Netflix默认为feign（BeanType beanName：ClassName）提供以下bean，配置类为FeignClientsConfiguration：  
* Decoder feignDecoder：ResponseEntityDecoder（包装SpringDecoder）
* Encoder feignEncoder：SpringEncoder
* Logger feignLogger：Slf4jLogger
* Contract feignContract：SpringMvcContract
* Feign.Builder feignBuilder：HystrixFeign.Builder
* Client feignClient：如果启用了Ribbon，则它是LoadBalancerFeignClient，否则使用默认的feign客户端
feign.okhttp.enabled或feign.httpclient.enabled分别设置为true,来使用OkHttpClient和ApacheHttpClient feign客户端。  

Spring Cloud Netflix默认情况下不为feign提供以下bean，但仍然从应用程序上下文中查找这些类型的bean以创建feign客户端：
+ Logger.Level
+ Retryer
+ ErrorDecoder
+ Request.Options
+ Collection<RequestInterceptor>
+ SetterFactory

```java
局部配置
@FeignClient(name="store",configuration = StoreConfiguration.class)
public interface FeignStore {
}

public class StoreConfiguration {//不用加@configuration

    @Bean
    public Contract feignContract() {//使用feign.Contract.Default替换SpringMvcContract
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {//添加拦截器
        return new BasicAuthRequestInterceptor("user", "password");
    }
}

//自定义拦截器,实现RequestInterceptor
public class FeignRequestInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate requestTemplate) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    Enumeration<String> headerNames = request.getHeaderNames();
    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String name = headerNames.nextElement();
        String values = request.getHeader(name);
        requestTemplate.header(name, values);
      }
    }
  }
}
//创建全局配置
@Configuration
public class FeignInterceptorConfig {
  /**
   * feign请求拦截器
   */
  @Bean
  public RequestInterceptor requestInterceptor(){
    return new FeignRequestInterceptor();
  }
}


全局配置 
@EnableFeignClients(defaultConfiguration="StoreConfiguration.class")
```


三 配置文件配置  

```java
feign:
  client:
    config:
      feignName://feign名称(可以用default替换，此时是全局配置)
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
        errorDecoder: com.example.SimpleErrorDecoder
        retryer: com.example.SimpleRetryer
        requestInterceptors:
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false
        encoder: com.example.SimpleEncoder
        decoder: com.example.SimpleDecoder
        contract: com.example.SimpleContract
```

四 手动创建Feign客户端  
使用Feign Builder API创建客户端  
```java
@Import(FeignClientsConfiguration.class)
class FooController {

    private FooClient fooClient;

    private FooClient adminClient;

    @Autowired
    public FooController(Decoder decoder, Encoder encoder, Client client, Contract contract) {
        this.fooClient = Feign.builder().client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .requestInterceptor(new BasicAuthRequestInterceptor("user", "user"))
                .target(FooClient.class, "http://PROD-SVC");

        this.adminClient = Feign.builder().client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
                .target(FooClient.class, "http://PROD-SVC");
    }
}
```
五 集成hystrix
```java
@FeignClient(name = "hello", fallback = HystrixClientFallback.class)
protected interface HystrixClient {
    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello iFailSometimes();
}

static class HystrixClientFallback implements HystrixClient {
    @Override
    public Hello iFailSometimes() {
        return new Hello("fallback");
    }
}

//如果需要访问产生回退触发器的原因，可以使用@FeignClient中的fallbackFactory属性
@FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
protected interface HystrixClient {
    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello iFailSometimes();
}

@Component
static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
    @Override
    public HystrixClient create(Throwable cause) {
        return new HystrixClient() {
            @Override
            public Hello iFailSometimes() {
                return new Hello("fallback; reason was: " + cause.getMessage());
            }
        };
    }
}
```
六 压缩
```java
//你可以考虑为你的Feign请求启用请求或响应GZIP压缩，你可以通过启用以下属性之一来执行此操作：
feign.compression.request.enabled=true
feign.compression.response.enabled=true
//Feign请求压缩为你提供类似于你为Web服务器设置的设置：
feign.compression.request.enabled=true
feign.compression.request.mime-types=text/xml,application/xml,application/json
feign.compression.request.min-request-size=2048
```

七 日志设置  
Feign日志记录仅响应DEBUG级别
每个客户端配置Logger.Level对象，告诉Feign要记录多少，选择是：
* NONE，没有记录（DEFAULT）。
* BASIC，仅记录请求方法和URL以及响应状态代码和执行时间。
* HEADERS，记录基本信息以及请求和响应headers。
* FULL，记录请求和响应的headers、body和元数据。
```java
public class FooConfiguration {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```
