package com.cxwudi.niconico_videodownloader.solve_tasks;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static com.cxwudi.niconico_videodownloader.util.DownloadStatus.*;

/**
 * The video downloader is the main class of downloading Vocaloid PV,
 * and stores them in the correspond folder.
 * @author CX无敌
 *
 */
public class YoutubeDLVideoDownloader {
	private File rootDLdir;  //user defined download dir
	/**
	 * create video downloader with default downloading folder 
	 */
	public YoutubeDLVideoDownloader() {
		this(Config.getRootOutputDir().toString());
	}
	/**
	 * create video downloader with user defined downloading folder
	 * @param downloadDir the root directory of downloaded video.
	 */
	public YoutubeDLVideoDownloader(String downloadDir) {
		var rootDLdir = new File(downloadDir);
		rootDLdir.mkdirs(); //this step should not make error
		this.rootDLdir = rootDLdir;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Download the Vocaloid Song and rename it properly. 
	 * it's a warper function of {@link #downloadUsingYoutube_dl}
	 * @param task the download task to be performed
	 * @return {@code SUCCESS} if the Vocaloid PV file is downloaded and renamed, otherwise, return others {@link DownloadStatus}.
	 */
	public DownloadStatus downloadVocaloidPV(VsongDownloadTask task) {
		Objects.requireNonNull(task);
		if (task.getSong().getId().equals("")) return FAIL_INITIAL;
		var song = task.getSong();
		var file = task.getOutputDir();
		var songFileName = task.getFileName();
		
		logger.info("It's show time for Youtube-dl");
		try {
			downloadUsingYoutube_dl(song, file, songFileName);
			//downloadUsingStream(song, file);
			//downloadUsingNIO(song, file);
			if (task.getVsongFile().exists()){
				logger.info("download success");
				return SUCCESS;
			} else {
				logger.warn("Oh no, CXwudi and Miku fail to find download file {}, download process may fail ", task.getVsongFile().toString());
				return FAIL_DOWNLOAD;
			}

		}  catch (IOException e) {
			e.printStackTrace();
			logger.error("どうしよう!!!!, CXwudi and Miku failed to start the process on downloading " + song.getTitle() + " from " + song.getURL(), e);
			return FAIL_DOWNLOAD;
		}
	}
	
	/**
	 * The core honor function that download videos from a niconico website denoted by vsong obj
	 * it use 3rd library executable downloader, youtube-dl to achieve the download process.
	 * thanks for the awesome 3rd library from https://github.com/rg3/youtube-dl
	 * @param song the song to downloaded
	 * @param dir the directory of where the video to be download.
	 * @throws IOException
	 */
	private void downloadUsingYoutube_dl(Vsong song, File dir, String fileName) throws IOException{
		//initialize variables and cmd process
		var youtube_dlProcessBuilder = new ProcessBuilder(
				Config.getYoutubeDlFile().getAbsolutePath(),
				"-v",
				"--username", new StringBuilder().append('"').append(Config.getEmail()).append('"').toString(),
				"--password", new StringBuilder().append('"').append(Config.getPassword()).append('"').toString(),
				"https://www.nicovideo.jp/watch/" + song.getId(),
				"-o", fileName,
				"-f",
				"\"best[height<=720]\"");
		//set the download directory to the proper subfolder, for example, 20xx年V家新曲
		youtube_dlProcessBuilder.directory(dir);
		logger.debug("start running youtube-dl command: \n{}", youtube_dlProcessBuilder.command());
		logger.debug("Vsong file saved to {}", dir.getAbsolutePath());

		//start the process
		Process youtube_dlProcess = youtube_dlProcessBuilder.start();
		//redirect output/error stream of cmd to java stdout/stderr
		var stdOutStrBuilder = new StringBuilder();
		var stdErrStrBuilder = new StringBuilder();
		
		var stdOutThread = new Thread(() -> {
			syncStream(youtube_dlProcess.getInputStream(), stdOutStrBuilder, System.out);
		}, "StdOut");
		stdOutThread.start();
		
		var stdErrThread = new Thread(() -> {
			syncStream(youtube_dlProcess.getErrorStream(), stdErrStrBuilder, System.err);
		}, "StdErr");
		stdErrThread.start();
		
		//redirect java stdin to cmd input, and type cmd command to invoke downloading process
//		var stdIn = new PrintWriter(youtube_dlProcess.getOutputStream());
//		//stdIn.println("echo " + file.toString());
//		stdIn.println(new StringBuilder().append(System.getProperty("user.dir")).append("/youtube-dl -v")
//				.append(" --username \"1113421658@qq.com\"").append(" --password \"2010017980502\"")
//				.append(" https://www.nicovideo.jp/watch/").append(song.getId())
//				.append(" -f \"best[height<=720]\""));
//		stdIn.close();
		
		//wait for the downloading process
		try {
			youtube_dlProcess.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//wait for thread to be finished
		try {
			stdOutThread.join();
			stdErrThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//check is success or not
		double state = Double.parseDouble(stdOutStrBuilder.substring(
		 stdOutStrBuilder.lastIndexOf("%")-4, stdOutStrBuilder.lastIndexOf("%")));
		//if the downloading process does not hit 100.0%, or returning error, then restart the process to continue.
		//this is a very useful code to handle niconico website occasionally return HTTP 403 forbidden.
		if (stdErrStrBuilder.toString().contains("ERROR") || state < 100) {
			logger.info("Don't worry, CXwudi and Miku will retry downloading again from " + state + "%");
			downloadUsingYoutube_dl(song, dir, fileName);
		}
		
	}
	private void syncStream(InputStream input, StringBuilder sb, PrintStream out) {
		try(var output = new BufferedReader(new InputStreamReader(input))){
			String s;
			int c = 0;
			while ((s = output.readLine()) != null) {
				if (sb != null) sb.append(s).append("\n");
				if (c++ >= 5 || !s.contains("ETA")) {
					out.println(s);
					c = 0;
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the current root directory that set to download
	 */
	public File getDownloadDir() {
		return rootDLdir;
	}
	/**
	 * @param downloadDir the rootDLdir to set. if error happens, then default dir will be used instead.
	 */
	public void setDownloadDir(String downloadDir) {
		File dir = new File(downloadDir);
		try {
			if (dir.isDirectory()) 
				if (!dir.mkdirs()) throw new SecurityException("fail to make directory");
			else throw new SecurityException("Such path name is not a directory");
			this.rootDLdir = dir;
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.info("CXwudi and miku found that this directory " + dir + " is not avaliable, plz try again.\n" +
					"A default directroy " + Config.getRootOutputDir() + " has been set instead.");
			this.rootDLdir = Config.getRootOutputDir();
		}
		
	}

//	/**
//	 * @Deprecated
//	 * This method is unstable after 2018 April when Niconico web site has updated that allows
//	 * account-free video watching. From then on, video url sometime receive HTTP 403 forbidden that
//	 * cause the on-going downloading process stops.
//	 * @param song vsong to download
//	 * @param file folder of vsong
//	 * @throws IOException
//	 */
//	@Deprecated
//	private void downloadUsingStream(Vsong song, File file) throws IOException{
//		try (var input = new BufferedInputStream(new URL(song.getURL()).openStream()); // get the input stream from video url
//				var output = new FileOutputStream(file);) { // create FileOutputStream for the above file.
//
//			// start downloading process
//			logger.info("start downloading");
//			byte[] buffer = new byte[1024];
//			int count = 0;
//			int size = 0;
//			while ((count = input.read(buffer, 0, 1024)) != -1) {
//				output.write(buffer, 0, count);
//				size += count;
//			}
//			logger.info("file size = " + size);
//			if (file.renameTo(new File(file.getParentFile(), generateFileName(song)))) {
//				logger.info(file.getName() + " done, yeah!!");
//			} else {
//				logger.info(file.getName() + " done, but rename fail :(");
//			}
//		}
//	}
//
//	@Deprecated
//	private void downloadUsingNIO(Vsong song, File file) throws IOException {
//		 var rbc = Channels.newChannel(new URL(song.getURL()).openStream());
//		 var output = new FileOutputStream(file);
//		 logger.info("start downloading");
//		 output.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//		 output.close();
//	     rbc.close();
//		if (file.renameTo(new File(file.getParentFile(), generateFileName(song)))) {
//			logger.info(file.getName() + " done, yeah!!");
//		} else {
//			logger.info(file.getName() + " done, but rename fail :(");
//		}
//	}

}
