package org.xpdojo.bank;

/**
 * Immutable class to represent Money as a concept.
 * This class should have no accessor methods.
 */
public class Money {

    private int amount;

    public Money(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Money add(Money money) {
        return new Money(this.amount + money.getAmount());
    }
}
