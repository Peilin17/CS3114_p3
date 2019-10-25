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