package com.example.guice.order;

public interface CreditCardProcessor {

    ChargeResult charge(CreditCard creditCard, int amount) throws UnreachableException;

}
