package paralllel;

import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

/**
 * @author dshusharin
 * @since 16.01.2022
 */
public class ParForExact extends RecursiveTask<Integer> {
    private final Consumer<Integer> function;
    private final int l;
    private final int r;

    public ParForExact(Consumer<Integer> function,
                       int l, int r) {
        this.function = function;
        this.l = l;
        this.r = r;
    }

    @Override
    protected Integer compute() {
        if (r == l) {
            function.accept(l);
            return null;
        }

        int mid = (l + r) / 2;
        ParForExact leftParFor = new ParForExact(function, l, mid);
        ParForExact rightParFor = new ParForExact(function, mid + 1, r);

        leftParFor.fork();
        rightParFor.compute();
        leftParFor.join();
        return null;
    }
}
