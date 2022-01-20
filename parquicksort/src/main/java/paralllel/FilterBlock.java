package paralllel;

import java.util.concurrent.ForkJoinPool;
import java.util.function.UnaryOperator;

import static utils.Utils.BLOCK;

/**
 * @author dshusharin
 * @since 16.01.2022
 */
public class FilterBlock {
    private final int[] ar;
    private final UnaryOperator<Integer> f;

    public FilterBlock(int[] ar, UnaryOperator<Integer> f) {
        this.ar = ar;
        this.f = f;
    }

    public int[] compute() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        int numberOfBlocks = (ar.length + BLOCK - 1) / BLOCK;
        int[] blocked_flags = new int[numberOfBlocks];

        ParForExact parFor = new ParForExact((blockNumber) -> {
            int l = blockNumber * BLOCK;
            int r = Math.min((blockNumber + 1) * BLOCK - 1, ar.length - 1);
            for (int j = l; j <= r; j++) {
                blocked_flags[blockNumber] += f.apply(ar[j]);
            }
        }, 0, numberOfBlocks);
        pool.invoke(parFor);

        ExclusiveScan exclusiveScan = new ExclusiveScan(blocked_flags);
        int[] scanned = exclusiveScan.compute();
        int numberToRemain = scanned[scanned.length - 1] + blocked_flags[blocked_flags.length - 1];
        int[] res = new int[numberToRemain];

        parFor = new ParForExact((blockNumber) -> {
            int l = blockNumber * BLOCK;
            int r = Math.min((blockNumber + 1) * BLOCK - 1, ar.length - 1);
            for (int j = l; j <= r; j++) {
                if (f.apply(ar[j]) == 1) {
                    int pos = scanned[blockNumber];
                    res[pos] = ar[j];
                    scanned[blockNumber]++;
                }
            }
        }, 0, numberOfBlocks);
        pool.invoke(parFor);
        return res;
    }
}
