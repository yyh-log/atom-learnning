package com.example.guice.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PizzaOrder {

    private int amount;

    public PizzaOrder(int amount){
        this.amount = amount;
    }
}
