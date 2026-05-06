package com.example.warehouse.counter;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class RaceConditionTest {

    private static final int THREAD_COUNT = 50;
    private static final int INCREMENTS_PER_THREAD = 1000;
    private static final int EXPECTED_TOTAL = THREAD_COUNT * INCREMENTS_PER_THREAD;

    @Test
    void unsafeCounter_shouldNotUseSynchronization() throws NoSuchMethodException {
        Method increment = UnsafeCounter.class.getDeclaredMethod("increment");

        assertFalse(Modifier.isSynchronized(increment.getModifiers()));
    }

    @Test
    void synchronizedCounter_shouldBeCorrect() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.increment();
                }
                latch.countDown();
            });
        }
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdownNow();

        long finalValue = counter.getValue();
        System.out.println("SynchronizedCounter final value = " + finalValue + ", expected = " + EXPECTED_TOTAL);
        assertEquals(EXPECTED_TOTAL, finalValue);
    }

    @Test
    void atomicCounter_shouldBeCorrect() throws InterruptedException {
        AtomicCounter counter = new AtomicCounter();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.increment();
                }
                latch.countDown();
            });
        }
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdownNow();

        long finalValue = counter.getValue();
        System.out.println("AtomicCounter final value = " + finalValue + ", expected = " + EXPECTED_TOTAL);
        assertEquals(EXPECTED_TOTAL, finalValue);
    }
}
