import org.junit.Test;
import sequential.SeqQuickSort;

import static utils.Utils.checkOrderSort;
import static utils.Utils.getArray;
import static utils.Utils.printArray;
import static org.junit.Assert.assertTrue;

/**
 * @author dshusharin
 * @since 15.01.2022
 */
public class SeqQuickSortTest {
    @Test
    public void testSeqQuickSort() {
        int[] ar = getArray(100);
        System.out.println("array generated");
        printArray(ar);
        SeqQuickSort.quickSort(ar, 0, ar.length - 1);
        System.out.println("array sorted");
        assertTrue(checkOrderSort(ar));
        printArray(ar);
    }
}
