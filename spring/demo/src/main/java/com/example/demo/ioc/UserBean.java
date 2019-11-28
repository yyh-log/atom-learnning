package com.example.demo.ioc;

@MyComponent
public class UserBean {

    @MyAutowired
    private Service service;

    public UserBean() {
        System.out.println("UserBean -> instance");
    }

    public void getUser(){
        service.test();     //接口的方法,如果注入失败,将报空指针异常
        System.out.println("用户详情展示");
    }
}
