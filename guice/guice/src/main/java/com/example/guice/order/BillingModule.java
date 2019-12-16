package com.example.guice.order;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class BillingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransactionLog.class).to(DatabaseTransactionLog.class);
        bind(CreditCardProcessor.class).annotatedWith(Names.named("credit")).to(PaypalCreditCardProcessor.class);
        bind(BillingService.class).to(RealBillingService.class);
        bind(Receipt.class);
    }

    @Provides
    public CreditCard provideCreditCard(){
        return new CreditCard(100);

    }
}
