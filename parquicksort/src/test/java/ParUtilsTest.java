import org.junit.Test;
import paralllel.ExclusiveScan;
import paralllel.Filter;
import paralllel.FilterBlock;
import paralllel.ParFor;
import paralllel.ParQuickSort;
import paralllel.InclusiveScan;
import paralllel.ParQuickSortFilterBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import static utils.Utils.checkOrderSort;
import static utils.Utils.getArray;
import static utils.Utils.printArray;
import static org.junit.Assert.assertTrue;

/**
 * @author dshusharin
 * @since 15.01.2022
 */
public class ParUtilsTest {
    @Test
    public void testParFor() {
        int[] array = getArray(100);
        int[] copiedArray = Arrays.copyOf(array, array.length);
        BiConsumer<Integer, Integer> function = (l, r) -> {
            for (int i = l; i <= r; i++) {
                array[i] += 1;
            }
        };
        ParFor parFor = new ParFor(function, 0, array.length - 1);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(parFor);
        printArray(array);
        printArray(copiedArray);
        assertTrue(checkParFor(array, copiedArray));
    }

    @Test
    public void testParSort() {
        int[] array = {3, 10, 1, 12, 5, 6, 2};
        ForkJoinPool pool = ForkJoinPool.commonPool();
        ParQuickSort parQuickSort = new ParQuickSort(array, 0, array.length - 1);
        pool.invoke(parQuickSort);
        printArray(parQuickSort.ar);
    }

    @Test
    public void testParSortRandom() {
        int[] array = getArray(100000000);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        ParQuickSort parQuickSort = new ParQuickSort(array, 0, array.length - 1);
        pool.invoke(parQuickSort);
//        printArray(parQuickSort.ar);
        assertTrue(checkOrderSort(parQuickSort.ar));
    }

    @Test
    public void testParSortFilterBlock() {
        int[] array = {3, 10, 1, 12, 5, 6, 2};
        ForkJoinPool pool = ForkJoinPool.commonPool();
        ParQuickSortFilterBlock parQuickSort = new ParQuickSortFilterBlock(array, 0, array.length - 1);
        pool.invoke(parQuickSort);
        printArray(parQuickSort.ar);
    }

    @Test
    public void testParSortFilterBlockRandom() {
        int[] array = getArray(100000000);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        ParQuickSortFilterBlock parQuickSort = new ParQuickSortFilterBlock(array, 0, array.length - 1);
        pool.invoke(parQuickSort);
//        printArray(parQuickSort.ar);
        assertTrue(checkOrderSort(parQuickSort.ar));
    }

    @Test
    public void testFilter() {
        int[] array = {3, 10, 1, 12, 5, 6, 2};
        Filter filter = new Filter(array, x -> x > 5 ? 1 : 0);
        int[] res = filter.compute();
        printArray(res);
    }

    @Test
    public void testFilter2() {
        int[] array = {612417565, 2066603279, 414376752, 1989601434};
        Filter filter = new Filter(array, x -> x > 5 ? 1 : 0);
        int[] res = filter.compute();
        printArray(res);
    }

    @Test
    public void testFilterRandom() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration " + i);
            int[] array = getArray(100000000);
            UnaryOperator<Integer> unaryOperator = x -> x > 5 ? 1 : 0;
            int[] linearFilter = linearFilter(array, unaryOperator);
            Filter filter = new Filter(array, unaryOperator);
            int[] res = filter.compute();
            assertTrue(equalArrays(linearFilter, res));
        }
    }

    @Test
    public void testFilterBlock() {
        int[] array = {3, 10, 1, 12, 5, 6, 2};
        FilterBlock filter = new FilterBlock(array, x -> x > 5 ? 1 : 0);
        int[] res = filter.compute();
        printArray(res);
    }

    @Test
    public void testFilterBlock2() {
        int[] array = getArray(20);
        UnaryOperator<Integer> unaryOperator = x -> x > 5 ? 1 : 0;
        FilterBlock filter = new FilterBlock(array, unaryOperator);
        int[] res = filter.compute();
        int[] linearFilter = linearFilter(array, unaryOperator);
        printArray(array);
        printArray(res);
        printArray(linearFilter);
        assertTrue(equalArrays(linearFilter, res));
    }

    @Test
    public void testFilterBlockRandom() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Iteration " + i);
            int[] array = getArray(100000000);
            UnaryOperator<Integer> unaryOperator = x -> x > 5 ? 1 : 0;
            int[] linearFilter = linearFilter(array, unaryOperator);
            FilterBlock filter = new FilterBlock(array, unaryOperator);
            int[] res = filter.compute();
            assertTrue(equalArrays(linearFilter, res));
        }
    }

    @Test
    public void testScan() {
        int[] array = {3, 10, 1, 12, 5, 6, 2};
        int[] baseLine = {3, 13, 14, 26, 31, 37, 39};
        InclusiveScan inclusiveScan = new InclusiveScan(array);
        int[] res = inclusiveScan.compute();
        assertTrue(equalArrays(baseLine, res));
    }

    @Test
    public void testExclusiveScan() {
        int[] array = {3, 10, 1, 12, 5, 6, 2};
        int[] baseLine = {3, 13, 14, 26, 31, 37, 39};
        ExclusiveScan exclusiveScan = new ExclusiveScan(array);
        int[] res = exclusiveScan.compute();
        int[] linear = linearExclusiveScan(array);
        printArray(array);
        printArray(res);
        printArray(baseLine);
        printArray(linear);
    }

    @Test
    public void testExclusiveScanRandom() {
        for (int i = 0; i < 10; i++) {
            int[] array = getArray(100000000);
            int[] linearScan = linearExclusiveScan(array);
            ExclusiveScan exclusiveScan = new ExclusiveScan(array);
            int[] res = exclusiveScan.compute();
            assertTrue(equalArrays(linearScan, res));
        }
    }

    @Test
    public void testScanOnes() {
        int[] array = {0, 1, 0, 0, 1, 1};
        int[] baseLine = {0, 1, 1, 1, 2, 3};
        InclusiveScan inclusiveScan = new InclusiveScan(array);
        int[] res = inclusiveScan.compute();
        assertTrue(equalArrays(baseLine, res));
    }

    @Test
    public void testScanOnes2() {
        int[] array = {1, 1, 0, 0, 1, 1};
        int[] baseLine = {1, 2, 2, 2, 3, 4};
        InclusiveScan inclusiveScan = new InclusiveScan(array);
        int[] res = inclusiveScan.compute();
        assertTrue(equalArrays(baseLine, res));
    }

    @Test
    public void testScanRandom() {
        for (int i = 0; i < 10; i++) {
            int[] array = getArray(100000000);
            int[] linearScan = linearScan(array);
            InclusiveScan inclusiveScan = new InclusiveScan(array);
            int[] res = inclusiveScan.compute();
            assertTrue(equalArrays(linearScan, res));
        }
    }

    private static boolean equalArrays(int[] first, int[] second) {
        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) {
                System.out.println(i);
                return false;
            }
        }
        return true;
    }

    private static int[] linearScan(int[] ar) {
        int[] copyOf = Arrays.copyOf(ar, ar.length);
        for (int i = 1; i < copyOf.length; i++) {
            copyOf[i] += copyOf[i - 1];
        }
        return copyOf;
    }

    private static int[] linearExclusiveScan(int[] ar) {
        int[] copyOf = new int[ar.length];
        for (int i = 1; i < copyOf.length; i++) {
            copyOf[i] = ar[i - 1];
        }
        for (int i = 1; i < copyOf.length; i++) {
            copyOf[i] += copyOf[i - 1];
        }
        return copyOf;
    }

    private static int[] linearFilter(int[] ar, UnaryOperator<Integer> unaryOperator) {
        List<Integer> list = new ArrayList<>();
        for (int j : ar) {
            if (unaryOperator.apply(j) == 1) {
                list.add(j);
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    private static boolean checkParFor(int[] array, int[] copiedArray) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] - 1 != copiedArray[i]) {
                return false;
            }
        }
        return true;
    }
}
