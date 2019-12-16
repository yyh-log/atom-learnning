package com.example.guice.order;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import javax.inject.Inject;

public class RealBillingService implements BillingService {

    private final CreditCardProcessor processor;
    private final TransactionLog transactionLog;

    @Inject
    public RealBillingService(@Named("credit") CreditCardProcessor processor,
                              TransactionLog transactionLog) {
        this.processor = processor;
        this.transactionLog = transactionLog;
    }

    @Override
    public Receipt chargeOrder(PizzaOrder order, CreditCard creditCard) {
        try {
            ChargeResult result = processor.charge(creditCard, order.getAmount());
            transactionLog.logChargeResult(result);
            return result.wasSuccessful() ? Receipt.forSuccessfulCharge(order.getAmount()) : Receipt.forDeclinedCharge(result.getDeclineMessage());
        } catch (UnreachableException e) {
            transactionLog.logConnectException(e);
            return Receipt.forSystemFailure(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BillingModule());
        BillingService billingService = injector.getInstance(BillingService.class);
        PizzaOrder order = new PizzaOrder(10);
        CreditCard creditCard = new CreditCard(1000);
        if (billingService != null) {
            billingService.chargeOrder(order, creditCard);
        }

        Receipt c1 = injector.getInstance(Receipt.class);
        Receipt c2 = injector.getInstance(Receipt.class);
        System.out.println(c1==c2);
    }
}
