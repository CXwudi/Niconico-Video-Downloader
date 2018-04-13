
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * the main model of Nico video downloader project, also is the main M part of MVC
 * it contains 4 small model pieces---Setup, Downloader, ListGrabber and TaskManager
 * @author CX无敌
 */
public class MainModel {
	NicoDriver driver;
	VideoDownloader downloader;
	MyListGrabber listGrabber;
	TaskManager taskManager;

	String email = "";
	String password = "";

	public MainModel() {
		
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");

		driver = new NicoDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		//synchronization between this application and the website pages, so that my codes can wait for the web elements to come up, then do the work.
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		downloader = new VideoDownloader(driver);
		listGrabber = new MyListGrabber(driver);
		taskManager = new TaskManager();

	}

	public boolean login() {
		Safely.loadWebPage(driver, "https://account.nicovideo.jp/login");
		driver.findElement(By.id("input__mailtel")).sendKeys(email);
		WebElement ps = driver.findElement(By.id("input__password"));
		ps.sendKeys(password);
		ps.submit();

		if (driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			System.out.println("login success");
			return true;
		} else {
			System.out.println("login fail");
			return false;
		}
	}
	// return true if both region change and language change are success.
	public boolean setupNicoNico() {
		boolean isSuccess = true;
		
		try {
			driver.findElement(By.id("areaTrigger")).click();
			Thread.sleep(50);
			driver.findElement(By.cssSelector("a.selectType.JP")).click();
			System.out.println("change region success");

		} catch (TimeoutException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.err.println("change region may fail");
			driver.navigate().refresh();
			isSuccess = false;
		}

		try {
			driver.findElement(By.id("langTrigger")).click();
			Thread.sleep(50);
			driver.findElement(By.cssSelector("a.selectType.ja-jp")).click();
			System.out.println("change language success");

		} catch (TimeoutException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.err.println("change language may fail");
			driver.navigate().refresh();
			isSuccess = false;
		}
		return isSuccess;

	}

	public void doTaskWhileUpdate(String subDir, TreeMap<String, String> toDoList) {
		for (Entry<String, String> sm : toDoList.entrySet()) {
			downloader.getVideoInfoFrom(sm.getKey());
			try {
				downloader.downloadVideoTo(subDir);
				taskManager.updateDownloadedList(sm.getKey(), sm.getValue());
			} catch (IOException e) {
				System.out.println(sm.getValue() + " is marked as undownload");
			}
		}
		
	}
	
	public void setAllDownloaded() {
		HashMap<String, String> hashMap = listGrabber.getMyList();
		for (Entry<String, String> list : hashMap.entrySet()) {
			TreeMap<String, String> List = listGrabber.fetchSMlists(list.getKey());
			taskManager.updateDownloadedList(List);
		}
	}

	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		main.login();
		main.setupNicoNico();
		main.setAllDownloaded();
	}

}
