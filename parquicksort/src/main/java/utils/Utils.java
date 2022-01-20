package utils;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author dshusharin
 * @since 15.01.2022
 */
public final class Utils {
    public static final int BLOCK = 100000;
    public static final int MAX_SIZE = 100000000;

    private Utils() {}

    private static final Random random = new Random();

    public static int[] getArray() {
        return getArray(MAX_SIZE);
    }

    public static int[] getArray(int size) {
        return IntStream.generate(random::nextInt).limit(size).toArray();
    }

    public static int[] getArrayWithBound(int size, int bound) {
        return IntStream.generate(() -> random.nextInt(bound)).limit(size).toArray();
    }

    public static void printArray(int[] ar) {
        for (int el : ar) {
            System.out.print(el + " ");
        }
        System.out.println();
    }

    public static boolean checkOrderSort(int[] ar) {
        for (int i = 0; i < ar.length - 1; i++) {
            if (ar[i] > ar[i + 1]) {
                System.out.println("problem at " + i + " with " + ar[i] + " " + ar[i + 1]);
                return false;
            }
        }
        return true;
    }
}
