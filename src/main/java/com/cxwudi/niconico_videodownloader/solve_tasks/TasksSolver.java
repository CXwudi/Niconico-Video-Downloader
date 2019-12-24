package com.cxwudi.niconico_videodownloader.solve_tasks;

import com.cxwudi.niconico_videodownloader.entity.NicoDriver;
import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.AbstractVideoDownloader;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.DLMethodNamesEnum;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.IDMwithYoutubeDLDownloader;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.YoutubeDLDownloader;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
/**
 * The main class for downloading videos and recording them into a file. It will loop through the TreeSet task,
 * for each Vocaloid Song in this Treeset, the manager will assign InfoGainer, VideoDownloader, and LocalRecorder 
 * to do the whole process for each Vocaloid song.
 * 
 * Details of VideoDownloader, InfoGainer, and LocalRecorder will be discussed in their class description. 
 * @author CX无敌
 *
 */
public class TasksSolver {
	private AbstractVideoDownloader downloader;
	private InfoGainer infoGainer;
	private LocalRecorder localRecorder;
	private ToTaskGenerator toTaskGenerator;
	
	private Set<Vsong> task; //currently use TreeSet

	public TasksSolver(NicoDriver d, Set<Vsong> task, Set<Vsong> update) {
		this.task = task;
		this.downloader = getDownloader(Config.getDownloadMethod());
		this.infoGainer = new InfoGainer(d);
		this.localRecorder = new LocalRecorder(update);
		this.toTaskGenerator = new ToTaskGenerator();
		//Learn java: addShutdownHook can tell java application to do something after the application is shut down
		//However, eclipse red button terminal the application with SIGKILL but not SIGTERM, so addShutdownHook doesn't work in eclipse
		Runtime.getRuntime().addShutdownHook(new Thread(this::triggerRecord));
	}

	/**
	 * get the proper downloader implementation for this {@link TasksSolver}
	 * @param methodName the enum of method name
	 * @return a downloader instance
	 */
	private AbstractVideoDownloader getDownloader(DLMethodNamesEnum methodName){
		switch (methodName){
			case YOUTUBE_DL: //use youtube-dl only
				return new YoutubeDLDownloader();
			case IDM:
				var idmFile = Config.getIdmFile();
				if (idmFile.exists()) {
					return new IDMwithYoutubeDLDownloader();
				} else {
					logger.error("CXwudi and Miku cannot find the IDMan.exe file in {}, using default youtube-dl downloader now 😂", idmFile.toString());
					return AbstractVideoDownloader.getDefaultDownloader();
				}
			default: logger.error("CXwudi and Miku can not supported downloader method: {}, using default youtube-dl downloader now 😂",
					Config.getDownloadMethod());
				return AbstractVideoDownloader.getDefaultDownloader();
		}
	}
	
	public void forEachVsongInTask(Consumer<Vsong> c) {
		for (Iterator<Vsong> iterator = task.iterator(); iterator.hasNext();) {
			c.accept(iterator.next());
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
	 * @return {@code SUCCESS} if the download process success, otherwise, return others {@link DownloadStatus}.
	 */
	public DownloadStatus downloadOneVocaloidPV(VsongDownloadTask song) {
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

	/**
	 * Convert a Vsong that had field been filled up to a VsongDownloadTask for VideoDownloader to download the song
	 * @param song the input vsong
	 * @return
	 */
	public VsongDownloadTask convertVsongToTask(Vsong song){
		return toTaskGenerator.vsongToTask(song);
	}

	public void setDriver(NicoDriver driver) {
		infoGainer.setDriver(driver);
	}

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * @deprecated not encourage to use, plz use forEachVsongInTask
	 */
	@Deprecated
	public void downloadVocaloidPVs() {
		for (Iterator<Vsong> iterator = task.iterator(); iterator.hasNext();) {
			Vsong vsong = iterator.next();
			fetchInfo(vsong);
			var task = toTaskGenerator.vsongToTask(vsong);
			if (downloadOneVocaloidPV(task) == DownloadStatus.SUCCESS)
				markDone(vsong);
			triggerRecord();//we add this line for now
		}
	}


}
