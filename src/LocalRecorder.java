import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
/**
 *TODO add comment
 * @author CX无敌
 *
 */
public class LocalRecorder {
	private PrintWriter writer;
	private File listDownloadedTxt;
	private TreeSet<Vsong> update;
	
	public LocalRecorder(TreeSet<Vsong> update) {
		this.update = update;
		listDownloadedTxt = new File(new File("."), "downloaded.txt");
		try {
			listDownloadedTxt.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param songs Vocaloid Songs that have been downloaded
	 * @return @{@code true} if {@code treeSet.addAll()} return true;
	 */
	public boolean markDone(Vsong... songs) {
		return update.addAll(Arrays.asList(songs));
	}
	
	public void writeRecord() {
		try {
			writer = new PrintWriter(listDownloadedTxt);
			for (Iterator<Vsong> iterator = update.iterator(); iterator.hasNext();) {
				Vsong vsong = iterator.next();
				StringBuilder sb = new StringBuilder();
				sb.append("sm").append(vsong.getSmId()).append("\t").append(vsong.getTitle());
				writer.println(sb.toString());
			}
			writer.close();
		} catch (IOException e) {
			System.err.println(e + "\nthis shouldn't happen");
		}
	}

}
