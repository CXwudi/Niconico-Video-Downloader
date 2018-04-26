import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class LocalListGrabber extends CollectionReader{
	private BufferedReader reader;
	private PrintWriter writer;
	private File listDownloadedTxt;

	public LocalListGrabber() {
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
			super.readRecord();
		} catch (IOException e) {
			System.err.println("this shouldn't happen");
			e.printStackTrace();
			isDone = false;
		}
		
	}
	public static void testReading() {
		LocalListGrabber localListGrabber = new LocalListGrabber();
		//Map<String, String> map = localRecorder.getIsDownloaded();
		localListGrabber.readRecord();
		System.out.println(localListGrabber.getCollection());
		
	}

	public static void main(String[] args) throws IOException {
		testReading();

	}
	
}
