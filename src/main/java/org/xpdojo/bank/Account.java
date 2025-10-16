package org.xpdojo.bank;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Account {

    private Money balance;

    private final ReentrantLock lock = new ReentrantLock();

    public Account() {
        balance = new Money(BigDecimal.ZERO);
    }

    public void deposit(Money money) {
        lock.lock();
        try {
            //Preconditions
            if (money.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Money cannot be negative");
            }
            balance = money.add(balance);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(Money money) {
        lock.lock();
        try {
            //Preconditions
            if (money.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Money cannot be negative");
            }
            var tempAmount = balance.subtract(money);
            if (tempAmount.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            balance = tempAmount;
        }
        finally {
            lock.unlock();
        }
    }

    public Money getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
