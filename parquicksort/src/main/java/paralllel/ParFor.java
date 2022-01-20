package paralllel;

import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;

import static utils.Utils.BLOCK;

/**
 * @author dshusharin
 * @since 15.01.2022
 */
public class ParFor extends RecursiveTask<Integer> {
    private final BiConsumer<Integer, Integer> function;
    private final int l;
    private final int r;

    public ParFor(BiConsumer<Integer, Integer> function,
                  int l, int r) {
        this.function = function;
        this.l = l;
        this.r = r;
    }

    @Override
    protected Integer compute() {
        if (r - l < BLOCK) {
            function.accept(l, r);
            return null;
        }

        int mid = (l + r) / 2;
        ParFor leftParFor = new ParFor(function, l, mid);
        ParFor rightParFor = new ParFor(function, mid + 1, r);

        leftParFor.fork();
        rightParFor.compute();
        leftParFor.join();
        return null;
    }
}
