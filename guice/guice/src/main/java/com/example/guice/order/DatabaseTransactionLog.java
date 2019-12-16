package com.example.guice.order;

public class DatabaseTransactionLog implements TransactionLog {

    @Override
    public void logChargeResult(ChargeResult chargeResult) {
        if (chargeResult.getDeclineMessage() == null) {
            System.out.println("Log success charge result");
        }
    }

    @Override
    public void logConnectException(Exception e) {

    }
}
