package paralllel;

import java.util.concurrent.ForkJoinPool;
import java.util.function.UnaryOperator;

/**
 * @author dshusharin
 * @since 16.01.2022
 */
public class Filter {
    private final int[] ar;
    private final UnaryOperator<Integer> f;

    public Filter(int[] ar, UnaryOperator<Integer> f) {
        this.ar = ar;
        this.f = f;
    }

    public int[] compute() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        int[] flags = new int[ar.length];
        ParFor parFor = new ParFor((l, r) -> {
            for (int i = l; i <= r; i++) {
                flags[i] = f.apply(ar[i]);
            }
        }, 0, ar.length - 1);
        pool.invoke(parFor);
        InclusiveScan inclusiveScan = new InclusiveScan(flags);
        int[] scanned = inclusiveScan.compute();
        int[] res = new int[scanned[scanned.length - 1]];

        ParForExact parForExact = new ParForExact((l) -> {
            if (flags[l] == 1) {
                int ind = scanned[l] - 1;
                res[ind] = ar[l];
            }
        }, 0, ar.length - 1);

        pool.invoke(parForExact);
        return res;
    }
}
