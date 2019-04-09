## RxJava2.0
要理解Rxjava2.0首先得搞懂什么是观察者模式。简单介绍一下，A和B两个，A是被观察者，B是观察者，B对A进行观察，B并不是需要时刻盯着A，而是A如果发生了变化，会主动通知B，B会对应做一些变化。举个例子，假设A是连载小说，B是读者，读者订阅了连载小说，当小说出现了新的连载的时候，会推送给读者。读者不用时刻盯着小说连载，而小说有了新的连载会主动推送给读者。这就是观察者模式。而RxJava正是基于观察者模式开发的。

![](assets/rxjava-674329e8.png)
## RxJava2.0的基本使用
添加依赖
```java
<dependency>
  <groupId>io.reactivex.rxjava2</groupId>
  <artifactId>rxjava</artifactId>
  <version>2.2.8</version>
</dependency>
```
