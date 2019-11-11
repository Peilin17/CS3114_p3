import java.io.IOException;

import junit.framework.TestCase;
/**
 * @author Peilin
 * @version 11/10/2019
 */
public class ExSortTest extends TestCase {
    
    /**
     * test
     * 
     * @throws IOException
     */
    public void test1() throws IOException
    {
        ExternalSort ex = new ExternalSort("sample1k2.bin");
        ex.sortData();
        assertNotNull(ex);
    }
    
    /**
     * test
     * 
     * @throws IOException
     */
    public void test2() throws IOException
    {
        Student a = new Student("A", "A"); 
        Student b = new Student("B", "B"); 
        Student c = new Student("C", "C"); 
        BST<Student> tree = new BST<Student>();
        tree.insert(a);
        tree.insert(b);
        tree.insert(c);
        tree.findMax();
        tree.findMin();
        tree.search(c);
        tree.search(b);
        tree.insert(a);
        tree.clear();
        tree.isEmpty();
        tree.insert(a);
        assertNotNull(tree);
        
        
    }
    
    /**
     * test
     * 
     * @throws IOException
     */
    public void test3() throws IOException
    {
        ExternalSort ex = new ExternalSort("sample18k.bin");
        ex.sortData();
        assertNotNull(ex);
    }
    /**
     * test
     * 
     * @throws IOException
     */
    public void test4() throws IOException
    {
        StudentManager ex = new StudentManager();
        ex.storeStudent("sample1k2.bin");
        ex.printStudent("sample1k2.bin", 10);
        long pid = 906136272;
        ex.createStudent("R", "S", "Z", pid);
        Student a = new Student("A", "A");
        Student ab = new Student("A", "B");
        Student ac = new Student("A", "C");
        Student b = new Student("B", "B");
        Student c = new Student("C", "C");
        Student s = new Student("SA", "sA");
        s.setGrade("A");
        s.setGrade("B-");
        s.setScore(90);
        s.setSection(1);
        s.setPid(pid);
        s.setStudentID("99090");
        a.compareTo(b);
        b.compareTo(c);
        a.compareTo(c);
        a.compareTo(ab);
        a.compareTo(ac);
        ex.toString();
        assertNotNull(ex);
    }
    
    /**
     * test
     * 
     * @throws IOException
     */
    public void test5() throws IOException
    {
        Ascoresorting ex = new Ascoresorting();
        String[] st = new String[] {"sample1k2.bin", "result.data"};
        ex.main(st);
        assertNotNull(ex);
    }
}
