package org.xpdojo.bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive concurrency tests to prove the ReentrantLock implementation
 * provides correct thread safety for Account operations
 */
public class AccountConcurrencyTest {

    @Test
    public void shouldHandleHighConcurrencyDepositsWithoutRaceConditions() throws InterruptedException {
        // Given
        Account account = new Account();
        int numberOfThreads = 50;
        int depositsPerThread = 200;
        BigDecimal depositAmount = BigDecimal.valueOf(1);
        Money money = new Money(depositAmount);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        AtomicInteger successfulDeposits = new AtomicInteger(0);
        AtomicInteger failedDeposits = new AtomicInteger(0);

        // When - All threads start depositing simultaneously
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for signal to start all threads at once

                    for (int j = 0; j < depositsPerThread; j++) {
                        try {
                            account.deposit(money);
                            successfulDeposits.incrementAndGet();
                        } catch (Exception e) {
                            failedDeposits.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start all threads simultaneously
        long startTime = System.currentTimeMillis();
        startLatch.countDown();

        // Wait for all threads to complete
        assertThat(endLatch.await(10, TimeUnit.SECONDS)).isTrue();
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        // Then - Verify no race conditions occurred
        BigDecimal expectedBalance = depositAmount
            .multiply(BigDecimal.valueOf(successfulDeposits.get()));

        assertThat(account.getBalance().getAmount()).isEqualTo(expectedBalance);
        assertThat(failedDeposits.get()).isEqualTo(0);
        assertThat(successfulDeposits.get()).isEqualTo(numberOfThreads * depositsPerThread);

        System.out.printf("High concurrency test: %d threads, %d deposits each, %d total operations in %d ms%n",
            numberOfThreads, depositsPerThread, successfulDeposits.get(), (endTime - startTime));
    }

    @Test
    public void shouldHandleConcurrentReadsAndWritesCorrectly() throws InterruptedException {
        // Given
        Account account = new Account();
        int numberOfWriterThreads = 20;
        int numberOfReaderThreads = 30;
        int operationsPerThread = 100;
        BigDecimal depositAmount = BigDecimal.valueOf(5);
        Money money = new Money(depositAmount);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfWriterThreads + numberOfReaderThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfWriterThreads + numberOfReaderThreads);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);
        AtomicLong totalReadBalance = new AtomicLong(0);

        // When - Multiple threads read and write concurrently
        for (int i = 0; i < numberOfWriterThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    for (int j = 0; j < operationsPerThread; j++) {
                        account.deposit(money);
                        writeCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        for (int i = 0; i < numberOfReaderThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    for (int j = 0; j < operationsPerThread; j++) {
                        Money balance = account.getBalance();
                        assertThat(balance).isNotNull();
                        assertThat(balance.getAmount()).isGreaterThanOrEqualTo(BigDecimal.ZERO);

                        readCount.incrementAndGet();
                        totalReadBalance.addAndGet(balance.getAmount().longValue());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start all threads simultaneously
        startLatch.countDown();

        // Wait for all threads to complete
        assertThat(endLatch.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        // Then - Verify all operations completed successfully
        assertThat(readCount.get()).isEqualTo(numberOfReaderThreads * operationsPerThread);
        assertThat(writeCount.get()).isEqualTo(numberOfWriterThreads * operationsPerThread);

        BigDecimal expectedBalance = depositAmount
            .multiply(BigDecimal.valueOf(writeCount.get()));

        assertThat(account.getBalance().getAmount()).isEqualTo(expectedBalance);

        System.out.printf("Read/Write test: %d reads, %d writes, final balance: %s%n",
            readCount.get(), writeCount.get(), account.getBalance().getAmount());
    }

    @RepeatedTest(5)
    public void shouldMaintainConsistencyUnderStress() throws InterruptedException {
        // Given
        Account account = new Account();
        int numberOfThreads = 100;
        int operationsPerThread = 50;
        BigDecimal depositAmount = BigDecimal.valueOf(2);
        Money money = new Money(depositAmount);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger totalOperations = new AtomicInteger(0);

        // When - Stress test with many threads
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        account.deposit(money);
                        account.getBalance(); // Mix reads and writes
                        totalOperations.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        assertThat(latch.await(15, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        // Then - Verify consistency
        BigDecimal expectedBalance = depositAmount
            .multiply(BigDecimal.valueOf(numberOfThreads))
            .multiply(BigDecimal.valueOf(operationsPerThread));

        assertThat(account.getBalance().getAmount()).isEqualTo(expectedBalance);
        assertThat(totalOperations.get()).isEqualTo(numberOfThreads * operationsPerThread);
    }

    @Test
    public void shouldPreventLostUpdatesInRaceConditions() throws InterruptedException {
        // Given
        Account account = new Account();
        int numberOfThreads = 200;
        BigDecimal depositAmount = BigDecimal.valueOf(1);
        Money money = new Money(depositAmount);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

        // When - All threads try to deposit at exactly the same time
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // All threads wait for the same signal
                    account.deposit(money);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start all threads at exactly the same time
        startLatch.countDown();

        // Wait for all threads to complete
        assertThat(endLatch.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        // Then - Every single deposit should be accounted for (no lost updates)
        BigDecimal expectedBalance = depositAmount.multiply(BigDecimal.valueOf(numberOfThreads));
        assertThat(account.getBalance().getAmount()).isEqualTo(expectedBalance);

        System.out.printf("Race condition test: %d simultaneous deposits, final balance: %s%n",
            numberOfThreads, account.getBalance().getAmount());
    }

    @Test
    public void shouldHandleMixedOperationsSafely() throws InterruptedException {
        // Given
        Account account = new Account();
        int numberOfThreads = 25;
        int operationsPerThread = 40;
        BigDecimal depositAmount = BigDecimal.valueOf(10);
        Money money = new Money(depositAmount);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger depositCount = new AtomicInteger(0);
        AtomicInteger readCount = new AtomicInteger(0);

        // When - Mix of deposit and read operations
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (threadId % 2 == 0) {
                            // Even threads do deposits
                            account.deposit(money);
                            depositCount.incrementAndGet();
                        } else {
                            // Odd threads do reads
                            Money balance = account.getBalance();
                            assertThat(balance.getAmount()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
                            readCount.incrementAndGet();
                        }

                        // Small delay to increase chance of interleaving
                        Thread.yield();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        // Then - Verify all operations completed and balance is correct
        BigDecimal expectedBalance = depositAmount.multiply(BigDecimal.valueOf(depositCount.get()));
        assertThat(account.getBalance().getAmount()).isEqualTo(expectedBalance);

        System.out.printf("Mixed operations test: %d deposits, %d reads, final balance: %s%n",
            depositCount.get(), readCount.get(), account.getBalance().getAmount());
    }
}
