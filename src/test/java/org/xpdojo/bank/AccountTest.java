package org.xpdojo.bank;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  [X] I can **Deposit** money to accounts
 *  [X] I can **Withdraw** money from accounts
 *  [ ] I can **Transfer** amounts between accounts (if I have the funds)
 *  [ ] I can print out an Account balance slip (date, time, balance)
 *  [ ] I can print a statement of account activity (statement)
 *  [ ] I can apply Statement filters (include just deposits, withdrawal, date)
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

    @Test
    public void shouldWithdrawMoneyFromTheAccount() {
        // Given
        Account account = new Account();
        BigDecimal depositAmount = BigDecimal.valueOf(200);
        BigDecimal withdrawAmount = BigDecimal.valueOf(100);
        Money depositMoney = new Money(depositAmount);
        Money withdrawMoney = new Money(withdrawAmount);

        // When
        account.deposit(depositMoney);
        account.withdraw(withdrawMoney);

        // Then
        assertThat(account.getBalance()).isEqualTo(new Money(BigDecimal.valueOf(100)));
    }

    @Test
    public void shouldNotAllowWithdrawalWhenInsufficientFunds() {
        // Given
        Account account = new Account();
        BigDecimal withdrawAmount = BigDecimal.valueOf(100);
        Money withdrawMoney = new Money(withdrawAmount);

        // When
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> account.withdraw(withdrawMoney));

        // Then
        assertThat(exception.getMessage()).isEqualTo("Insufficient funds");
    }
}
