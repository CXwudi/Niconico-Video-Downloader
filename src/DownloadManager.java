import java.util.TreeSet;

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
	
	public void downloadVocaloidPV() {
		/*TODO: write for loop to download videos from task sets and update local list 
		 * by calling InfoGainer's, VideoDownloader's and LocalRecorder's methods
		 */
	}
	
	public void fetchInfo(Vsong song) {
		//TODO: fulfill Vsong instance's other infomation including url, producer name, etc. by visiting niconico website.
	}
	
	public boolean downloadOneVocaloidPV(Vsong song) {
		return downloader.downloadVocaloidPV(song);
	}
	
	public void updateOneVocaloidPV(Vsong song) {
		//TODO: update one Vsong into local file
	}

}
