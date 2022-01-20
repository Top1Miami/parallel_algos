package paralllel;

import sequential.SeqQuickSort;

import java.util.concurrent.RecursiveTask;

/**
 * @author dshusharin
 * @since 15.01.2022
 */
public class ParQuickSortNoFilter extends RecursiveTask<Integer> {
    private static final int BLOCK = 1000;
    private final int[] ar;
    private final int l;
    private final int r;

    public ParQuickSortNoFilter(int[] ar, int l, int r) {
        this.ar = ar;
        this.l = l;
        this.r = r;
    }

    @Override
    protected Integer compute() {
        if (l >= r) {
            return null;
        } else if (r - l < BLOCK) {
            SeqQuickSort.quickSort(ar, l, r);
        } else {
            int partition = SeqQuickSort.partition(ar, l, r);
            ParQuickSortNoFilter left = new ParQuickSortNoFilter(ar, l, partition);
            ParQuickSortNoFilter right = new ParQuickSortNoFilter(ar, partition + 1, r);

            left.fork();
            right.compute();
            left.join();
        }
        return null;
    }
}
