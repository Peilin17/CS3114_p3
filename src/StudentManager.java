import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 */

/**
 * @author Peilin
 * @version 11/10/2019
 */
public class StudentManager {
    private BST<Student> studentList;
    private ByteBuffer buffer;
    private DataInputStream in;
    private int count;

    /**
     * constructor
     */
    public StudentManager() {
        studentList = new BST<Student>();
    }

    /**
     * store student
     * 
     * @param filename filename
     */
    public void storeStudent(String filename) {
        try {
            DataInputStream inn = 
                    new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(filename)));
            byte[] txt = new byte[10];
            inn.read(txt);
            int size = inn.readInt();

            for (int i = 0; i < size; i++) {
                long pid = inn.readLong();
                StringBuilder sb = new StringBuilder();
                byte[] tem = new byte[1];
                inn.read(tem);
                String temp = new String(tem, "UTF-8");
                while (!temp.equals("$")) {
                    sb.append(temp);
                    inn.read(tem);
                    temp = new String(tem, "UTF-8");
                }
                String firstname = sb.toString();
                StringBuilder sb1 = new StringBuilder();
                inn.read(tem);
                temp = new String(tem, "UTF-8");
                while (!temp.equals("$")) {
                    sb1.append(temp);
                    inn.read(tem);
                    temp = new String(tem, "UTF-8");
                }
                String midname = sb1.toString();
                StringBuilder sb2 = new StringBuilder();
                inn.read(tem);
                temp = new String(tem, "UTF-8");
                while (!temp.equals("$")) {
                    sb2.append(temp);
                    inn.read(tem);
                    temp = new String(tem, "UTF-8");
                }
                String lastname = sb2.toString();
                byte[] txttail = new byte[8];
                inn.read(txttail);

                studentList.insert(
                        createStudent(
                                firstname, 
                                midname, 
                                lastname, pid));

            }
            inn.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * print student
     * 
     * @param filename filename
     * @param num      number
     * @throws IOException 
     */
    public void printStudent(String filename, int num) throws IOException {
        in = new DataInputStream(new BufferedInputStream(
                new FileInputStream(filename)));
        read10Block();
        try {
            
            
            int i = 1;
            int j = 0;
            while (j < 100 && i <= num) {
                if (count == 10240)
                {
                    read10Block();
                }
                Long pid = buffer.getLong(count * 16);
                String p = String.valueOf(pid);
                p = p.substring(3, p.length());
                pid = Long.parseLong(p);
                Double score = buffer.getDouble((count * 16) + 8);
                count++;
                Student t = searchByPid(pid);

                if (t != null) {
                    j++;
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0; k < 9 - String.valueOf(pid).length(); k++)
                    {
                        sb.append("0");
                    }
                    String z = sb.toString();
                    System.out.println("909" + z + pid + ", " 
                            + t.getFirstName() + " " 
                            + t.getLastName() + " at rank "
                            + i + " with Ascore " + score);
                }

                i++;
            }

            in.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * search base on pid
     * 
     * @param pid pidnumber
     * @return t t null
     */
    private Student searchByPid(Long pid) {
        BSTIterator<Student> it = 
                new BSTIterator<Student>(
                        studentList.getRoot());
        while (it.hasNext()) {
            Student t = it.next().getElement();
            if (t.getPid().equals(pid)) {
                return t;
            }
        }
        return null;
    }

    /**
     * @param f   firstname
     * @param m   middle name
     * @param l   last name
     * @param pid pidnumber
     * 
     * @return if student created
     */
    public Student createStudent(String f, String m, String l, long pid) {
        Student created = new Student(f, l);
        created.setPid(pid);
        created.setMidName(m);
        created.setScore(0);
        return created;
    }
    /**
    * @throws IOException
    */
    private void read10Block() throws IOException
    {
        count = 0;
        byte[] b = new byte[10240 * 16];
        in.read(b);
        buffer = ByteBuffer.wrap(b);
    }
}