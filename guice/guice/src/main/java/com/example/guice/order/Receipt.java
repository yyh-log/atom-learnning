package com.example.guice.order;

import com.google.inject.Singleton;

//@Singleton
public class Receipt {

    public static Receipt forSuccessfulCharge(int amount) {
        System.out.println("charge " + amount + " money" + "Generate receipt successfully.");
        return new Receipt();
    }

    public static Receipt forDeclinedCharge(String declineMessage) {
        System.err.println("charge " + declineMessage + " money" + "Generate receipt failed.");
        return new Receipt();
    }

    public static Receipt forSystemFailure(String message) {
        System.err.println("Generate receipt failed.");
        return new Receipt();
    }
}
