
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * the model in MVC, it will contains many small model pieces.
 */
public class MainModel {
	WebDriver driver;
	VideoDownloader downloader;
	MyListGrabber listGrabber;
	TaskManager taskManager;

	String email = "1113421658@qq.com";
	String password = "2010017980502";

	HashSet<String> downloadDone;// record the video that just downloaded.

	public MainModel() {
		// TODO Auto-generated constructor stub
		System.setProperty("webdriver.chrome.driver", "C:\\ChromeAuto\\chromedriver.exe");

		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
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

	public void setupNicoNico() {

		// driver.findElements
		try {
			driver.findElement(By.id("areaTrigger")).click();
			Thread.sleep(50);
			driver.findElement(By.cssSelector("a.selectType.JP")).click();
			System.out.println("change region success");

		} catch (TimeoutException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.err.println("change region may fail");
			driver.navigate().refresh();
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
		}

	}

	public void doTask(String subDir, TreeMap<String, String> toDoList) {
		for (String sm : toDoList.keySet()) {
			downloader.getVideoInfoFrom(sm);
			downloader.downloadVideoTo(subDir);
		}
		
	}
	
	public void setAllDownloaded() {
		HashMap<String, String> hashMap = listGrabber.getMyList();
		for (Entry<String, String> list : hashMap.entrySet()) {
			TreeMap<String, String> toDoList = listGrabber.fetchSMlists(list.getKey());
			taskManager.updateDownloadedList(toDoList);
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
