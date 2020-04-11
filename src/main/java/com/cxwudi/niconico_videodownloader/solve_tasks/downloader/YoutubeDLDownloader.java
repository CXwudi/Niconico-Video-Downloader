package com.cxwudi.niconico_videodownloader.solve_tasks.downloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;

/**
 * An implementation of {@link AbstractVideoDownloader} that drives youtube-dl to download the Vocaloid PV
 * @author CX无敌
 */
public class YoutubeDLDownloader extends AbstractVideoDownloader {
	public YoutubeDLDownloader(){}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * The core honor function that download videos from a niconico website denoted by vsong obj
	 * it use 3rd library executable downloader, youtube-dl to achieve the download process.
	 * thanks for the awesome 3rd library from https://github.com/rg3/youtube-dl
	 * @param song the song to downloaded
	 * @param dir the directory of where the video to be download.
	 * @return {@link DownloadStatus#SUCCESS} always (hence this downloader is very strong to successfully download video)
	 * @throws IOException
	 */
	@Override
	protected DownloadStatus downloadImpl(Vsong song, File dir, String fileName) throws IOException{
		//initialize variables and cmd process
		var youtubeDLProcessBuilder = new ProcessBuilder(
				Config.getYoutubeDlFile().getAbsolutePath(),
				"-v",
				"--username", '"' + Config.getEmail() + '"',
				"--password", '"' + Config.getPassword() + '"',
				"https://www.nicovideo.jp/watch/" + song.getId(),
				"-o", fileName,
				"-f",
				"\"best[height<=720]\"");
		//set the download directory to the proper subfolder, for example, 20xx年V家新曲
		youtubeDLProcessBuilder.directory(dir);
		logger.debug("start running youtube-dl command: \n{}", youtubeDLProcessBuilder.command());
		logger.debug("Vsong file saved to {}", dir.getAbsolutePath());

		//start the process
		Process youtubeDlProcess = youtubeDLProcessBuilder.start();
		//redirect output/error stream of cmd to java stdout/stderr
		var stdOutStrBuilder = new StringBuilder();
		var stdErrStrBuilder = new StringBuilder();
		
		var stdOutThread = new Thread(() -> {
			syncStream(youtubeDlProcess.getInputStream(), stdOutStrBuilder, System.out);
		}, "StdOut");
		stdOutThread.start();
		
		var stdErrThread = new Thread(() -> {
			syncStream(youtubeDlProcess.getErrorStream(), stdErrStrBuilder, System.err);
		}, "StdErr");
		stdErrThread.start();
		
		//wait for the downloading process
		try {
			youtubeDlProcess.waitFor();
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
			return downloadImpl(song, dir, fileName);
		} else {
			return DownloadStatus.SUCCESS;
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

//	/**
//	 * @Deprecated
//	 * This method is unstable after 2018 April when Niconico web site has updated that allows
//	 * account-free video watching. From then on, video url will receive HTTP 403 if the heartbeat is not received every 60s
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
