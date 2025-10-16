package org.xpdojo.bank;

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
    public void shouldIncreaseTheBalanceWhenDepositingAnAmount() {

        // Given
        int amount = 100;
        Money money = new Money(amount);
        Account account = new Account();

        // When
        account.deposit(money);

        // Then
        assertThat(account.getBalance().getAmount()).isEqualTo(amount); // 100
    }
}
