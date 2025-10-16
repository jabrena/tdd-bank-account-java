package org.xpdojo.bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

public class MoneyTest {

    @Test
    public void shouldAddTwoMoneyObjects() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(100);
        Money money1 = new Money(amount);
        Money money2 = new Money(amount);

        // When
        Money result = money1.add(money2);
        assertThat(result.getAmount()).isEqualTo(amount.multiply(new BigDecimal(2)));
    }
}
