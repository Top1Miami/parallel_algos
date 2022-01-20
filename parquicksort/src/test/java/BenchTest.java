import paralllel.ParQuickSortFilterBlock;
import paralllel.ParQuickSortNoFilter;
import sequential.SeqQuickSort;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import static utils.Utils.MAX_SIZE;
import static utils.Utils.checkOrderSort;
import static utils.Utils.getArray;

/**
 * -XX:ActiveProcessorCount=4
 *
 * @author dshusharin
 * @since 15.01.2022
 */
public class BenchTest {
    public static void main(String[] argv) {
        System.out.println(Runtime.getRuntime().availableProcessors());

        long allSeqTime = 0;
        long allParTime = 0;
        long allParTimeBlock = 0;

        for (int i = 0; i < 5; i++) {
            int[] array = getArray();

            int[] seqArray = Arrays.copyOf(array, MAX_SIZE);
            int[] parArray = Arrays.copyOf(array, MAX_SIZE);
            int[] parArrayBlock = Arrays.copyOf(array, MAX_SIZE);

            long startTimePar = System.currentTimeMillis();
            ForkJoinPool pool = ForkJoinPool.commonPool();
            ParQuickSortNoFilter parQuickSort = new ParQuickSortNoFilter(parArray, 0, MAX_SIZE - 1);
            pool.invoke(parQuickSort);
            long parTime = System.currentTimeMillis() - startTimePar;

            allParTime += parTime;
            System.out.println("It number: " + i + " par time: " + convertTimeToSeconds(parTime));

            long startTimeParBlock = System.currentTimeMillis();
            pool = ForkJoinPool.commonPool();
            ParQuickSortFilterBlock parQuickSortFilterBlock = new ParQuickSortFilterBlock(parArrayBlock, 0, MAX_SIZE - 1);
            pool.invoke(parQuickSortFilterBlock);
            long parTimeBlock = System.currentTimeMillis() - startTimeParBlock;

            allParTimeBlock += parTimeBlock;
            System.out.println("It number: " + i + " par block time: " + convertTimeToSeconds(parTimeBlock));

            long startTimeSeq = System.currentTimeMillis();
            SeqQuickSort.quickSort(seqArray, 0, MAX_SIZE - 1);
            long seqTime = System.currentTimeMillis() - startTimeSeq;

            allSeqTime += seqTime;
            System.out.println("It number: " + i + " seq time: " + convertTimeToSeconds(seqTime));

            if (!checkOrderSort(parArray)) {
                System.out.println("Sorted incorrectly");
                return;
            }

            if (!checkOrderSort(parArrayBlock)) {
                System.out.println("Sorted incorrectly");
                return;
            }
            System.out.println("Sorted correctly");
        }

        Double meanSeqTime = convertTimeToSeconds(allSeqTime) / 5.0;
        Double meanParTime = convertTimeToSeconds(allParTime) / 5.0;
        Double meanParTimeBlock = convertTimeToSeconds(allParTimeBlock) / 5.0;

        System.out.println("Mean time: seq " + meanSeqTime + ", par " + meanParTime + ", par block " + meanParTimeBlock);
        System.out.println("Par better than seq with: " + meanSeqTime / meanParTime);
        System.out.println("Par block better than seq with: " + meanSeqTime / meanParTimeBlock);
    }

    private static double convertTimeToSeconds(long millis) {
        return 1.0 * millis / 1000;
    }
}
