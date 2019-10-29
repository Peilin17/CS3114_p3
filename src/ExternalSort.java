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
    Ascore heap[];

    //private ArrayList<Ascore> inter;
    private DataInputStream in;
    private int index;
    private File f;
    private DataOutputStream out;
    private File result;

    public ExternalSort(String filename) throws FileNotFoundException {
        index = 0;
        f = new File("runFile");
        result = new File("result");
        //inter = new ArrayList<Ascore>();
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        heap = new Ascore[8192];
        FileOutputStream file = new FileOutputStream(f);
        out = new DataOutputStream(file);
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
            if (size == 0)
            {
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
            } else {
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
        } else {
            vl = null;
        }
        if (r < HEAP_SIZE) {
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

    private void mutiMerge() throws IOException {
        ArrayList<ArrayList<Ascore>> runs = new ArrayList<ArrayList<Ascore>>();
        if (HEAP_SIZE / index < 1024) {
            int recordsPerRun = HEAP_SIZE / index;
            /*
             * 这里相对于下面需要补充：
             * 1. 每一次取recordPerRun的量 （多一个循环去分段读完每一个run（1024 record））
             * 2. 在findmax时一个arrayList读完了要记得补充下一个（感觉很麻烦）
             */
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("runfile")));
            for (int i = 0; i < index; i++) {
                int runLength = in.readInt();
                ArrayList<Ascore> run = new ArrayList<Ascore>();
                for (int j = 0; j < recordsPerRun; j++) {
                    Ascore t = new Ascore(in.readLong(), in.readDouble());
                    run.add(t);
                }
                runs.add(run);
            }
            in.close();
            // next is output part
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {
                for (int i = 0; i < index; i++) {
                    
                    fin.writeLong(findMax(runs).getPid());
                    fin.writeDouble(findMax(runs).getScore());
                }
                fin.flush();
            }
        } else {
            // int recordsPerRun = 1024;
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("runfile")));
            for (int i = 0; i < index; i++) {
                int runLength = in.readInt();
                ArrayList<Ascore> run = new ArrayList<Ascore>();
                for (int j = 0; j < runLength; j++) {
                    Ascore t = new Ascore(in.readLong(), in.readDouble());
                    run.add(t);
                }
                runs.add(run);
            }
            in.close();
            // next is output part
            FileOutputStream fi = new FileOutputStream(result);
            DataOutputStream fin = new DataOutputStream(fi);
            while (!runs.isEmpty()) {
                for (int i = 0; i < index; i++) {
                    fin.writeLong(findMax(runs).getPid());
                    fin.writeDouble(findMax(runs).getScore());
                }
                fin.flush();
            }
            //fin.close();?
        }

    }

    private Ascore findMax(ArrayList<ArrayList<Ascore>> runs) {
        Ascore max = new Ascore(Long.parseLong("909123456789"), -1);
        int x = -1;
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i).isEmpty())
            {
                runs.remove(i);
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
