import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 */

/**
 * @author Peilin
 *
 */
public class ExternalSort {
    public static int BUFFER_SIZE = 8192;
    Ascore heap[] = new Ascore[8192];
    Ascore outBuffer[] = new Ascore[1024];
    private DataInputStream in;

    public ExternalSort(String filename) throws FileNotFoundException {
        in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
    }

    public void read8block(String filename) {
        try {
            

            for (int i = 0; i < 8192; i++) {// total record for 8 block
                long pid = in.readLong();
                double score = in.readDouble();
                // store in heap, raw.
                Ascore t = new Ascore(pid, score);
                heap[i] = t;
            }
            for (int i = (BUFFER_SIZE - 1) / 2; i >= 0; i--) {
                sift(i);
            }//heap build finish
            
            

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //先读8个block然后建成堆， 然后再一个block一个block读， 读一个，写一个， 现在还差一个outBuffer写入磁盘的method没有实现

    
    public void closeBuffer() throws IOException
    {
        in.close();
    }
    public void readOneBlock() throws IOException
    {
        int size  = BUFFER_SIZE - 1;
        for (int i = 0; i < 1024; i++)
        {
            long pid = in.readLong();
            double score = in.readDouble();
            Ascore t = new Ascore(pid, score);
            Ascore last = heap[0];
            //写入out buffer
            outBuffer[i] = heap[0];
            //finish write out.
            if (t.compareTo(last) == -1)
            {
                heap[0] = heap[size];
                heap[size - 1] = t;
                size--;
            }
            else
            {
                heap[0] = t;
            }
            sift(0);
        }
    }
    
    
    /**
     * 这个metod巨tm关键 heap的建立和后续排序都在这了
     * 
     * @param i
     */
    public void sift(int i) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        Ascore vl, vr, v;
        if (l < BUFFER_SIZE) {
            vl = heap[l];
        } else {
            vl = null;
        }
        if (r < BUFFER_SIZE) {
            vr = heap[r];
        } else {
            vr = null;
        }
        v = heap[i];
        if (v.compareTo(vl) == -1 && v.compareTo(vr) == -1)
            return;
        if (vl.compareTo(vr) == -1) {
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
     */
    public void outputBuffer(Ascore h[])
    {
        // write into disk output outBuffer.
    }

}
