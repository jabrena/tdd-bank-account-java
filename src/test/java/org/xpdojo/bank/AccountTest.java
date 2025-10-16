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
 */
public class AccountTest {

    @Test
    public void depositAnAmountToIncreaseTheBalance() {

        // Given
        int amount = 100;
        Account account = new Account();

        // When
        account.deposit(amount);

        // Then
        assertThat(account.getBalance()).isEqualTo(amount); // 100
    }
}
