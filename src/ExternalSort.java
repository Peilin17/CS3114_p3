import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Peilin
 * @version 11/10/2019
 */
public class ExternalSort {
    private static int constHeapSize = 8192;
    private Ascore[] heap;
    private DataInputStream in;
    private int index;
    private DataOutputStream out;
    private File result;
    private int removeIndex = -1;
    private int num = 0;
    private int size;
    private boolean finishReading;
    private int runsize = 0;
    private int heapSize = 0;
    private ArrayList<Integer> runlength = new ArrayList<Integer>();
    private boolean deathHeap = false;
    private Ascore last;

    
    /**
     * constructor
     * @param filename filename
     * @throws FileNotFoundException
     */
    public ExternalSort(String filename) throws FileNotFoundException {
        index = 0;
        finishReading = false;
        File f = new File("runFile.data");
        result = new File("result.data");
        // inter = new ArrayList<Ascore>();
        in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(filename)));
        heap = new Ascore[8192];
        FileOutputStream file = new FileOutputStream(f);
        out = new DataOutputStream(file);
    }

    /**
     * sort the data
     * @throws IOException
     */
    public void sortData() throws IOException {
        read8block();
        while (!finishReading) {
            if (deathHeap) {
                last = null;
                deathHeap = false;
                runlength.add(runsize);
                runsize = 0;
            }
            outputBuffer(readOneBlock());
        }
        runlength.add(runsize);
        runsize = 0;
        clearHeap();
        closeInBuffer();
        closeOutBuffer();
        mutiMerge();

    }

    /**
     * read 8 block
     * @return 
     */
    public void read8block() {
        try {
            for (int i = 0; i < 8192; i++) {
                long pid = in.readLong();
                double score = in.readDouble();
                // store in heap, raw.
                Ascore t = new Ascore(pid, score);
                heap[i] = t;
                heapSize++;
            }
            for (int i = (heapSize - 1) / 2; i >= 0; i--) {
                // System.out.println(i);
                sift(i, heapSize);
            } // heap build finish

        } 
        catch (Exception e) {
            for (int i = (heapSize - 1) / 2; i >= 0; i--) {
                // System.out.println(i);
                sift(i, heapSize);
            }
            return;
        }
        size = heapSize;
    }

    /**
     * @return outBuffer
     */
    private ArrayList<Ascore> readOneBlock() {
        ArrayList<Ascore> outBuffer = new ArrayList<Ascore>();
        try {

            for (int i = 0; i < 1024; i++) {
                if (size == 0) {
                    for (int l = (heapSize - 1) / 2; l >= 0; l--) {
                        sift(l, heapSize);
                    }
                    size = heapSize;
                    while (heap[0].compareTo(last) == 1) {
                        if (size == 0) {
                            deathHeap = true;
                            size = heapSize;
                            for (int l = (heapSize - 1) / 2; l >= 0; l--) {
                                sift(l, heapSize);
                            }
                            return outBuffer;
                        }
                        Ascore temp = heap[0];
                        heap[0] = heap[size - 1];
                        heap[size - 1] = temp;

                        size--;

                        sift(0, size);
                    }

                }
                long pid = in.readLong();
                double score = in.readDouble();
                Ascore t = new Ascore(pid, score);
                outBuffer.add(heap[0]);
                runsize++;
                last = heap[0];

                if (t.compareTo(last) == 1) {
                    heap[0] = heap[size - 1];
                    heap[size - 1] = t;
                    size--;
                }
                else {
                    heap[0] = t;
                }
                sift(0, size);
            }
        } 
        catch (Exception e) {
            finishReading = true;
            return outBuffer;
        }

        return outBuffer;

    }

    /**
     * @param i
     * @param size
     */
    private void sift(int i, int sizeH) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        Ascore vl;
        Ascore vr;
        Ascore v;
        if (l < sizeH) {
            vl = heap[l];
        }
        else {
            vl = null;
        }
        if (r < sizeH) {
            vr = heap[r];
        }
        else {
            vr = null;
        }
        v = heap[i];
        if (v.compareTo(vl) == 1 && v.compareTo(vr) == 1) {
            return;
        }       
        if (vl.compareTo(vr) == 1) {
            heap[i] = vl;
            heap[l] = v;
            sift(l, sizeH);
        }
        else {
            heap[i] = vr;
            heap[r] = v;
            sift(r, sizeH);
        }
        return;
    }

    /**
     * output the outBUffer
     * @param h 
     * @throws IOException
     */
    private void outputBuffer(ArrayList<Ascore> h) throws IOException {
        // write into disk output outBuffer.
        // remember to add the inter.
        if (h.size() > 0) {
            // out.writeInt(h.size());
            for (int i = 0; i < h.size(); i++) {
                out.writeLong(h.get(i).getPid());
                out.writeDouble(h.get(i).getScore());

            }

            out.flush();
            index++;
        }

    }

    /**
     * clear the heaps
     * 
     * @throws IOException
     */
    private void clearHeap() throws IOException {

        int heapSizeHelp = heapSize;
        int i = 0;
        for (int j = (heapSize - 1) / 2; j >= 0; j--) {
            sift(j, heapSize);
        } // heap build finish
        for (; i < heapSizeHelp; i++) {
            Ascore t = extractMax();
            out.writeLong(t.getPid());
            out.writeDouble(t.getScore());

            if (i > 0 && (i + 1) % 1024 == 0) {
                index++;
                out.flush();
            }
        }
        out.flush();
        runlength.add(heapSizeHelp);
    }

    /**
     * 
     * @throws IOException
     */
    private void mutiMerge() throws IOException {
        ArrayList<ArrayList<Ascore>> runs = new ArrayList<ArrayList<Ascore>>();
        if (index == 0) {
            index++;
        }
        int[] pivot = new int[index];
        if (constHeapSize / index < 1024) { 
            // 取每一个run的前 （总8block容量/run的数量）
            int recordsPerRun = constHeapSize / runlength.size();

            ArrayList<Ascore> ascoreArray = new ArrayList<Ascore>();

            DataInputStream writein = 
                    new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream("runFile.data")));
            for (int i = 0; i < runlength.size(); i++) {
                // 这里第一次提取，for loop里判断recordsPerRun是否够每一个 run， 最后一个run可能很少。
                // ArrayList<Ascore> run = new ArrayList<Ascore>();
                int j = 0;
                for (; 
                        j < (runlength.get(i) < 
                                recordsPerRun ? runlength.get(i) : 
                                    recordsPerRun); j++) {

                    Ascore t = 
                            new Ascore(writein.readLong(), 
                                    writein.readDouble());
                    t.setIndex(i);
                    ascoreArray.add(t);

                }
                pivot[i] = j;
                // System.out.println(runlength.get(i) +" "+ i);
                for (; j < runlength.get(i); j++)// 这里读完当前run, 为了去读下一个
                {
                    // System.out.println(i + ", " +j);
                    writein.readLong();
                    writein.readDouble();
                }

            }
            writein.close();
            // next is output part
            /*
             * 开启写入： 正式版要求写入原文件，这里先新建一个测试省的复制粘贴
             */
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            // int runsCount = 0;
            // System.out.println(index);
            int[] highscorePerRun = new int[runlength.size()];
            while (ascoreArray.size() != 0) {

                quicksort(ascoreArray, 0, ascoreArray.size() - 1);
                // insertion(ascoreArray);
                for (int i = 0; 
                        i < (ascoreArray.size() 
                                < recordsPerRun ? ascoreArray.size() : 
                                    recordsPerRun); i++) {
                    fin.writeLong(ascoreArray.get(0).getPid());
                    fin.writeDouble(ascoreArray.get(0).getScore());
                    // System.out.println(ascoreArray.get(0).getScore());
                    int group = ascoreArray.get(0).getIndex();
                    highscorePerRun[group]++;
                    ascoreArray.remove(0);
                }
                fin.flush();
                DataInputStream addnew = new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream("runFile.data")));
                for (int i = 0; i < runlength.size(); i++) {

                    if (pivot[i] >= runlength.get(i)) {
                        for (int j = 0; j < runlength.get(i); j++) {
                            addnew.readLong();
                            addnew.readDouble();
                        }
                        // ascoreArray.remove(0);
                        continue;
                    }
                    for (int k = 0; k < pivot[i]; k++) {
                        addnew.readLong();
                        addnew.readDouble();
                    }
                    for (int k = pivot[i]; 
                            k < (highscorePerRun[i] 
                                    + pivot[i] < runlength.get(i)
                            ? highscorePerRun[i] + pivot[i]
                            : runlength.get(i)); k++) {
                        Ascore temp = 
                                new Ascore(addnew.readLong(), 
                                        addnew.readDouble());
                        ascoreArray.add(temp);
                    }
                    pivot[i] += highscorePerRun[i];
                    for (int k = pivot[i]; k < runlength.get(i); k++) {
                        addnew.readLong();
                        addnew.readDouble();
                    }

                }
                addnew.close();
                num += recordsPerRun;

            }
            fin.close();
        }
        else {
            // int recordsPerRun = 1024;
            DataInputStream writein = 
                    new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream("runFile.data")));
            boolean finish = false;
            try {

                for (int i = 0; i < index; i++) {
                    // int runLength = writein.readInt();
                    ArrayList<Ascore> run = new ArrayList<Ascore>();
                    for (int j = 0; j < 1024; j++) {
                        Ascore t = 
                                new Ascore(writein.readLong(), 
                                        writein.readDouble());
                        run.add(t);
                    }
                    if (finish) {
                        break;
                    }
                    runs.add(run);
                }
            } 
            catch (Exception e) {
                finish = true;

            }
            writein.close();
            // next is output part
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {
                if (removeIndex != -1) {
                    runs.remove(removeIndex);
                    removeIndex = -1;
                }
                if (runs.size() == 0) {
                    break;
                }

                Ascore t = findMax(runs);
                if (t != null) {
                    fin.writeLong(t.getPid());
                    fin.writeDouble(t.getScore());
                    // System.out.println(t.getPid() + " " + t.getScore());
                    fin.flush();
                    num++;
                }
            }
            fin.close();
        }

    }

    /**
     * run 提取max
     * 
     * @param runs
     * @return
     */
    private Ascore findMax(ArrayList<ArrayList<Ascore>> runs) {
        Ascore max = new Ascore(Long.parseLong("909123456789"), -1);
        int x = -1;
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i) == null) {
                continue;
            }
            if (runs.get(i).isEmpty()) {
                // runs.remove(i);
                removeIndex = i;
                return null;

            }

        }
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i) == null) {
                continue;
            }
            if (runs.get(i).get(0).compareTo(max) == 1) {
                max = runs.get(i).get(0);
                x = i;
            }
        }
        if (x != -1) {
            runs.get(x).remove(0);
            return max;
        }
        return null;
    }

    /**
     * heap数组提取max
     * 
     * @return popped
     */
    public Ascore extractMax() {
        Ascore popped = heap[0];
        heap[0] = heap[heapSize - 1];
        heapSize--;

        for (int i = (heapSize - 1) / 2; i >= 0; i--) {
            sift(i, heapSize);
        } // heap build finish
        return popped;

    }
    /**
     * get total
     * 
     * @return num 
     */
    public int getTotal() {
        return num;
    }
    /**
     * closeInBuffer
     * @throws IOException
     */
    public void closeInBuffer() throws IOException {
        in.close();
    }
    /**
     * closeOutBuffer
     * @throws IOException
     */
    public void closeOutBuffer() throws IOException {
        out.close();
    }
    /**
     * closeOutBuffer
     * @param a 
     * @param i
     * @param j
     * @return
     */
    private void quicksort(ArrayList<Ascore> a, int i, int j) {
        int pivotindex = findpivot(a, i, j); // Pick a pivot
        swap(a, pivotindex, j); // Stick pivot at end
        // k will be the first position in the right subarray
        if (j - 1 - i < 65) {
            insertion(a, i, j);
            return;
        }

        int k = partition(a, i, j - 1, a.get(j));

        swap(a, k, j); // Put pivot in place
        if ((k - i) > 1) {
            // System.out.println("k-i " +k +", " +i);
            quicksort(a, i, k - 1); // Sort left partition
        }
        if ((j - k) > 1) {
            // System.out.println("j-k "+j +", " +k);
            quicksort(a, k + 1, j); // Sort right partition
        }
    }
    /**
     * closeOutBuffer
     * @param a 
     * @param i
     * @param j
     * @return (i + j) / 2
     */
    private int findpivot(ArrayList<Ascore> a, int i, int j) {
        return (i + j) / 2;
    }
    /**
     * closeOutBuffer
     * @param a 
     * @param i
     * @param j
     * @return left
     */
    private int partition(ArrayList<Ascore> a, 
            int left, int right, Ascore pivot) {
        while (left <= right) { // Move bounds inward until they meet
            while (a.get(left).compareTo(pivot) > 0) {
                left++;
            }
            while ((right >= left) && (a.get(right).compareTo(pivot) < 0)) {
                right--;
            }
            if (right > left) {
                swap(a, left, right); // Swap out-of-place values
                
            }
        }
        return left; // Return first position in right partition
    }
    /**
     * closeOutBuffer
     * @param a 
     * @param i
     * @param j
     */
    private void swap(ArrayList<Ascore> a, int i, int j) {
        Ascore t = a.get(i);
        a.set(i, a.get(j));
        a.set(j, t);
    }
    /**
     * closeOutBuffer
     * @param a 
     * @param s
     * @param e
     */
    private void insertion(ArrayList<Ascore> a, int s, int e) {
        for (int i = s; i < e; i++) {
            for (int k = i + 1; 
                    (k > 0) && a.get(k).compareTo(a.get(k - 1)) > 0; 
                    k--) {
                swap(a, k, k - 1);
            }
        }
    }
}
