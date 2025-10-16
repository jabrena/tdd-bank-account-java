package org.xpdojo.bank;

public class Account {

    private Money balance;

    public Account() {
        balance = new Money(0);
    }

    public void deposit(Money money) {
        balance = money.add(balance);
    }

    public Money getBalance() {
        return balance;
    }
}
