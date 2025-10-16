package org.xpdojo.bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoneyTest {

    @Test
    public void shouldAddTwoMoneyObjects() {
        // Given
        int amount = 100;
        Money money1 = new Money(amount);
        Money money2 = new Money(amount);

        // When
        Money result = money1.add(money2);
        assertThat(result.getAmount()).isEqualTo(amount * 2);
    }
}
