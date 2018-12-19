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
		//Learn java: addShutdownHook can tell java application to do something after the application is shut down
		//However, eclipse red button terminal the application with SIGKILL but not SIGTERM, so addShutdownHook doesn't work in eclipse
		Runtime.getRuntime().addShutdownHook(new Thread(this::triggerRecord));
	}
	
	public void forEachVsongInTask(Consumer<Vsong> c) {
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
	 * @param song the Vocaloid song to be filled.
	 * @return {@code true} iff {@link InfoGainer#fetchInfo(Vsong)} return true.
	 */
	public boolean fetchInfo(Vsong song) {
		return infoGainer.fetchInfo(song);
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
	 * put the one or more Vsong that has been downloaded into local recorder.
	 * Be aware that this method does not record them into the local file yet, 
	 * call {@link #triggerRecord()} to do so. 
	 * @param songs songs that has been downloaded.
	 */
	public void markDone(Vsong... songs) {
		localRecorder.markDone(songs);
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
