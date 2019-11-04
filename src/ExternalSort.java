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

    public ExternalSort(String filename) throws FileNotFoundException {
        index = 0;

        f = new File("runFile");
        result = new File("result");
        // inter = new ArrayList<Ascore>();
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        heap = new Ascore[8192];
        FileOutputStream file = new FileOutputStream(f);
        out = new DataOutputStream(file);
    }
    
    public void sortData() throws IOException {
        read8block();
        outputBuffer(readOneBlock());
        mutiMerge();
        clearHeap();
        closeInBuffer();
        closeOutBuffer();
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
            }
            for (int i = (HEAP_SIZE - 1) / 2; i >= 0; i--) {
                sift(i);
            } // heap build finish

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ȼ����һ��blockһ��block��.
     * 
     * @return
     * @throws IOException
     */
    private Ascore[] readOneBlock() throws IOException {
        Ascore outBuffer[] = new Ascore[1024];
        int size = HEAP_SIZE - 1;
        for (int i = 1; i < 1024; i++) {
            if (size == 0) {
                for (int l = (HEAP_SIZE - 1) / 2; l >= 0; l--) {
                    sift(i);
                } // heap rebuild finish
                size = HEAP_SIZE - 1;
            }
            long pid = in.readLong();
            double score = in.readDouble();
            Ascore t = new Ascore(pid, score);
            Ascore last = heap[0];
            outBuffer[i] = heap[0];
            if (t.compareTo(last) == 1) {
                heap[0] = heap[size - 1];
                heap[size - 1] = t;
                size--;
            }
            else {
                heap[0] = t;
            }
            sift(0);

        }
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
        if (l < HEAP_SIZE) {
            vl = heap[l];
        }
        else {
            vl = null;
        }
        if (r < HEAP_SIZE) {
            vr = heap[r];
        }
        else {
            vr = null;
        }
        v = heap[i];
        if (v.compareTo(vl) == 1 && v.compareTo(vr) == 1)
            return;
        if (vl.compareTo(vr) == 1) {
            heap[i] = vl;
            heap[l] = v;
            sift(l);
        }
        else {
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
    private void outputBuffer(Ascore h[]) throws IOException {
        // write into disk output outBuffer.
        // remember to add the inter.
        out.writeInt(h.length);
        for (int i = 0; i < h.length; i++) {
            out.writeLong(heap[i].getPid());
            out.writeDouble(heap[i].getScore());
        }
        index++;
        out.flush();

    }
    
    /**
     * clear the heaps
     * @throws IOException 
     */
    private void clearHeap() throws IOException {
        FileOutputStream fi = new FileOutputStream(result);
        DataOutputStream fin = new DataOutputStream(fi);
        
        for(int i = 0; i < this.heap.length; i ++)
        {
            Ascore t = heap[i];
            fin.writeLong(t.getPid());
            fin.writeDouble(t.getScore());
        }
        fin.flush();
        fin.close();
            
    }

    /**
     * �������̫�����ˣ� ��Ҫ�Ż� ���忴�ֲ�ע��
     * 
     * @throws IOException
     */
    private void mutiMerge() throws IOException {
        ArrayList<ArrayList<Ascore>> runs = new ArrayList<ArrayList<Ascore>>();
        pivot = new int[index];
        if (HEAP_SIZE / index < 1024) { // ��������run����������8��������ÿһ��run����ȡ�겻Ȼ�ڴ泬8block
            // ȡÿһ��run��ǰ ����8block����/run��������
            int recordsPerRun = HEAP_SIZE / index;
            /*
             * �������������else��Ҫ���䣺 1. ÿһ��ȡrecordPerRun���� ����һ��ѭ��ȥ�ֶζ���ÿһ��run��1024 record���� 2.
             * ��findmaxʱһ��arrayList������Ҫ�ǵò�����һ�����о����鷳��
             */
            DataInputStream writein = new DataInputStream(new BufferedInputStream(new FileInputStream("runfile")));
            for (int i = 0; i < index; i++) {
                // �����һ����ȡ��for loop���ж�recordsPerRun�Ƿ�ÿһ�� run�� ���һ��run���ܺ��١�
                int runLength = writein.readInt();
                ArrayList<Ascore> run = new ArrayList<Ascore>();
                int j = 0;
                for (; j < (recordsPerRun < runLength ? recordsPerRun : runLength); j++) {
                    Ascore t = new Ascore(writein.readLong(), writein.readDouble());
                    run.add(t);

                }
                pivot[i] = j;// pivot ��ÿһ��run�����������Ϊ�˺��浱���е�run merge�겹��
                for (; j < runLength; j++)// ������굱ǰrun, Ϊ��ȥ����һ��
                {
                    writein.readLong();
                    writein.readDouble();
                }

                runs.add(run);// ��ӵ�runs�ܼ�
            }
            writein.close();
            // next is output part
            /*
             * ����д�룺 ��ʽ��Ҫ��д��ԭ�ļ����������½�һ������ʡ�ĸ���ճ��
             */
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {// ѭ���鿴ÿһ��run�ĵ�һ��������Сֵ
                for (int i = 0; i < index; i++) {
                    Ascore t = findMax(runs);// ��������Сֵ
                    if (removeIndex != -1)// ��һ��run����ʱ�ᴥ��removeIndex ��=-1
                    {
                        // �½�һ��run��ͨ��pivotȷ���ϴζ�����λ�ã��������涨����������������Ͷ�������
                        ArrayList<Ascore> run = new ArrayList<Ascore>();
                        DataInputStream addnew = new DataInputStream(
                                new BufferedInputStream(new FileInputStream("runfile")));
                        for (int y = 0; y < removeIndex; y++) {// ����֮ǰ��run
                            int runLength = addnew.readInt();
                            int j = 0;
                            for (; j < runLength; j++) {
                                addnew.readLong();
                                addnew.readDouble();
                            }
                        }
                        // ����ָ��run
                        int runLength = addnew.readInt();
                        // pivot
                        if (pivot[removeIndex] < runLength) {
                            int m = 0;
                            for (; m < pivot[removeIndex]; m++)// ����֮ǰ������
                            {
                                addnew.readLong();
                                addnew.readDouble();
                            }
                            for (; m < (pivot[removeIndex] + recordsPerRun < runLength
                                    ? pivot[removeIndex] + recordsPerRun
                                    : runLength); m++) {// ��ʼ��Ҫ��Ĳ���ӵ��ڴ�
                                Ascore ta = new Ascore(addnew.readLong(), addnew.readDouble());
                                run.add(ta);
                            }
                        }
                        // �ù�-1�Ĺ�-1�� �üӵļ�
                        runs.set(removeIndex, run);
                        pivot[removeIndex] += recordsPerRun;
                        removeIndex = -1;
                        addnew.close();

                    }
                    fin.writeLong(t.getPid());
                    fin.writeDouble(t.getScore());
                }
                fin.flush();
            }
            fin.close();
        }
        else {
            // int recordsPerRun = 1024;
            DataInputStream writein = new DataInputStream(new BufferedInputStream(new FileInputStream("runfile")));
            for (int i = 0; i < index; i++) {
                int runLength = writein.readInt();
                ArrayList<Ascore> run = new ArrayList<Ascore>();
                for (int j = 0; j < runLength; j++) {
                    Ascore t = new Ascore(writein.readLong(), writein.readDouble());
                    run.add(t);
                }
                runs.add(run);
            }
            writein.close();
            // next is output part
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {
                for (int i = 0; i < index; i++) {
                    Ascore t = findMax(runs);
                    fin.writeLong(t.getPid());
                    fin.writeDouble(t.getScore());
                }
                fin.flush();
            }
            fin.close();
        }

    }

    private Ascore findMax(ArrayList<ArrayList<Ascore>> runs) {
        Ascore max = new Ascore(Long.parseLong("909123456789"), -1);
        int x = -1;
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i).isEmpty()) {
                // runs.remove(i);
                removeIndex = i;
                continue;
            }
            if (runs.get(i).get(1).compareTo(max) == 1) {
                max = runs.get(i).get(1);
                x = i;
            }
        }
        runs.get(x).remove(1);
        return max;
    }

    public void closeInBuffer() throws IOException {
        in.close();
    }

    public void closeOutBuffer() throws IOException {
        out.close();
    }
}
