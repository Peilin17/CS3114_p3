import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
    private int aa;
    private int indexOfOneBlock;
    private String name;
    private ByteBuffer oneBlockBuffer;
    // private int test;

    /**
     * constructor
     * 
     * @param filename filename
     * @throws FileNotFoundException
     */
    public ExternalSort(String filename) throws FileNotFoundException {
        index = 0;
        name = filename;
        finishReading = false;
        File f = new File("runFile.data");
        // result = new File("result.data");
        // inter = new ArrayList<Ascore>();
        File sample = new File(filename);
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(sample)));
        heap = new Ascore[8192];
        FileOutputStream file = new FileOutputStream(f);
        out = new DataOutputStream(file);
    }

    /**
     * sort the data
     * 
     * @throws IOException
     */
    public void sortData() throws IOException {
        read8block();
        readOneBuffer();
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
        File s = new File(name);
        s.delete();
        // sample.delete();
        mutiMerge();

    }

    /**
     * read 8 block
     * 
     * @return
     */
    public void read8block() {
        try {

            byte[] b = new byte[16384 * 8];
            in.read(b);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            for (int i = 0; i < 8192; i++) {
                Long pid = buffer.getLong(i * 16);
                Double score = buffer.getDouble((i * 16) + 8);
                heap[i] = new Ascore(pid, score);
                heapSize++;
            }
            for (int i = (heapSize - 1) / 2; i >= 0; i--) {
                // System.out.println(i);
                sift(i, heapSize, heap);
            } // heap build finish

        } catch (Exception e) {
            for (int i = (heapSize - 1) / 2; i >= 0; i--) {
                // System.out.println(i);
                sift(i, heapSize, heap);
            }
            return;
        }
        size = heapSize;
    }

    /**
     * read one block
     * 
     * @throws IOException
     */
    private void readOneBuffer() throws IOException {
        indexOfOneBlock = 0;
        byte[] b = new byte[16384];
        in.read(b);
        oneBlockBuffer = ByteBuffer.wrap(b);
    }

    /**
     * @return outBuffer
     * @throws IOException
     */
    private ArrayList<Ascore> readOneBlock() throws IOException {

        ArrayList<Ascore> outBuffer = new ArrayList<Ascore>();
        try {

            for (int i = 0; i < 1024; i++) {
                if (size == 0) {
                    for (int l = (heapSize - 1) / 2; l >= 0; l--) {
                        sift(l, heapSize, heap);
                    }
                    size = heapSize;
                    while (heap[0].compareTo(last) == 1) {
                        if (size == 0) {
                            deathHeap = true;
                            size = heapSize;
                            for (int l = (heapSize - 1) / 2; l >= 0; l--) {
                                sift(l, heapSize, heap);
                            }
                            return outBuffer;
                        }
                        Ascore temp = heap[0];
                        heap[0] = heap[size - 1];
                        heap[size - 1] = temp;
                        size--;
                        sift(0, size, heap);
                    }

                }
                Long pid = oneBlockBuffer.getLong(indexOfOneBlock * 16);
                if (pid.equals(Long.parseLong("0"))) {
                    finishReading = true;
                    break;
                }
                Double score = oneBlockBuffer.getDouble((indexOfOneBlock * 16) + 8);
                indexOfOneBlock++;
                if (indexOfOneBlock == 1024) {
                    readOneBuffer();
                }
                Ascore t = new Ascore(pid, score);
                outBuffer.add(heap[0]);
                runsize++;
                last = heap[0];

                if (t.compareTo(last) == 1) {
                    heap[0] = heap[size - 1];
                    heap[size - 1] = t;
                    size--;
                } else {
                    heap[0] = t;
                }
                sift(0, size, heap);
            }
        } catch (Exception e) {
            finishReading = true;
            return outBuffer;
        }

        return outBuffer;

    }

    /**
     * @param i
     * @param sizeH
     * @param heapS
     */
    private void sift(int i, int sizeH, Ascore[] heapS) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        Ascore vl;
        Ascore vr;
        Ascore v;
        if (l < sizeH) {
            vl = heapS[l];
        } else {
            vl = null;
        }
        if (r < sizeH) {
            vr = heapS[r];
        } else {
            vr = null;
        }
        v = heapS[i];
        if (v.compareTo(vl) == 1 && v.compareTo(vr) == 1) {
            return;
        }
        if (vl.compareTo(vr) == 1) {
            heapS[i] = vl;
            heapS[l] = v;
            sift(l, sizeH, heapS);
        } else {
            heapS[i] = vr;
            heapS[r] = v;
            sift(r, sizeH, heapS);
        }
        return;
    }

    /**
     * output the outBUffer
     * 
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
            // System.out.println(test++ + ", "+h.size());
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
            sift(j, heapSize, heap);
        }
        for (; i < heapSizeHelp; i++) {
            Ascore t = extractMax(heap, heapSize);
            heapSize--;
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

            // ArrayList<Ascore> ascoreArray = new ArrayList<Ascore>();
            Ascore[] ascoreArray = new Ascore[8192];
            aa = 0;
            DataInputStream writein = new DataInputStream(new BufferedInputStream(new FileInputStream("runFile.data")));
            for (int i = 0; i < runlength.size(); i++) {
                // 这里第一次提取，for loop里判断recordsPerRun是否够每一个 run， 最后一个run可能很少。
                // ArrayList<Ascore> run = new ArrayList<Ascore>();
                int j = 0;
                for (; j < (runlength.get(i) < recordsPerRun ? runlength.get(i) : recordsPerRun); j++) {

                    Ascore t = new Ascore(writein.readLong(), writein.readDouble());
                    t.setIndex(i);
                    // ascoreArray.add(t);
                    ascoreArray[aa] = t;
                    aa++;

                }
                pivot[i] = j;

                writein.read(new byte[16 * (runlength.get(i) - j)]);

            }
            writein.close();
            // next is output part
            // File result = new File(name);
            result = new File(name);
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            
            while (aa != 0/* ascoreArray.size() != 0 */) {
                // System.out.println(aa);
                int[] highscorePerRun = new int[runlength.size()];
                // quicksort(ascoreArray, 0, ascoreArray.size() - 1);
                for (int j = (aa - 1) / 2; j >= 0; j--) {
                    sift(j, aa, ascoreArray);
                } // heap build finish
                for (int i = 0; i < (aa < recordsPerRun ? aa : recordsPerRun); i++) {
                    Ascore t = ascoreArray[0];
                    ascoreArray[0] = ascoreArray[aa - 1];

                    // extractMax(ascoreArray, aa);
                    aa--;
                    sift(0, aa, ascoreArray);
                    fin.writeLong(t.getPid());
                    fin.writeDouble(t.getScore());
                    num++;
                    // System.out.println(ascoreArray.get(0).getScore());
                    int group = t.getIndex();
                    highscorePerRun[group]++;
                    // ascoreArray.remove(0);
                }
                fin.flush();

                DataInputStream addnew = new DataInputStream(
                        new BufferedInputStream(new FileInputStream("runFile.data")));
                // int f = 0;
                for (int i = 0; i < runlength.size(); i++) {

                    if (pivot[i] >= runlength.get(i)) {

                        addnew.read(new byte[runlength.get(i) * 16]);

                        continue;
                    }

                    addnew.read(new byte[pivot[i] * 16]);

                    for (int k = pivot[i]; k < (highscorePerRun[i] + pivot[i] < runlength.get(i)
                            ? highscorePerRun[i] + pivot[i]
                            : runlength.get(i)); k++) {
                        Ascore temp = new Ascore(addnew.readLong(), addnew.readDouble());
                        // ascoreArray.add(temp);
                        temp.setIndex(i);
                        ascoreArray[aa] = temp;
                        aa++;
                        // f++;
                    }
                    // System.out.println(aa);
                    pivot[i] += highscorePerRun[i];
//                    for (int k = pivot[i]; k < runlength.get(i); k++) {
//                        addnew.readLong();
//                        addnew.readDouble();
//                    }
                    if (runlength.get(i) - pivot[i] > 0)
                    {
                        addnew.read(new byte[16 * (runlength.get(i) - pivot[i])]);
                    }
                    

                }
                addnew.close();
                // num += recordsPerRun;

            }
            fin.close();
        } else {
            // int recordsPerRun = 1024;
            DataInputStream writein = new DataInputStream(new BufferedInputStream(new FileInputStream("runFile.data")));
            boolean finish = false;
            try {

                for (int i = 0; i < index; i++) {
                    // int runLength = writein.readInt();
                    ArrayList<Ascore> run = new ArrayList<Ascore>();
                    for (int j = 0; j < 1024; j++) {
                        Ascore t = new Ascore(writein.readLong(), writein.readDouble());
                        run.add(t);
                    }
                    if (finish) {
                        break;
                    }
                    runs.add(run);
                }
            } catch (Exception e) {
                finish = true;

            }
            writein.close();
            // next is output part
            result = new File(name);
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
     * @param heapA heapA
     * @param sizeA sizeA
     * 
     * @return popped
     */
    public Ascore extractMax(Ascore[] heapA, int sizeA) {
        Ascore popped = heapA[0];
        heapA[0] = heapA[sizeA - 1];
        sizeA--;

        for (int i = (sizeA - 1) / 2; i >= 0; i--) {
            sift(i, sizeA, heapA);
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
     * 
     * @throws IOException
     */
    public void closeInBuffer() throws IOException {
        in.close();
    }

    /**
     * closeOutBuffer
     * 
     * @throws IOException
     */
    public void closeOutBuffer() throws IOException {
        out.close();
    }

}
