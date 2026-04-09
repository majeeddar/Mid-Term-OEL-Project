import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Sorts {

    public static List<Complaint> mergeSort(List<Complaint> arr, Comparator<Complaint> cmp) {
        if (arr.size()<=1) return arr;
        int mid=arr.size()/2;
        List<Complaint> left = mergeSort(new ArrayList<>(arr.subList(0,mid)),cmp);
        List<Complaint> right = mergeSort(new ArrayList<>(arr.subList(mid,arr.size())),cmp);
        return merge(left,right,cmp);
    }

    private static List<Complaint> merge(List<Complaint> a, List<Complaint> b, Comparator<Complaint> cmp) {
        List<Complaint> out=new ArrayList<>();
        int i=0,j=0;
        while (i<a.size() && j<b.size()) {
            if (cmp.compare(a.get(i), b.get(j))<=0) out.add(a.get(i++)); else out.add(b.get(j++));
        }
        while (i<a.size()) out.add(a.get(i++));
        while (j<b.size()) out.add(b.get(j++));
        return out;
    }

    public static void quickSort(List<Complaint> arr, Comparator<Complaint> cmp) {
        quickSortRec(arr, 0, arr.size()-1, cmp);
    }
    private static void quickSortRec(List<Complaint> a, int l, int r, Comparator<Complaint> cmp) {
        if (l>=r) return;
        int p = partition(a,l,r,cmp);
        quickSortRec(a,l,p-1,cmp);
        quickSortRec(a,p+1,r,cmp);
    }
    private static int partition(List<Complaint> a, int l, int r, Comparator<Complaint> cmp) {
        Complaint pivot = a.get(r);
        int i=l;
        for (int j=l;j<r;j++) {
            if (cmp.compare(a.get(j), pivot) <= 0) { swap(a,i,j); i++; }
        }
        swap(a,i,r); return i;
    }
    private static void swap(List<Complaint> a, int i, int j) { Complaint t=a.get(i); a.set(i,a.get(j)); a.set(j,t); }

    public static void insertionSort(List<Complaint> a, Comparator<Complaint> cmp) {
        for (int i=1;i<a.size();i++){
            Complaint key=a.get(i); int j=i-1;
            while (j>=0 && cmp.compare(a.get(j), key) > 0) { a.set(j+1,a.get(j)); j--; }
            a.set(j+1,key);
        }
    }

    public static void selectionSort(List<Complaint> a, Comparator<Complaint> cmp) {
        for (int i=0;i<a.size();i++){
            int min=i;
            for (int j=i+1;j<a.size();j++) if (cmp.compare(a.get(j), a.get(min)) < 0) min=j;
            if (min!=i) swap(a,i,min);
        }
    }
}
