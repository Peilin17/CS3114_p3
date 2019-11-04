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
     * 先读8个block然后建成堆，
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
     * 然后再一个block一个block读.
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
     * 这个metod巨tm关键 heap的建立和后续排序都在这了
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
     * 这个步骤太恶心了， 需要优化 具体看分步注释
     * 
     * @throws IOException
     */
    private void mutiMerge() throws IOException {
        ArrayList<ArrayList<Ascore>> runs = new ArrayList<ArrayList<Ascore>>();
        pivot = new int[index];
        if (HEAP_SIZE / index < 1024) { // 这下面是run的数量超过8个，所以每一个run不能取完不然内存超8block
            // 取每一个run的前 （总8block容量/run的数量）
            int recordsPerRun = HEAP_SIZE / index;
            /*
             * 这里相对于下面else需要补充： 1. 每一次取recordPerRun的量 （多一个循环去分段读完每一个run（1024 record）） 2.
             * 在findmax时一个arrayList读完了要记得补充下一个（感觉很麻烦）
             */
            DataInputStream writein = new DataInputStream(new BufferedInputStream(new FileInputStream("runfile")));
            for (int i = 0; i < index; i++) {
                // 这里第一次提取，for loop里判断recordsPerRun是否够每一个 run， 最后一个run可能很少。
                int runLength = writein.readInt();
                ArrayList<Ascore> run = new ArrayList<Ascore>();
                int j = 0;
                for (; j < (recordsPerRun < runLength ? recordsPerRun : runLength); j++) {
                    Ascore t = new Ascore(writein.readLong(), writein.readDouble());
                    run.add(t);

                }
                pivot[i] = j;// pivot 看每一个run都读到了哪里，为了后面当现有的run merge完补齐
                for (; j < runLength; j++)// 这里读完当前run, 为了去读下一个
                {
                    writein.readLong();
                    writein.readDouble();
                }

                runs.add(run);// 添加到runs总集
            }
            writein.close();
            // next is output part
            /*
             * 开启写入： 正式版要求写入原文件，这里先新建一个测试省的复制粘贴
             */
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {// 循环查看每一个run的第一个，找最小值
                for (int i = 0; i < index; i++) {
                    Ascore t = findMax(runs);// 这是找最小值
                    if (removeIndex != -1)// 当一个run跑完时会触发removeIndex ！=-1
                    {
                        // 新建一个run，通过pivot确定上次读到的位置，继续读规定的数量，如果不够就读完拉倒
                        ArrayList<Ascore> run = new ArrayList<Ascore>();
                        DataInputStream addnew = new DataInputStream(
                                new BufferedInputStream(new FileInputStream("runfile")));
                        for (int y = 0; y < removeIndex; y++) {// 跳过之前的run
                            int runLength = addnew.readInt();
                            int j = 0;
                            for (; j < runLength; j++) {
                                addnew.readLong();
                                addnew.readDouble();
                            }
                        }
                        // 到达指定run
                        int runLength = addnew.readInt();
                        // pivot
                        if (pivot[removeIndex] < runLength) {
                            int m = 0;
                            for (; m < pivot[removeIndex]; m++)// 跳过之前读过的
                            {
                                addnew.readLong();
                                addnew.readDouble();
                            }
                            for (; m < (pivot[removeIndex] + recordsPerRun < runLength
                                    ? pivot[removeIndex] + recordsPerRun
                                    : runLength); m++) {// 开始读要求的并添加到内存
                                Ascore ta = new Ascore(addnew.readLong(), addnew.readDouble());
                                run.add(ta);
                            }
                        }
                        // 该归-1的归-1， 该加的加
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
