package org.example;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    protected static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    protected static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    protected static BlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);

    protected static final Map<String, Integer> topSize = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        topSize.put("a", 0);
        topSize.put("b", 0);
        topSize.put("c", 0);

        Thread[] threads = new Thread[4];
        int length = 100_000;
        String text = "abc";

        threads[0] = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    queue1.put(generateText(text, length));
                    queue2.put(generateText(text, length));
                    queue3.put(generateText(text, length));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        threads[1] = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                AtomicInteger quantityA = new AtomicInteger(0);
                try {
                    StringBuilder builder = new StringBuilder(queue1.take());
                    builder.chars().filter(n -> n == 'a').forEach(n -> quantityA.incrementAndGet());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                addMap("a", quantityA.get());
            }
        });


        threads[2] = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                AtomicInteger quantityB = new AtomicInteger(0);
                try {
                    StringBuilder builder = new StringBuilder(queue2.take());
                    builder.chars().filter(n -> n == 'b').forEach(n -> quantityB.incrementAndGet());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                addMap("b", quantityB.get());
            }
        });


        threads[3] = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                AtomicInteger quantityC = new AtomicInteger(0);
                try {
                    StringBuilder builder = new StringBuilder(queue3.take());
                    builder.chars().filter(n -> n == 'c').forEach(n -> quantityC.incrementAndGet());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                addMap("c", quantityC.get());
            }
        });

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("max size 'a' " + topSize.get("a"));
        System.out.println("max size 'b' " + topSize.get("b"));
        System.out.println("max size 'c' " + topSize.get("c"));

        long finish = System.currentTimeMillis();
        System.out.println("time " + (finish - start));
    }

    private static void addMap(String key, int value) {
        if (topSize.containsKey(key) & topSize.get(key) < value) {
            topSize.put(key, value);
        }
    }

    private static String generateText(String letter, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letter.charAt(random.nextInt(letter.length())));
        }
        return text.toString();
    }
}
