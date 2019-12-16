package com.example.guice.order;

public class PaypalCreditCardProcessor implements CreditCardProcessor {

    @Override
    public ChargeResult charge(CreditCard creditCard, int amount) throws UnreachableException {
        if (creditCard == null) {
            throw new UnreachableException("creditCard is null");
        }
        System.out.println("Begin to charge order.");
        ChargeResult chargeResult =  new ChargeResult();
        System.out.println("Charge order successfully.");
        return chargeResult;
    }
}
