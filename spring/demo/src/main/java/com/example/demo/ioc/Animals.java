package com.example.demo.ioc;

@MyComponent
public class Animals {

    @MyAutowired
    private UserBean userBean;

    public void eat(){
        System.out.println("eat...");
        userBean.getUser();
    }

}
