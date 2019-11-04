package com.cxwudi.niconico_videodownloader.solve_tasks;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.util.Config;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import com.cxwudi.niconico_videodownloader.util.NicoStringTool;

/**
 * The video downloader is the main class of downloading Vocaloid PV,
 * and stores them in the correspond folder.
 * @author CX无敌
 *
 */
public class VideoDownloader {
	private File rootDLdir;  //user defined download dir
	/**
	 * create video downloader with default downloading folder 
	 */
	public VideoDownloader() {
		this(Config.OUTPUT_ROOT_DIR.toString());
	}
	/**
	 * create video downloader with user defined downloading folder
	 * @param rootDLdir the root directory of downloaded video.
	 */
	public VideoDownloader(String downloadDir) {
		Config.OUTPUT_ROOT_DIR.mkdirs(); //this step should not make error
		this.rootDLdir = new File(downloadDir);
	}
	/**
	 * Download the Vocaloid Song and rename it properly. 
	 * it's a warper function of {@link #downloadUsingYoutube_dl(Vsong, File)} 
	 * @param song the song to be download.
	 * @return {@code SUCCESS} if the Vocaloid PV file is downloaded and renamed, otherwise, return others {@link DownloadStatus}.
	 */
	public DownloadStatus downloadVocaloidPV(Vsong song) {
		Objects.requireNonNull(song);
		if (song.getId().equals("")) return DownloadStatus.FAIL_INITIAL;
		File file = makeDirForDownloadingVideoFile(song);//get the directory
		
		System.out.println("It's show time for Youtube-dl");
		try {
			downloadUsingYoutube_dl(song, file);
			//downloadUsingStream(song, file);
			//downloadUsingNIO(song, file);
			System.out.println("download success");
		}  catch (IOException e) {
			e.printStackTrace();
			System.err.println("どうしよう!!!!, CXwudi and Miku failed to start the process on downloading " + song.getTitle() + " from " + song.getURL());
			return DownloadStatus.FAIL_DOWNLOAD;
		} 
		
		if (song.getId().contains("nm")) {
			System.out.println("since we don't grab the info for nm-id video " + song.getId() + ", no renaming action performed, but file was download");
			return DownloadStatus.FAIL_RENAME;
		}
		
		File[] videoFiles = file.listFiles( (dir, aFile) -> {
			return aFile.toString().contains(song.getId());
		});
		if (videoFiles.length > 0) {
			var videoFile = videoFiles[0];
			if (videoFile.renameTo(new File(videoFile.getParentFile(), generateFileName(song)))) {
				System.out.println("rename success");
				return DownloadStatus.SUCCESS;
			} else {
				System.err.println("rename fail:(, renamed file might already exists");
				return DownloadStatus.FAIL_RENAME;
			}
			
		} else { //although so-id and pure-number id are same, but youtube-dl rename the file with the input id
			System.out.println("VideoDownloader.downloadVocaloidPV() fail to find the downloaded video file");
			return DownloadStatus.FAIL_UNKNOWN;
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
	private void downloadUsingYoutube_dl(Vsong song, File dir) throws IOException{
		//initialize variables and cmd process
		var youtube_dlProcessBuilder = new ProcessBuilder(
				Config.YOUTUBE_DL_FILE.getAbsolutePath(),
				"-v",
				"--username", new StringBuilder().append('"').append(Config.EMAIL).append('"').toString(),
				"--password", new StringBuilder().append('"').append(Config.PASSWORD).append('"').toString(),
				"https://www.nicovideo.jp/watch/" + song.getId(),
				"-f",
				"\"best[height<=720]\"");
		//set the download directory to the proper subfolder, for example, 20xx年V家新曲
		youtube_dlProcessBuilder.directory(dir);
		
		//start the process
		Process youtube_dlProcess = youtube_dlProcessBuilder.start();
		//redirect output/error stream of cmd to java stdout/stderr
		var stdOutStrBuilder = new StringBuilder();
		var stdErrStrBuilder = new StringBuilder();
		
		var stdOutThread = new Thread(() -> {
			syncStream(youtube_dlProcess.getInputStream(), stdOutStrBuilder, System.out);
		});
		stdOutThread.start();
		
		var stdErrThread = new Thread(() -> {
			syncStream(youtube_dlProcess.getErrorStream(), stdErrStrBuilder, System.err);
		});
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
			System.out.println("Don't worry, CXwudi and Miku will retry downloading again from " + state + "%");
			downloadUsingYoutube_dl(song, dir);
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
	 * Create a proper directory for downloading the video, the last folder is named 
	 * as the same name of the subDir of this vsong. 
	 * @param song the Vsong to download.
	 * @return the file that contains the directory.
	 */
	private File makeDirForDownloadingVideoFile(Vsong song) {
		String subDir = NicoStringTool.fixFileName(song.getSubDir());
		//var fileNameString = generateFileName(song);
		//make sure the subfolder is created, otherwise downloading PV might cause problem
		File dir = new File(rootDLdir, subDir);
		try {
			if (!dir.exists()) 
				dir.mkdirs();//Learn java: even enough your File is xxx/xxx.mp4, mkdirs() will still create folders, but folder is named as "xxx.mp4"
			if (!dir.isDirectory()) 
				throw new SecurityException("Such path name is not a directory");//a fake exception
		} catch (SecurityException e) {
			System.err.println(e + "\nCXwudi and miku found that this directory" + dir + "is not avaliable, default directory is set, as " + Config.OUTPUT_ROOT_DIR.toString());
			dir = Config.OUTPUT_ROOT_DIR;
		}
		
		return dir;
	}
	/**
	 * generate an error-free string of the file name. e.g. xxxx.mp4
	 * @param song the Vsong object
	 * @return the error-free name of the file 
	 */
	private String generateFileName(Vsong song) {
		String title = song.getTitle();
		var fileNameBuilder = new StringBuilder(title);
		if (!song.getProducerName().equals("") && !title.contains(song.getProducerName())) {
			fileNameBuilder.append("【")
					.append(song.getProducerName())
					.append("】");
		}
		fileNameBuilder.append(".mp4");
		
		var fileNameString = fileNameBuilder.toString()
				.replace("オリジナル曲", "").replace("オリジナル", "")
				.replace("アニメ", "").replace("MV", "").replace("PV", "")
				.replace("[]", "").replace("【】", "");
		fileNameString = NicoStringTool.fixFileName(fileNameString);
		System.out.println("file name: " + fileNameString);
		return fileNameString;
	}
	
	/**
	 * @return the current root directory that set to download
	 */
	public File getDownloadDir() {
		return rootDLdir;
	}
	/**
	 * @param rootDLdir the rootDLdir to set. if error happens, then default dir will be used instead.
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
			System.err.println("CXwudi and miku found that this directory " + dir + " is not avaliable, plz try again.\nA default directroy D:\\11134\\Download\\Video has been set instead.");
			this.rootDLdir = Config.OUTPUT_ROOT_DIR;
		}
		
	}
	
	public static void main(String[] args) {
	}
	
	
	/**
	 * @Deprecated
	 * This method is unstable after 2018 April when Niconico web site has updated that allows 
	 * account-free video watching. From then on, video url sometime receive HTTP 403 forbidden that 
	 * cause the on-going downloading process stops.
	 * @param song vsong to download
	 * @param file folder of vsong
	 * @throws IOException
	 */
	@Deprecated
	private void downloadUsingStream(Vsong song, File file) throws IOException{
		try (var input = new BufferedInputStream(new URL(song.getURL()).openStream()); // get the input stream from video url
				var output = new FileOutputStream(file);) { // create FileOutputStream for the above file.
	
			// start downloading process
			System.out.println("start downloading");
			byte[] buffer = new byte[1024];
			int count = 0;
			int size = 0;
			while ((count = input.read(buffer, 0, 1024)) != -1) {
				output.write(buffer, 0, count);
				size += count;
			}
			System.out.println("file size = " + size);
			if (file.renameTo(new File(file.getParentFile(), generateFileName(song)))) {
				System.out.println(file.getName() + " done, yeah!!");
			} else {
				System.out.println(file.getName() + " done, but rename fail :(");
			}
		}
	}
	
	@Deprecated
	private void downloadUsingNIO(Vsong song, File file) throws IOException {
		 var rbc = Channels.newChannel(new URL(song.getURL()).openStream());
		 var output = new FileOutputStream(file);
		 System.out.println("start downloading");
		 output.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		 output.close();
	     rbc.close();
		if (file.renameTo(new File(file.getParentFile(), generateFileName(song)))) {
			System.out.println(file.getName() + " done, yeah!!");
		} else {
			System.out.println(file.getName() + " done, but rename fail :(");
		}
	}

}
