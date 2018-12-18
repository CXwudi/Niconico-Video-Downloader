import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
/**
 * @author CX无敌
 *
 */
public class LocalRecorder {
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
		try (PrintWriter writer = new PrintWriter(listDownloadedTxt)){
			for (Iterator<Vsong> iterator = update.iterator(); iterator.hasNext();) {
				Vsong vsong = iterator.next();
				StringBuilder sb = new StringBuilder();
				sb.append("sm").append(vsong.getId()).append("------").append(vsong.getTitle());
				writer.println(sb.toString());
			}
		} catch (IOException e) {
			System.err.println(e + "\nthis shouldn't happen");
		} 
	}

}
