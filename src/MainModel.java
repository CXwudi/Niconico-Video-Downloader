
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import Old.OldListGrabber;


/**
 * the main model of Nico video downloader project, also is the main M part of MVC
 * it contains 4 small model pieces---Setup, Downloader, OldListGrabber and LocalRecorder
 * @author CX无敌
 */
public class MainModel {
	//TODO add private modifier
	
	private NicoDriver driver;
	private TaskManager taskManager;
	
	private LocalRecorder localRecorder;
	VideoDownloader downloader;
	OldListGrabber oldListGrabber;
	

	String email = "1113421658@qq.com";
	String password = "2010017980502";
	
	private TreeSet<Vsong> task, update;

	public MainModel() {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
		driver = new NicoDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		//synchronization between this application and the website pages, so that my codes can wait for the web elements to come up, then do the work.
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		
		//WARNING don't write update = task = new TreeSet<>(); this gonna make two pointers point to the same one TreeSet, which is bad.
		update = new TreeSet<>();
		task = new TreeSet<>();
		
		taskManager = new TaskManager(driver, task, update);
		downloader = new VideoDownloader(driver);
		oldListGrabber = new OldListGrabber(driver);
		localRecorder = new LocalRecorder();
		
		

	}

	public boolean login() {
		driver.get("https://account.nicovideo.jp/login");
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
				localRecorder.updateDownloadedList(sm.getKey(), sm.getValue());
			} catch (IOException e) {
				System.out.println(sm.getValue() + " is marked as undownload");
			}
		}
		
	}

	/**
	 * @return the driver
	 */
	public NicoDriver driver() {
		return driver;
	}

	/**
	 * @return the taskManager
	 */
	public TaskManager taskManager() {
		return taskManager;
	}

	/**
	 * @return the task
	 */
	public TreeSet<Vsong> getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(TreeSet<Vsong> task) {
		this.task = task;
	}

	/**
	 * @return the update
	 */
	public TreeSet<Vsong> getUpdate() {
		return update;
	}

	/**
	 * @param update the update to set
	 */
	public void setUpdate(TreeSet<Vsong> update) {
		this.update = update;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		main.login();
		main.setupNicoNico();
		main.taskManager().readRecord();
		main.taskManager().getTaskAndUpdate();
		
	}

}
