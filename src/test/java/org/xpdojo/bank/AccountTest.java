package org.xpdojo.bank;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  1. I can **Deposit** money to accounts
 *  1. I can **Withdraw** money from accounts
 *  1. I can **Transfer** amounts between accounts (if I have the funds)
 *  1. I can print out an Account balance slip (date, time, balance)
 *  1. I can print a statement of account activity (statement)
 *  1. I can apply Statement filters (include just deposits, withdrawal, date)
 *
 *  RIGHT-BICEP
 *
 *  RIGHT
 *  Boundary
 *  Inverse Relationship
 *  Cross Check
 *  Error Conditions
 *  Performance
 */
public class AccountTest {

    @Test
    public void shouldDepositMoneyToTheAccount() {

        // Given
        BigDecimal amount = BigDecimal.valueOf(100);
        Money money = new Money(amount);

        // When
        Account account = new Account();
        account.deposit(money);

        // Then
        assertThat(account.getBalance()).isEqualTo(money); // 100
    }

    @Test
    public void shouldNotAcceptNegativeMoney() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(-1);
        Money money = new Money(amount);

        // When
        Account account = new Account();
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> account.deposit(money));

        // Then
        assertThat(exception.getMessage()).isEqualTo("Money cannot be negative");
    }
}
