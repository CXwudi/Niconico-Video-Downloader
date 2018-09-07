import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
		defaultDir.mkdirs(); //this step should not 
		this.rootDLdir = new File(downloadDir);
	}
	/**
	 * Download the Vocaloid Song.
	 * @param song the song to be download.
	 * @return {@code true} if the Vocaloid PV file is downloaded and ready to be watched.
	 */
	public boolean downloadVocaloidPV(Vsong song) {
		Objects.requireNonNull(song);
		if (song.getURL().equals("")) return false;
		
		File file = makeVideoFile(song);//make a file to receive data from above input stream.
		if (file.isFile()) {
			file.delete();	//we'll see if this can fix the broken file issue
		}
		//WARNING: don't try to use try-on-resource for now, broken video file detected when using try-on-resource
		//need further investigation on why this is happen, is it my bad or a problem from niconico's server.
		try {
			downloadUsingStream(song, file);
			//downloadUsingNIO(song, file);
			return true;
		}  catch (IOException e) {
			e.printStackTrace();
			System.err.println("どうしよう!!!!, CXwudi and Miku failed to download " + song.getTitle() + " from " + song.getURL());
			return false;
		}
	}
	private void downloadUsingStream(Vsong song, File file) throws IOException{
		var input = new BufferedInputStream(new URL(song.getURL()).openStream()); //get the input stream from video url
		var output = new FileOutputStream(file); 	//create FileOutputStream for the above file.
		
		//start downloading process
		System.out.println("start downloading");
		byte[] buffer = new byte[1024];
		int count=0;
		while((count = input.read(buffer,0,1024)) != -1){
		    output.write(buffer, 0, count);
		}
		output.close();
		input.close();
		if (file.renameTo(new File(file.getParentFile(), generateFileName(song)))) {
			System.out.println(file.getName() + " done, yeah!!");
		} else {
			System.out.println(file.getName() + " done, but rename fail :(");
		}
	}
	
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
	
	private File makeVideoFile(Vsong song) {
		String subDir = NicoStringTool.fixFileName(song.getSubDir());
		var fileName = generateFileName(song);
		//make sure the subfolder is created, otherwise downloading PV might cause problem
		File dir = new File(rootDLdir, subDir);
		try {
			if (!dir.exists()) 
				dir.mkdirs();//Learn java: even enough your File is xxx/xxx.mp4, mkdirs() will still create folders, but folder is named as "xxx.mp4"
			if (!dir.isDirectory()) 
				throw new SecurityException("Such path name is not a directory");//a fake exception
		} catch (SecurityException e) {
			System.err.println(e + "\nCXwudi and miku found that this directory" + dir + "is not avaliable, video file is now made on default directory as " + new File(defaultDir, fileName.toString()));
			dir = defaultDir;
		}
		
		return new File(dir, NicoStringTool.fixFileName(fileName.toString()));
	}
	private String generateFileName(Vsong song) {
		String title = song.getTitle();
		var fileName = new StringBuilder(title);
		
		if (!song.getProducerName().equals("") && !title.contains(song.getProducerName())) {
			fileName.append("【")
					.append(song.getProducerName())
					.append("】");
		}
		fileName.append(".mp4");
		return fileName.toString();
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
	}
	private static void testFile() {
		VideoDownloader v = new VideoDownloader();
		File video = new File(v.rootDLdir + "\\" + "resume upload.mp4");
		System.out.println(video.isFile());
		System.out.println(video);
		video = new File(v.rootDLdir, "");
		System.out.println(video);
	}

}
