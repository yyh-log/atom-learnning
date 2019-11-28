package com.example.demo.ioc;

@MyComponent
public class MyService implements Service {

    @MyAutowired
    private Animals animals;

    public void print(){
        System.out.println("print ioc...");
        animals.eat();
    }

    @Override
    public void test(){
        System.out.println("test ioc...");
    }
}
