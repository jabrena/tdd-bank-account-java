package org.xpdojo.bank;

public class Account {

    private Money balance;

    public Account() {
        balance = new Money(0);
    }

    public void deposit(Money money) {
        //Preconditions
        if (money.getAmount() < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        balance = money.add(balance);
    }

    public Money getBalance() {
        return balance;
    }
}
