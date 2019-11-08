import java.io.IOException;

import junit.framework.TestCase;

public class exSortTest extends TestCase{
    public void test1() throws IOException
    {
        ExternalSort ex = new ExternalSort("sample1k2.bin");
        ex.sortData();
        assertNotNull(ex);
    }
    public void test2() throws IOException
    {
        ExternalSort ex = new ExternalSort("sample128k.bin");
        ex.sortData();
        assertNotNull(ex);
    }
}
