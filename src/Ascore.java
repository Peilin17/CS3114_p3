/**
 * 
 */

/**
 * @author Peilin
 * @version 11/10/22019
 *
 */
public class Ascore implements Comparable<Ascore> {
    private long pid;
    private double score;
    private int index;

    
    /**
     * constructor 
     * @param pid pid number
     * @param score score
     *
     */
    public Ascore(long pid, double score) {
        this.pid = pid;
        this.score = score;
    }

    /**
     * compare function
     * @param o pass in data
     * @return 1 if equal or bigger
     *         -1 less
     */
    public int compareTo(Ascore o) {
        if (o == null) {
            return 1;
        }
        if (score > o.score) {
            return 1;
        }
        else {
            return -1;
        }
    }

    /**
     * get the pid
     * 
     *@return pid number
     */
    public long getPid() {
        return pid;
    }

    /**
     * get the score
     * 
     *@return score number
     */
    public double getScore() {
        return score;
    }

    /**
     * set the pid
     * 
     *@param i index
     */
    public void setIndex(int i) {
        index = i;
    }

    /**
     * get the index
     * 
     *@return index number
     */
    public int getIndex() {
        return index;
    }

}
