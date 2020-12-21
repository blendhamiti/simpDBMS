import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RowTest {

    @Test
    public void updateEntryTest() {
        List<String> list1 = new ArrayList<>();
        list1.add("asd");
        list1.add(null);
        list1.add(null);
        list1.remove(null);
        list1.add(1, "ccd");

        List<String> list2 = new ArrayList<>();
        list2.add("asd");
        list2.add("ccd");
        list2.add(null);

        Assert.assertTrue(list1.equals(list2));
    }
}
