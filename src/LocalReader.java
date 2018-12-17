
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
/**
 * the collection reader that read a local file to get a collection of downloaded Vocaloid videos list
 * @see CollectionReader
 * @author CX无敌
 *
 */

public class LocalReader extends CollectionReader{
	private File listDownloadedTxt;

	public LocalReader() {
		super();
		listDownloadedTxt = new File(new File("."), "downloaded.txt");
		try {
			listDownloadedTxt.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void readRecord() {
		//convert into a smarter line-by-line reader from a char-by-char reader.
		try (BufferedReader reader = new BufferedReader(new FileReader(listDownloadedTxt));){
			
			for (String i = reader.readLine(); i != null && !i.equals(""); i = reader.readLine()) {
				String[] detialArray = i.split("------");
				collection.add(new Vsong(getIntFromSongId(detialArray[0]),detialArray[1]));
			}
			System.out.println("local record is: " + collection);
			reader.close();
			isDone = true;
		} catch (IOException e) {
			System.err.println("this shouldn't happen");
			e.printStackTrace();
			isDone = false;
		}
		
	}
	
	/**
	 * a function to filter chars from sm-id and get the integers.
	 * this function is designed because in some casees, the smXXXXXXXX doesn't read properly
	 * from download.txt file.
	 * @param smid
	 * @return
	 */
	private int getIntFromSongId(String smid) {
		return Integer.parseInt(
				smid.chars().parallel()
				.filter(Character::isDigit)
				.mapToObj(Character::toString)
				.collect(Collectors.joining())
				);
		//return Integer.parseInt(smid.substring(2, smid.length()));
	}

	public static void main(String[] args) throws IOException {
		testGetSMid();
		testReading();
	}
	public static void testReading() {
		LocalReader localReader = new LocalReader();
		//Map<String, String> map = localRecorder.getIsDownloaded();
		localReader.readRecord();
		System.out.println(localReader.getCollection());
		
	}
	
	public static void testGetSMid() {
		LocalReader localReader = new LocalReader();
		System.out.println(localReader.getIntFromSongId("m12345678"));
		System.out.println(localReader.getIntFromSongId("sm12345678"));
	}
	
}
