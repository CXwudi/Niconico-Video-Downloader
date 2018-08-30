import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
/**
 * The main manager for downloading videos and recording them into a file. It will loop through the TreeSet task,
 * for each Vocaloid Song in this Treeset, the manager will assign InfoGainer, VideoDownloader, and LocalRecorder 
 * to do the whole process for each Vocaloid song.
 * 
 * Details of VideoDownloader, InfoGainer, and LocalRecorder will be discussed in their class description. 
 * @author CX无敌
 *
 */
public class DownloadManager {
	private VideoDownloader downloader;
	private InfoGainer infoGainer;
	private LocalRecorder localRecorder;
	
	private TreeSet<Vsong> task;

	public DownloadManager(NicoDriver d, TreeSet<Vsong> task, TreeSet<Vsong> update) {
		this.task = task;
		this.downloader = new VideoDownloader();
		this.infoGainer = new InfoGainer(d);
		this.localRecorder = new LocalRecorder(update);
	}
	
	public void forEachVsong(Consumer<Vsong> c) {
		for (Iterator<Vsong> iterator = task.iterator(); iterator.hasNext();) {
			c.accept(iterator.next());
		}
	}
	public void downloadVocaloidPVs() {
		for (Iterator<Vsong> iterator = task.iterator(); iterator.hasNext();) {
			Vsong vsong = iterator.next();
			fetchInfo(vsong);
			if (downloadOneVocaloidPV(vsong))
				markDone(vsong);
			triggerRecord();//we add this line for now
		}
	}
	/**
	 * fulfill Vsong instance's other information including url, producer name, etc. by visiting niconico website.
	 * @param song the Vocaloid song to be filled.
	 */
	public void fetchInfo(Vsong song) {
		infoGainer.fetchInfo(song);
	}
	/**
	 * The honor method to download the Vocaloid PV
	 * @param song
	 * @return {@code true} if the downloading process succeed.
	 */
	public boolean downloadOneVocaloidPV(Vsong song) {
		return downloader.downloadVocaloidPV(song);
	}
	/**
	 * put the one or more Vsong (depends on number of parameters)that has been downloaded into local recorder.
	 * Be aware that this method does not record them into the local file yet.
	 * Call {@code triggerRecord()} to do so. 
	 * @param song 
	 */
	public void markDone(Vsong... song) {
		localRecorder.markDone(song);
	}
	/**
	 * Tell local record to start the record process.
	 */
	public void triggerRecord() {
		localRecorder.writeRecord();
	}

	public void setDriver(NicoDriver driver) {
		infoGainer.setDriver(driver);
	}
	
	

}
