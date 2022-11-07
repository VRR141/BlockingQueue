package org.example;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final int lettersCount = 10_000;
    private static final int lettersLength = 100_000;
    private static final String letters = "abc";
    private static final int blockingQueueCapacity = 100;

    private static final BlockingQueue<String> blockingQueueA = new ArrayBlockingQueue<>(blockingQueueCapacity);
    private static final BlockingQueue<String> blockingQueueB = new ArrayBlockingQueue<>(blockingQueueCapacity);
    private static final BlockingQueue<String> blockingQueueC = new ArrayBlockingQueue<>(blockingQueueCapacity);
    private static final List<BlockingQueue<String>> listQueues = Arrays.asList(blockingQueueA, blockingQueueB, blockingQueueC);
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);
    private static final List<Integer> listA = new ArrayList<>();
    private static final List<Integer> listB = new ArrayList<>();
    private static final List<Integer> listC = new ArrayList<>();




    public static void main(String[] args) throws InterruptedException {
        Thread generate = new Thread(() -> {
            for (int i = 0; i < lettersCount; i++) {
                String temp = generateText(letters, lettersLength);
                for (BlockingQueue<String> bq : listQueues) {
                    try {
                        bq.put(temp);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        Thread findA = new Thread(() -> {
            for (int i = 0; i < lettersCount; i++) {
                int a = 0;
                try {
                    String temp = blockingQueueA.take();
                    char[] chars = temp.toCharArray();
                    for (char aChar : chars) {
                        if (aChar == 'a') {
                            a++;
                        }
                    }
                    listA.add(a);
                    atomicCounter.getAndIncrement();
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread findB = new Thread(() -> {
            for (int i = 0; i < lettersCount; i++) {
                int b = 0;
                try {
                    String temp = blockingQueueB.take();
                    char[] chars = temp.toCharArray();
                    for (char aChar : chars) {
                        if (aChar == 'b') {
                            b++;
                        }
                    }
                    listB.add(b);
                    atomicCounter.getAndIncrement();
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread findC = new Thread(() -> {
            for (int i = 0; i < lettersCount; i++) {
                int c = 0;
                try {
                    String temp = blockingQueueC.take();
                    char[] chars = temp.toCharArray();
                    for (char aChar : chars) {
                        if (aChar == 'c') {
                            c++;
                        }
                    }
                    listC.add(c);
                    atomicCounter.getAndIncrement();
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        generate.start();
        findA.start();
        findB.start();
        findC.start();

        generate.join();
        findA.join();
        findB.join();
        findC.join();

       String answer = (atomicCounter.get() == (lettersCount * listQueues.size())) ?
               String.format("All ok, counter = %d", lettersCount * listQueues.size()) :
               "something wrong";

        System.out.println(answer);
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}