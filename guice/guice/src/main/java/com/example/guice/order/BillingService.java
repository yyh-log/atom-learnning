package com.example.guice.order;

public interface BillingService {

    Receipt chargeOrder(PizzaOrder order, CreditCard creditCard);
}
