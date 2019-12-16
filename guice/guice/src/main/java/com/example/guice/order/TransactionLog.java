package com.example.guice.order;

public interface TransactionLog {

    void logChargeResult(ChargeResult chargeResult);

    void logConnectException(Exception e);
}
