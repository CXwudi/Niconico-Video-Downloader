import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Objects;
/**
 * The video downloader is the main class of downloading Vocaloid PV,
 * and stores them in the correspond folder.
 * @author CX无敌
 *
 */
public class VideoDownloader {
	private final static File defaultDir = new File(System.getProperty("user.home") + "\\Videos");
	private File rootDLdir;  //user defined download dir
	/**
	 * create video downloader with default downloading folder 
	 */
	public VideoDownloader() {
		this(defaultDir.toString());
	}
	/**
	 * create video downloader with user defined downloading folder
	 * @param rootDLdir the root directory of downloaded video.
	 */
	public VideoDownloader(String downloadDir) {
		defaultDir.mkdirs(); //this step should not make error
		this.rootDLdir = new File(downloadDir);
	}
	/**
	 * Download the Vocaloid Song.
	 * @param song the song to be download.
	 * @return {@code true} if the Vocaloid PV file is downloaded and ready to be watched.
	 */
	@SuppressWarnings("finally")
	public boolean downloadVocaloidPV(Vsong song) {
		Objects.requireNonNull(song);
		if (song.getURL().equals("")) return false;
		
		File file = makeDirForDownloadingVideoFile(song);//It's a file represents a directory
		
		//TODO: write code to use Youtube-dl to download file
		
		try {
			downloadUsingYoutube_dl(song, file);
			//downloadUsingStream(song, file);
			//downloadUsingNIO(song, file);
			return true;
		}  catch (IOException e) {
			e.printStackTrace();
			System.err.println("どうしよう!!!!, CXwudi and Miku failed to download " + song.getTitle() + " from " + song.getURL());
			return false;
		} finally {
			System.out.println("done");
			return false;
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
			System.err.println(e + "\nCXwudi and miku found that this directory" + dir + "is not avaliable, default directory is set, as " + defaultDir.toString());
			dir = defaultDir;
		}
		
		return dir;
	}
	
	private void downloadUsingYoutube_dl(Vsong song, File file) throws IOException{
		//initialize variables and cmd process
		var youtube_dlProcessBuilder = new ProcessBuilder("cmd");
		youtube_dlProcessBuilder.directory(file);
		System.out.println("It's show time for Youtube-dl");
		var youtube_dlProcess = youtube_dlProcessBuilder.start();
		var stdOutStrBuilder = new StringBuilder();
		var stdErrStrBuilder = new StringBuilder();
		
		//redirect output/error stream of process to java stdout/stderr
		new Thread(() -> {
			syncStream(youtube_dlProcess.getInputStream(), stdOutStrBuilder, System.out);
		}).start();
		
		new Thread(() -> {
			syncStream(youtube_dlProcess.getErrorStream(), stdErrStrBuilder, System.err);
		}).start();
		//redirect java stdin to cmd input
		var stdIn = new PrintWriter(youtube_dlProcess.getOutputStream());
		//create downloading process, Powered by youtube-dl, thanks for the awesome 3rd library from https://github.com/rg3/youtube-dl
		stdIn.println(new StringBuilder().append(System.getProperty("user.dir")).append("/youtube-dl -v ")
				.append("https://www.nicovideo.jp/watch/").append(song.getId()));
		stdIn.close();
		
		//wait for the downloading process
		try {
			youtube_dlProcess.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//check is success or not
		int state = NicoStringTool.filterIntsFromString(stdOutStrBuilder.substring(
				stdOutStrBuilder.lastIndexOf("%")-5, stdOutStrBuilder.lastIndexOf("%")));
		if (stdErrStrBuilder.toString().contains("ERROR") || state < 100) { //if the downloading process does not hit 100.0%
			System.out.println("Don't worry, CXwudi and Miku will retry downloading again from " + state + "%");
			downloadUsingYoutube_dl(song, file);
		}
		
	}
	private void syncStream(InputStream input, StringBuilder sb, PrintStream out) {
		try(var output = new BufferedReader(new InputStreamReader(input))){
			String s;
			while ((s = output.readLine()) != null) {
				if (sb != null) sb.append(s).append("\n");
				out.println(s);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		var fileNameString = fileNameBuilder.toString().replace("オリジナル", "").replace("MV", "").replace("【】", "");
		fileNameString = NicoStringTool.fixFileName(fileNameString);
		System.out.println("next download: " + fileNameString);
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
			this.rootDLdir = defaultDir;
		}
		
	}
	public static void main(String[] args) {
		testFile();
		testStream();
		testDownload();
	}
	
	private static void testDownload() {
		VideoDownloader v = new VideoDownloader();
		v.downloadVocaloidPV(new Vsong("sm34225660").setSubDir("2018年V家新曲").setURL("fake url"));
		
	}
	private static void testStream() {
		var stdout = new PrintWriter(System.out, true);
		stdout.println("print from print writer");
	}
	private static void testFile() {
		VideoDownloader v = new VideoDownloader();
		File video = new File(v.rootDLdir + "\\" + "a video.mp4");
		System.out.println(video.isFile());
		System.out.println(video);
		video = new File(v.rootDLdir, "");
		System.out.println(video);
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
