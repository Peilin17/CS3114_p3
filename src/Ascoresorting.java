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
        StudentManager studentmanager = new StudentManager();
        studentmanager.storeStudent(args[1]);//store the student file same as p2
        ExternalSort es = new ExternalSort(args[0]);
        es.sortData();
        studentmanager.printStudent("result.data", es.getTotal());
        
    }

    /*
     * ����浮��
     * 8k����û������
     * Ч��ɶ�Ļ���֪������Ӧ��û���ڴ�����
     * ���ڽ�����Ҫ������ExternalSort��212-291�� ��д��һЩ�����bug���Ѿ��޺��˴󲿷֣�Ӧ�ã���
     * ����Ӧ�������������µ�run�������е����ڴ��run����ʱ��
     * ���ͣ�����
     */
}
