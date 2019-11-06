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
     * 初版版诞生
     * 8k以下没有问题
     * 效率啥的还不知道，但应该没有内存问题
     * 现在接下来要看的是ExternalSort：212-291行 你写的一些步骤的bug我已经修好了大部分（应该？）
     * 问题应该是在如何添加新的run（当现有的在内存的run跑完时）
     * 加油！！！
     */
}
