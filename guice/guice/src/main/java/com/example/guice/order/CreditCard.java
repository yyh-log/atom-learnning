package com.example.guice.order;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Singleton
public class CreditCard {

    private int balance;

    public CreditCard(int balance){
        this.balance = balance;
    }
}
