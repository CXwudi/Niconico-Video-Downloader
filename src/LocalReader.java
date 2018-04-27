import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class LocalReader extends CollectionReader{
	private BufferedReader reader;
	private PrintWriter writer;
	private File listDownloadedTxt;

	public LocalReader() {
		super();
		listDownloadedTxt = new File(new File("."), "downloaded.txt");
	}
	



	@Override
	public void readRecord() {
		try {
			//convert into a smarter line-by-line reader from a char-by-char reader.
			reader = new BufferedReader(new FileReader(listDownloadedTxt));
			for (String i = reader.readLine(); i != null && !i.equals(""); i = reader.readLine()) {
				String[] detialArray = i.split("\t");
				collection.add(new Vsong(Integer.parseInt(detialArray[0].substring(2, detialArray[0].length())),detialArray[1]));
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
	public static void testReading() {
		LocalReader localReader = new LocalReader();
		//Map<String, String> map = localRecorder.getIsDownloaded();
		localReader.readRecord();
		System.out.println(localReader.getCollection());
		
	}

	public static void main(String[] args) throws IOException {
		testReading();

	}
	
}
