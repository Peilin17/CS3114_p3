import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * 
 */

/**
 * @author Peilin
 *
 */
public class StudentManager {
    private BST<Student> studentList;
    private int size;
    public StudentManager() {
        studentList = new BST<Student>();
    }

    public void storeStudent(String filename) {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            byte[] txt = new byte[10];
            in.read(txt);
            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                long pid = in.readLong();
                StringBuilder sb = new StringBuilder();
                byte[] tem = new byte[1];
                in.read(tem);
                String temp = new String(tem, "UTF-8");
                while (!temp.equals("$")) {
                    sb.append(temp);
                    in.read(tem);
                    temp = new String(tem, "UTF-8");
                }
                String firstname = sb.toString();
                StringBuilder sb1 = new StringBuilder();
                in.read(tem);
                temp = new String(tem, "UTF-8");
                while (!temp.equals("$")) {
                    sb1.append(temp);
                    in.read(tem);
                    temp = new String(tem, "UTF-8");
                }
                String midname = sb1.toString();
                StringBuilder sb2 = new StringBuilder();
                in.read(tem);
                temp = new String(tem, "UTF-8");
                while (!temp.equals("$")) {
                    sb2.append(temp);
                    in.read(tem);
                    temp = new String(tem, "UTF-8");
                }
                String lastname = sb2.toString();
                byte[] txttail = new byte[8];
                in.read(txttail);

                studentList.insert(createStudent(firstname, midname, lastname, pid));

            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(filename + " successfully loaded");

    }
    public void printStudent(String filename, int num)
    {
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            int i = 1;
            int j = 0;
            while (j < 100 && i <= num)
            {
                Long pid = in.readLong();
                String p = String.valueOf(pid);
                p = p.substring(3, p.length());
                pid = Long.parseLong(p);
                Double score = in.readDouble();
                Student t = searchByPid(pid);
                
                if (t != null)
                {
                    j++;
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0; k < 9 
                            - String.valueOf(pid).length();
                            k++) {
                        sb.append("0");
                    }
                    String z = sb.toString();
                    System.out.println("909" + z + pid + ", " + t.getFirstName() + " " + t.getLastName() + " at rank " + i + " with Ascore " + score);
                }
                
                i++;
            }

            
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * search base on pid
     */
    private Student searchByPid(Long pid)
    {
        BSTIterator<Student> it = new BSTIterator<Student>(studentList.getRoot());
        while (it.hasNext())
        {
            Student t = it.next().getElement();
            if (t.getPid().equals(pid))
            {
                return t;
            }
        }
        return null;
    }
    /**
     * @param f firstname
     * @param m middle name
     * @param l last name
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
}