package org.xpdojo.bank;

import java.math.BigDecimal;

public class Account {

    private Money balance;

    public Account() {
        balance = new Money(BigDecimal.ZERO);
    }

    public void deposit(Money money) {
        //Preconditions
        if (money.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        balance = money.add(balance);
    }

    public Money getBalance() {
        return balance;
    }
}
