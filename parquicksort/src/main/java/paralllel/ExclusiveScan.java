package paralllel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author dshusharin
 * @since 16.01.2022
 */
public class ExclusiveScan {
    private final int[] ar;
    private final int[] tree;
    private final int[] res;

    public ExclusiveScan(int[] ar) {
        this.ar = ar;
        tree = new int[2 * ar.length];
        res = new int[ar.length];
    }

    public int[] compute() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        SiftUp up = new SiftUp(ar, tree, 0, ar.length - 1, 0);
        up.compute();
        SiftDown down = new SiftDown(ar, tree, res, 0, ar.length - 1, 0, 0);
        pool.invoke(down);
        return res;
    }

    public static class SiftUp extends RecursiveTask<Integer> {
        private final int[] ar;
        private final int[] tree;
        private final int l;
        private final int r;
        private final int i;

        public SiftUp(int[] ar, int[] tree, int l, int r, int i) {
            this.ar = ar;
            this.tree = tree;
            this.l = l;
            this.r = r;
            this.i = i;
        }

        @Override
        protected Integer compute() {
            if (r == l) {
                return ar[l];
            }

            int mid = (l + r) / 2;
            SiftUp left = new SiftUp(ar, tree, l, mid, 2 * i + 1);
            SiftUp right = new SiftUp(ar, tree, mid + 1, r, 2 * i + 2);
            left.fork();
            return tree[i] = right.compute() + left.join();
        }
    }

    public static class SiftDown extends RecursiveTask<Integer> {
        private final int[] ar;
        private final int[] tree;
        private final int[] res;
        private final int l;
        private final int r;
        private final int i;
        private final int sumLeft;

        public SiftDown(int[] ar, int[] tree, int[] res, int l, int r, int i, int sumLeft) {
            this.ar = ar;
            this.tree = tree;
            this.res = res;
            this.l = l;
            this.r = r;
            this.i = i;
            this.sumLeft = sumLeft;
        }

        @Override
        protected Integer compute() {
            if (r == l) {
                res[l] = sumLeft;
                return null;
            }

            int mid = (l + r) / 2;
            int newSumLeft = r - l == 1 ? ar[l] : tree[2 * i + 1];
            SiftDown left = new SiftDown(ar, tree, res, l, mid, 2 * i + 1, sumLeft);
            SiftDown right = new SiftDown(ar, tree, res, mid + 1, r, 2 * i + 2, sumLeft + newSumLeft);
            left.fork();
            right.compute();
            left.join();
            return null;
        }
    }
}
