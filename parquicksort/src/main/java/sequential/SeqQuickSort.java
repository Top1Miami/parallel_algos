package sequential;

/**
 * @author dshusharin
 * @since 15.01.2022
 */
public class SeqQuickSort {
    public static void quickSort(int[] ar, int l, int r) {
        if (l < r) {
            int q = partition(ar, l, r);
            quickSort(ar, l, q);
            quickSort(ar, q + 1, r);
        }
    }

    public static int partition(int[] ar, int l, int r) {
        int mid = ar[(l + r) / 2];
        int i = l;
        int j = r;
        while (i <= j) {
            while (ar[i] < mid) {
                i++;
            }
            while (ar[j] > mid) {
                j--;
            }
            if (i >= j) {
                break;
            }
            swap(ar, i, j);
            j--;
            i++;
        }
        return j;
    }

    private static void swap(int[] ar, int l, int r) {
        int temp = ar[l];
        ar[l] = ar[r];
        ar[r] = temp;
    }
}
