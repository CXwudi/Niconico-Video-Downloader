import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.IntStream;

/**
 * @author CXwudi
 *
 */
class Test {

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
		testCreateFile(new Vsong(3456, "asdasd", "testFolder"));
		testReplaceAll("as\\cbhjsdbc/ash?cjsd");
	}

	private static void testReplaceAll(String string) {
		System.out.println(string.replaceAll("/", "-").replaceAll("\\\\", "-").replaceAll("\\?", " "));
		
	}

	private static void testCreateFile(Vsong vsong) {
		File defaultDir = new File(System.getProperty("user.home") + "\\Videos");
		File video = new File(defaultDir, "testVideo.mp4");
		try {
			video.createNewFile();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
