package com.example.guice.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeResult {

    private String declineMessage;

    public boolean wasSuccessful() {
        return declineMessage == null;
    }
}
