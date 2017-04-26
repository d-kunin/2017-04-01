package ru.otus.kunin.hw4;

import java.util.Arrays;

public class MemoryLeak {

    /**
     * Size of a single allocated byte[]
     */
    static final int singleMemoryChunkSize = 128;

    /**
     * How many objects to create per iteration
     */
    static final int numberOfObjectsToAllocatePerIteration = 512;

    /**
     * How many iterations to do at max before OOM happens
     */
    static final int maxNumberOfIterations = 10_000_000;

    /**
     * What ratio of objects from a single batch should leak
     * range [0 .. 1]
     * 0 - nothing leaks
     * 1 - everything leaks
     */
    static final double ratioOfLeakedObjectsPerBatch = .07;

    /**
     *
     * @return true if OOM happens, false otherwise
     */
    public static boolean leak() {
        try {
            leakLoop();
            return false;
        } catch (OutOfMemoryError outOfMemoryError) {
            return true;
        }
    }

    private static void leakLoop() {
        Object[][] refs = new Object[maxNumberOfIterations][];
        for (int iteration = 0; iteration < maxNumberOfIterations; ++iteration) {
            refs[iteration] = new Object[numberOfObjectsToAllocatePerIteration];

            for (int batchNumber = 0; batchNumber < numberOfObjectsToAllocatePerIteration; batchNumber++) {
                refs[iteration][batchNumber] = new byte[singleMemoryChunkSize];
                Arrays.fill((byte[])refs[iteration][batchNumber], (byte)1);
            }

            // Let some objects to be garbage collected to keep things more realistic
            final int cleanUntilIndex = (int) Math.round((1 - ratioOfLeakedObjectsPerBatch) * numberOfObjectsToAllocatePerIteration);
            for (int indexToClean = 0; indexToClean < cleanUntilIndex; indexToClean++) {
                refs[iteration][indexToClean] = null;
            }

//            final long memoryLeftKb = Runtime.getRuntime().freeMemory() / 1024;
//            System.out.println("Memory left: " + memoryLeftKb+ "kB");
//            System.out.flush();
            try {
                // todo(dima) increase sleep
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
