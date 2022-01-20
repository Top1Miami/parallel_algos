package paralllel;

import sequential.SeqQuickSort;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static utils.Utils.BLOCK;

/**
 * @author dshusharin
 * @since 16.01.2022
 */
public class ParQuickSort extends RecursiveTask<Integer> {
    public final int[] ar;
    private final int l;
    private final int r;

    public ParQuickSort(int[] ar, int l, int r) {
        this.ar = ar;
        this.l = l;
        this.r = r;
    }

    @Override
    protected Integer compute() {
        if (r - l < BLOCK) {
            SeqQuickSort.quickSort(ar, l, r);
            return null;
        }

        int mid = ar[(l + r) / 2];
        int[] left = new Filter(ar, x -> x < mid ? 1 : 0).compute();
        int[] middle = new Filter(ar, x -> x == mid ? 1 : 0).compute();
        int[] right = new Filter(ar, x -> x > mid ? 1 : 0).compute();

        ParQuickSort leftPart = new ParQuickSort(left, 0, left.length - 1);
        ParQuickSort rightPart = new ParQuickSort(right, 0, right.length - 1);
        leftPart.fork();
        rightPart.compute();
        leftPart.join();

        ForkJoinPool pool = ForkJoinPool.commonPool();
        ParFor mergeArr = new ParFor((ll, rr) -> {
            for (int i = ll; i <= rr; i++) {
                ar[i] = left[i];
            }
        }, 0, left.length - 1);
        pool.invoke(mergeArr);
        mergeArr = new ParFor((ll, rr) -> {
            for (int i = ll; i <= rr; i++) {
                ar[i + left.length] = middle[i];
            }
        }, 0, middle.length - 1);
        pool.invoke(mergeArr);
        mergeArr = new ParFor((ll, rr) -> {
            for (int i = ll; i <= rr; i++) {
                ar[i + left.length + middle.length] = right[i];
            }
        }, 0, right.length - 1);
        pool.invoke(mergeArr);
        return null;
    }
}
