
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class VideoDownloader {
	WebDriver driver;
	String downloadDir = "D:\\11134\\Download\\Video";
	String downloadSubDir;

	String SMnumber = "sm32128035";
	String videoURL;
	String videoTitle;
	String producerName;
	

	// learn it by heart, there are 8 elements that can be searched by findElement()
	// method.
	// id, name, classname, tagname, cssSelector,
	// linkText, partialLinkText, xpath

	// when using cssSelector, use . to first indicate I want to search class,
	// secondly, to replace space with .
	// use [attribute=value] to search webelement by specific attributes,
	// if the value contains non-character (1!/...) plz put ''
	// 1. '^' symbol, represents the starting text in a string.
	// 2. '$' symbol represents the ending text in a string.
	// 3. '*' symbol represents contains text in a string.

	public VideoDownloader(WebDriver d) {
		// TODO Auto-generated constructor stub
		driver = d;
	}


	public void getVideoInfoFrom(String sm) {
		SMnumber = sm;
		videoURL = "";
		int repeated = 0;
		while (videoURL.equals("") || videoURL == null) {
			// driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			/*if (repeated++ >= 4) {
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {}
			}*/

			System.out.println(++repeated + " times try");
			MainModel.loadWebPage(driver, "http://www.nicovideo.jp/watch/" + SMnumber);
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) { }
			System.out.println("website opened");
			videoURL = driver.findElement(By.id("MainVideoPlayer")).findElement(By.cssSelector("video")).getAttribute("src");
			System.out.println("url reached: " + videoURL);
			videoTitle = driver.findElement(By.cssSelector("h1.VideoTitle")).getText();
			// System.out.println(videoTitle);
			producerName = driver.findElement(By.cssSelector("a.Link.VideoOwnerInfo-pageLink")).getAttribute("title");
			// System.out.println(producerName);
			// WebElement player = driver.findElement(By.id("MainVideoPlayer"));
			// System.out.println(player);
			// WebElement video = player.findElement(By.cssSelector("video"));
			// System.out.println(video);
			// videoURL = video.getAttribute("src");

		}

		System.out.println("SEE!! CXwudi and miku get it, here we are. URL = " + videoURL);
		System.out.println("title = " + videoTitle);
		System.out.println("producer = " + producerName);

	}

	public void OLDdownloadVideoTo(String subDir) {
		try {
			downloadSubDir = subDir.equals("")? "":"\\" + subDir ;
			Process downloadingProcess = Runtime.getRuntime().exec("cmd /c C:\\ChromeAuto\\wget64.exe -P " + downloadDir + downloadSubDir + " --no-check-certificate -nv " + videoURL);
			// driver.get("about:blank");// save internet speed by prevent brower from
			System.out.println("start downloading");
			/*InputStream is = downloadingProcess.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
			Thread.sleep(1000);
			String line;
			while(downloadingProcess.isAlive()) {
				line = br.readLine();
				System.out.println(line);
			}*/

			downloadingProcess.waitFor();
			System.out.println("done");
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
	}
	/**
	 *	download video and rename it by own generated title
	 *@param subDir: the folder to save the video file
	 *
	 */
	public void downloadVideoTo(String subDir) {
		String fileTile = videoTitle.contains(producerName) ? videoTitle
				: videoTitle + "【" + producerName.substring(0, producerName.length() - 3) + "】";
		downloadVideoTo(subDir, fileTile);
	}
	/**
	 *	download video and rename it by specific title;
	 *@param subDir: the folder to save the video file
	 *@param title: the title of the video.
	 */
	public void downloadVideoTo(String subDir, String title) {
		try {
			downloadSubDir = subDir.equals("")? "":"\\" + subDir ;
			URL url = new URL(videoURL);
			//ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			BufferedInputStream input = new BufferedInputStream(url.openStream());
			FileOutputStream output = new FileOutputStream(new File(downloadDir + downloadSubDir +"\\"+ title +  ".mp4"));
			System.out.println("start downloading");
			byte[] buffer = new byte[1024];
			//output.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			int count=0;
	        while((count = input.read(buffer,0,1024)) != -1)
	        {
	            output.write(buffer, 0, count);
	        }
			output.close();
			input.close();
			System.out.println("done");
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void rename() {

		String fileTile = videoTitle + "【" + producerName.substring(0, producerName.length() - 3) + "】";

		File dir = new File(downloadDir);
		String[] fileList = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("nicovideo");
			}
		});

		if (fileList.length == 0) {
			System.out.println("video file is not found");
		} else {
			System.out.println("file found, rename to");
			File video = new File(downloadDir  + downloadSubDir + "\\" + fileList[0]);
			video.renameTo(new File(downloadDir + downloadSubDir + "\\" + fileTile + ".mp4"));
		}
		System.out.println(fileTile + ".mp4, done!");

	}

	


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
