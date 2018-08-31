import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
/**
 * The video downloader is the main class of downloading Vocaloid PV,
 * it also takes in charge of making mp4 file in the corespond folder.
 * @author CX无敌
 *
 */
public class VideoDownloader {
	private final static File defaultDir = new File("D:\\11134\\Download\\Video");//TODO: use System video directory
	private File downloadDir;
	/**
	 * create video downloader with default downloading folder D:\\11134\\Download\\Video
	 */
	public VideoDownloader() {
		this(defaultDir.toString());
	}
	/**
	 * create video downloader with defined downloading folder
	 * @param downloadDir the root directory of downloaded video.
	 */
	public VideoDownloader(String downloadDir) {
		defaultDir.mkdirs();
		this.downloadDir = new File(downloadDir);
	}
	/**
	 * Download the Vocaloid Song.
	 * @param song the song to be download.
	 * @return {@code true} if the Vocaloid PV file is downloaded and ready to be watched.
	 */
	public boolean downloadVocaloidPV(Vsong song) {
		if (song.getURL().equals("")) return false;
		try {
			BufferedInputStream input = new BufferedInputStream(new URL(song.getURL()).openStream()); //get the input stream from video url
			File file = makeAFile(song); 							//make a file to receive data from above input stream.
			FileOutputStream output = new FileOutputStream(file); 	//create FileOutputStream for the above file.
			
			//start downloading process
			System.out.println("start downloading");
			byte[] buffer = new byte[1024*1024];
			int count=0;
	        while((count = input.read(buffer,0,1024*1024)) != -1){
	            output.write(buffer, 0, count);
	        }
			output.close();
			input.close();
			
			System.out.println(file.getName() + " done, yeah!!");
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("どうしよう!!!!, CXwudi and Miku failed to download " + song.getTitle() + " from " + song.getURL());
			return false;
		}
	}
	
	private File makeAFile(Vsong song) {
		String properTitle = song.getTitle().replaceAll("/", "-").replaceAll("\\\\", "-").replaceAll("\\?", " ");
		StringBuilder fileName = new StringBuilder(properTitle);
		if (!song.getProducerName().equals("")) {
			fileName.append("【")
					.append(song.getProducerName())
					.delete(fileName.length()-3, fileName.length())
					.append("】.mp4");
		}
		
		//Create the folder that associate to song's folder.
		File dir = new File(downloadDir, song.getSubDir());
		try {
			if (!dir.exists()) 
				dir.mkdirs();
			if (!dir.isDirectory()) 
				throw new SecurityException("Such path name is not a directory");
		} catch (SecurityException e) {
			e.printStackTrace();
			System.err.println("CXwudi and miku found that this directory" + dir + "is not avaliable, video file is now made on default directory as " + new File(defaultDir, fileName.toString()));
			dir = defaultDir;
		}
		
		return new File(dir, fileName.toString());
	}
	
	/**
	 * @return the downloadDir
	 */
	public File getDownloadDir() {
		return downloadDir;
	}
	/**
	 * @param downloadDir the downloadDir to set. if error happens, then default dir will be used instead.
	 */
	public void setDownloadDir(String downloadDir) {
		File dir = new File(downloadDir);
		try {
			if (dir.isDirectory()) 
				if (!dir.mkdirs()) throw new SecurityException("fail to make directory");
			else throw new SecurityException("Such path name is not a directory");
			this.downloadDir = dir;
		} catch (SecurityException e) {
			e.printStackTrace();
			System.err.println("CXwudi and miku found that this directory " + dir + " is not avaliable, plz try again.\nA default directroy D:\\11134\\Download\\Video has been set instead.");
			this.downloadDir = defaultDir;
		}
		
	}
	public static void main(String[] args) {
		testFile();
	}
	private static void testFile() {
		VideoDownloader v = new VideoDownloader();
		File video = new File(v.downloadDir + "\\" + "resume upload.mp4");
		System.out.println(video.isFile());
		System.out.println(video);
		video = new File(v.downloadDir, "");
		System.out.println(video);
	}

}
