/**
 * 
 */

/**
 * @author Peilin
 *
 */
public class Ascore implements Comparable<Ascore> {
    private long pid;
    private double score;
    private int index;

    public Ascore(long pid, double score) {
        this.pid = pid;
        this.score = score;
    }

    @Override
    public int compareTo(Ascore o) {
        if (o == null) {
            return 1;
        }
        if (score > o.score) {
            return 1;
        } else {
            return -1;
        }

    }

    public long getPid() {
        return pid;
    }

    public double getScore() {
        return score;
    }
    public void setIndex(int i)
    {
        index = i;
    }
    public int getIndex()
    {
        return index;
    }

}
