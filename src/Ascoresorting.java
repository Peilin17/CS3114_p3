import java.io.IOException;

/**
 * 
 */

/**
 * @author Peilin
 * @throws IOException
 */
public class Ascoresorting {
    public static void main(String args[]) throws IOException
    {
        long start=System.currentTimeMillis();
        StudentManager studentmanager = new StudentManager();
        studentmanager.storeStudent(args[1]);//store the student file same as p2
        ExternalSort es = new ExternalSort(args[0]);
        es.sortData();
        studentmanager.printStudent("result.data", es.getTotal());
        long end=System.currentTimeMillis();
        //System.out.println("��������ʱ�䣺 "+(end-start)+"ms");
    }

    
}
