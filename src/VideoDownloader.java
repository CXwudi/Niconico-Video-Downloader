import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class VideoDownloader {
	WebDriver driver;
	String downloadDir = "D:\\11134\\Download\\Video";
	String email = "";
	String password = "";
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

	public void openChrome() {
		System.setProperty("webdriver.chrome.driver", "C:\\ChromeAuto\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
	}

	public void login() {
		driver.get("https://account.nicovideo.jp/login");
		driver.findElement(By.id("input__mailtel")).sendKeys(email);
		WebElement ps = driver.findElement(By.id("input__password"));
		ps.sendKeys(password);
		ps.submit();
	}

	public void setupNicoNico() throws Exception {

		if (!driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			throw new Exception("nicovideo main website does not open after login");
		}
		driver.findElement(By.id("areaTrigger")).click();
		driver.findElement(By.cssSelector("a.selectType.JP")).click();
		if (!driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			throw new Exception("nicovideo main website does not open after region selection");
		}
		driver.findElement(By.id("langTrigger")).click();
		driver.findElement(By.cssSelector("a.selectType.ja-jp")).click();
	}

	public void getVideoInfo() {
		videoURL = "";
		int repeated = 0;
		while (videoURL.equals("") || videoURL == null) {
			// driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			try {
				driver.get("http://www.nicovideo.jp/watch/" + SMnumber);
				System.out.println("website opened");
				videoURL = driver.findElement(By.id("MainVideoPlayer")).findElement(By.cssSelector("video")).getAttribute("src");
				System.out.println("url reached: " + videoURL);
				videoTitle = driver.findElement(By.cssSelector("h1.VideoTitle")).getText();
				System.out.println(videoTitle);
				producerName = driver.findElement(By.cssSelector("a.Link.VideoOwnerInfo-pageLink")).getAttribute("title");
				System.out.println(producerName);
				// WebElement player = driver.findElement(By.id("MainVideoPlayer"));
				// System.out.println(player);
				// WebElement video = player.findElement(By.cssSelector("video"));
				// System.out.println(video);
				// videoURL = video.getAttribute("src");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(new TimeoutException("don't worry, CXwudi and miku are going to refrash the webpage and make it work!!"));
			}
			if (repeated++ >= 5) {
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		System.out.println("SEE!! CXwudi and miku get it, here we are. URL = " + videoURL);
		System.out.println("title = " + videoTitle);
		System.out.println("producer = " + producerName);

	}

	public void downloadVideo() {
		try {
			Process downloadingProcess = Runtime.getRuntime().exec("cmd /c C:\\ChromeAuto\\wget64.exe -P " + downloadDir + " --no-check-certificate -nv " + videoURL);
			System.out.println("start downloading");
			downloadingProcess.waitFor();
			System.out.println("done");
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
	}

	public void rename() {

		String fileTile = videoTitle + "【" + producerName.substring(0, producerName.length() - 3) + "】";
		
		File dir = new File(downloadDir);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("nicovideo");
			}
		};
		String[] fileList = dir.list(filter);
		if (fileList.length == 0) {
			System.out.println("video file is not found");
		} else {
			System.out.println("file found, rename to");
			File video = new File(downloadDir + "\\" + fileList[0]);
			video.renameTo(new File(downloadDir + "\\" + fileTile + ".mp4"));
		}
		System.out.println(fileTile + ".mp4, done!");
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		VideoDownloader aMain = new VideoDownloader();
		aMain.openChrome();
		aMain.login();
		aMain.setupNicoNico();
		aMain.getVideoInfo();
		aMain.downloadVideo();
		aMain.rename();
	}

}
