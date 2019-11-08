import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * 
 */

/**
 * @author Peilin
 *
 */
public class ExternalSort {
    public static int HEAP_SIZE = 8192;
    private Ascore heap[];

    // private ArrayList<Ascore> inter;
    private DataInputStream in;
    private int index;
    private File f;
    private DataOutputStream out;
    private File result;
    private int removeIndex = -1;
    private int pivot[];
    private int num = 0;
    private int size;
    private boolean finishReading;
    private int heapSize = 0;

    public ExternalSort(String filename) throws FileNotFoundException {
        index = 0;
        finishReading = false;
        f = new File("runFile.data");
        result = new File("result.data");
        // inter = new ArrayList<Ascore>();
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        heap = new Ascore[8192];
        FileOutputStream file = new FileOutputStream(f);
        out = new DataOutputStream(file);
    }

    public void sortData() throws IOException {
        read8block();

        while (!finishReading) {
            outputBuffer(readOneBlock());
        }
        clearHeap();
        closeInBuffer();
        closeOutBuffer();
        mutiMerge();

    }

    /**
     * �ȶ�8��blockȻ�󽨳ɶѣ�
     */
    public void read8block() {
        try {
            for (int i = 0; i < 8192; i++) {// total record for 8 block
                long pid = in.readLong();
                double score = in.readDouble();
                // store in heap, raw.
                Ascore t = new Ascore(pid, score);
                heap[i] = t;
                heapSize++;
            }
            for (int i = (heapSize - 1) / 2; i >= 0; i--) {
                // System.out.println(i);
                sift(i);
            } // heap build finish

        } catch (Exception e) {
            for (int i = (heapSize - 1) / 2; i >= 0; i--) {
                // System.out.println(i);
                sift(i);
            }
            return;
        }
        size = heapSize;
    }

    /**
     * Ȼ����һ��blockһ��block��.
     * 
     * @return
     * @throws IOException
     */
    private ArrayList<Ascore> readOneBlock() throws IOException {
        ArrayList<Ascore> outBuffer = new ArrayList<Ascore>();
        try {
            // size = heapSize;
            for (int i = 0; i < 1024; i++) {
                if (size == 0) {
                    for (int l = (heapSize - 1) / 2; l >= 0; l--) {
                        sift(i);
                    } // heap rebuild finish
                    size = heapSize;
                }
                long pid = in.readLong();
                double score = in.readDouble();
                Ascore t = new Ascore(pid, score);
                Ascore last = heap[0];
                outBuffer.add(heap[0]);
                if (t.compareTo(last) == 1) {
                    heap[0] = heap[size - 1];
                    heap[size - 1] = t;
                    size--;
                } else {
                    heap[0] = t;
                }
                sift(0);
            }
        } catch (Exception e) {
            finishReading = true;
            return outBuffer;
        }
//        for (Ascore t: outBuffer)
//        {
//            System.out.println(t.getScore());
//            
//        }
//        System.out.println("===============================");
        return outBuffer;

    }

    /**
     * ���metod��tm�ؼ� heap�Ľ����ͺ�������������
     * 
     * @param i
     */
    private void sift(int i) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        Ascore vl, vr, v;
        if (l < heapSize) {
            vl = heap[l];
        } else {
            vl = null;
        }
        if (r < heapSize) {
            vr = heap[r];
        } else {
            vr = null;
        }
        v = heap[i];
        if (v.compareTo(vl) == 1 && v.compareTo(vr) == 1)
            return;
        if (vl.compareTo(vr) == 1) {
            heap[i] = vl;
            heap[l] = v;
            sift(l);
        } else {
            heap[i] = vr;
            heap[r] = v;
            sift(r);
        }
        return;
    }

    /**
     * output the outBUffer
     * 
     * @throws IOException
     */
    private void outputBuffer(ArrayList<Ascore> h) throws IOException {
        // write into disk output outBuffer.
        // remember to add the inter.
        if (h.size() > 0) {
            // out.writeInt(h.size());
            for (int i = 0; i < h.size(); i++) {
                out.writeLong(heap[i].getPid());
                out.writeDouble(heap[i].getScore());

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
            sift(j);
        } // heap build finish
        for (; i < heapSizeHelp; i++) {
            Ascore t = extractMax();
            out.writeLong(t.getPid());
            out.writeDouble(t.getScore());
            out.flush();
            if (i > 0 && (i + 1) % 1024 == 0) {
                index++;
            }
        }
    }

    /**
     * �������̫�����ˣ� ��Ҫ�Ż� ���忴�ֲ�ע��
     * 
     * @throws IOException
     */
    private void mutiMerge() throws IOException {
        ArrayList<ArrayList<Ascore>> runs = new ArrayList<ArrayList<Ascore>>();
        if (index == 0) {
            index++;
        }
        pivot = new int[index];
        if (HEAP_SIZE / index < 1024) { // ��������run����������8��������ÿһ��run����ȡ�겻Ȼ�ڴ泬8block
            // ȡÿһ��run��ǰ ����8block����/run��������
            int recordsPerRun = HEAP_SIZE / index;
            /*
             * �������������else��Ҫ���䣺 1. ÿһ��ȡrecordPerRun���� ����һ��ѭ��ȥ�ֶζ���ÿһ��run��1024 record���� 2.
             * ��findmaxʱһ��arrayList������Ҫ�ǵò�����һ�����о����鷳��
             */
            DataInputStream writein = new DataInputStream(new BufferedInputStream(new FileInputStream("runFile.data")));
            for (int i = 0; i < index; i++) {
                // �����һ����ȡ��for loop���ж�recordsPerRun�Ƿ�ÿһ�� run�� ���һ��run���ܺ��١�
                // int runLength = writein.readInt();
                ArrayList<Ascore> run = new ArrayList<Ascore>();
                int j = 0;
                for (; j < recordsPerRun; j++) {

                    Ascore t = new Ascore(writein.readLong(), writein.readDouble());

                    run.add(t);

                }
                pivot[i] = j;// pivot ��ÿһ��run�����������Ϊ�˺��浱���е�run merge�겹
                for (; j < 1024; j++)// ������굱ǰrun, Ϊ��ȥ����һ��
                {
                    writein.readLong();
                    writein.readDouble();
                }
                // System.out.println(i + ", " + j);
                runs.add(run);// ��ӵ�runs�ܼ�
            }
            writein.close();
            // next is output part
            /*
             * ����д�룺 ��ʽ��Ҫ��д��ԭ�ļ����������½�һ������ʡ�ĸ���ճ��
             */
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {// ѭ���鿴ÿһ��run�ĵ�һ��������daֵ

                Ascore t = findMax(runs);// ��������Сֵ
                if (removeIndex != -1)// ��һ��run����ʱ�ᴥ��removeIndex ��=-1
                {
                    // �½�һ��run��ͨ��pivotȷ���ϴζ�����λ�ã��������涨����������������Ͷ�������
                    ArrayList<Ascore> run = new ArrayList<Ascore>();
                    DataInputStream addnew = new DataInputStream(
                            new BufferedInputStream(new FileInputStream("runFile.data")));
                    for (int y = 0; y < removeIndex; y++) {// ����֮ǰ��run

                        for (int j = 0; j < 1024; j++) {
                            addnew.readLong();
                            addnew.readDouble();
                        }
                    }
                    // ����ָ��run
                    // pivot
                    if (pivot[removeIndex] < 1024) {
                        int m = 0;
                        for (; m < pivot[removeIndex]; m++)// ����֮ǰ������
                        {
                            addnew.readLong();
                            addnew.readDouble();
                        }
                        for (; m < (pivot[removeIndex] + recordsPerRun < 1024 ? pivot[removeIndex] + recordsPerRun
                                : 1024); m++) {// ��ʼ��Ҫ���part����ӵ��ڴ�
                            Ascore ta = new Ascore(addnew.readLong(), addnew.readDouble());
                            run.add(ta);
                        }
                        runs.set(removeIndex, run);
                        pivot[removeIndex] += recordsPerRun;
                    } else {
                        runs.remove(removeIndex);
                        pivotRemove(removeIndex);
                    }
                    // �ù�-1�Ĺ�-1�� �üӵļ�

                    removeIndex = -1;
                    addnew.close();

                }
                if (t != null) {
                    System.out.println(t.getPid() + " " + t.getScore());
                    fin.writeLong(t.getPid());
                    fin.writeDouble(t.getScore());

                    fin.flush();
                    num++;
                }
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
     * run ��ȡmax
     * 
     * @param runs
     * @return
     */
    private Ascore findMax(ArrayList<ArrayList<Ascore>> runs) {
        Ascore max = new Ascore(Long.parseLong("909123456789"), -1);
        int x = -1;
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i).isEmpty()) {
                // runs.remove(i);
                removeIndex = i;
                return null;
            }
        }
        for (int i = 0; i < runs.size(); i++) {
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
     * heap������ȡmax
     * 
     * @return
     */
    public Ascore extractMax() {
        Ascore popped = heap[0];
        heap[0] = heap[heapSize - 1];
        heapSize--;

        for (int i = (heapSize - 1) / 2; i >= 0; i--) {
            sift(i);
        } // heap build finish
        return popped;
//        int heapSizeHelp = heapSize;
//        Ascore popped = heap[0];
//        heap[0] = heap[--heapSizeHelp];
//        maxHeapify(0);
//        return popped;
    }

    public int getTotal() {
        return num;
    }

    // ������heapתrun helpmethod��
//    private boolean isLeaf(int pos) {
//        if (pos >= (heap.length / 2) && pos <= heap.length) {
//            return true;
//        }
//        return false;
//    }
//
//    private void maxHeapify(int pos) {
//        if (isLeaf(pos))
//            return;
//
//        if (heap[pos].compareTo(heap[leftChild(pos)]) == -1 || heap[pos].compareTo(heap[rightChild(pos)]) == -1) {
//
//            if (heap[leftChild(pos)].compareTo(heap[rightChild(pos)]) == 1) {
//                swap(pos, leftChild(pos));
//                maxHeapify(leftChild(pos));
//            } else {
//                swap(pos, rightChild(pos));
//                maxHeapify(rightChild(pos));
//            }
//        }
//    }
//
//    private void swap(int fpos, int spos) {
//        Ascore tmp;
//        tmp = heap[fpos];
//        heap[fpos] = heap[spos];
//        heap[spos] = tmp;
//    }
//
//    private int leftChild(int pos) {
//        return (2 * pos) + 1;
//    }
//
//    private int rightChild(int pos) {
//        return (2 * pos) + 2;
//    }

    public void closeInBuffer() throws IOException {
        in.close();
    }

    public void closeOutBuffer() throws IOException {
        out.close();
    }

    public void pivotRemove(int i) {
        for (; i < pivot.length - 1; i++) {
            pivot[i] = pivot[i + 1];
        }
        pivot[pivot.length - 1] = 404;
    }
}
