import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

class Test {
    private Test() {}
    
    private static void testStream() {
        Date now = new Date();
        var aList = new ConcurrentSkipListSet<Integer>();
        IntStream.range(0, 10000).parallel().forEach(aList::add);
        Date after = new Date();
        System.out.println(after.getTime() - now.getTime());
        System.out.println(aList.size());
        
    }
    public static void main(String[] args) {
        testStream();
    }

}
