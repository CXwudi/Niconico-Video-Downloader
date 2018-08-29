
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * the main model of Nico video downloader project, also is the main M part of MVC
 * it contains 4 small model pieces---Setup, Downloader, OldListGrabber and LocalReader
 * @author CX无敌
 */
public class MainModel {
	
	private NicoDriver driver;
	private TaskManager taskManager;
	private DownloadManager downloadManager;
	
	private String email = "1113421658@qq.com";
	private String password = "2010017980502";
	
	private TreeSet<Vsong> task, done;

	public MainModel() {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
		driver = new NicoDriver();
		
		
		//WARNING don't write done = task = new TreeSet<>(); 
		//this gonna make two pointers point to the same one TreeSet, which is bad.
		done = new TreeSet<>();
		task = new TreeSet<>();
		
		taskManager = new TaskManager(driver, task, done);
		downloadManager = new DownloadManager(driver, task, done);
		

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
			System.err.println("change region may fail");
			driver.navigate().refresh();
			isSuccess = false;
		}
		
		//TODO: some new changes are added here, need to done
		try {
			WebElement lanElement = driver.findElement(By.cssSelector("span.CountrySelector-item.CountrySelector-currentItem[data-value='en-us']"));
			lanElement.click();
			Thread.sleep(50);
			driver.findElement(By.cssSelector("li.CountrySelector-item[data-type='language'][data-value='ja-jp']")).click();
			System.out.println("change language success");

		} catch (TimeoutException | InterruptedException e) {
			System.err.println("change language may fail");
			driver.navigate().refresh();
			isSuccess = false;
		}
		return isSuccess;

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
	 * @return the downloadManager
	 */
	public DownloadManager downloadManager() {
		return downloadManager;
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
	 * @return the done
	 */
	public TreeSet<Vsong> getUpdate() {
		return done;
	}

	/**
	 * @param done the done to set
	 */
	public void setUpdate(TreeSet<Vsong> update) {
		this.done = update;
	}

	public static void main(String[] args) {
		MainModel main = new MainModel();
		main.driver().setupDriver();
		main.login();
		main.setupNicoNico();
		main.taskManager().readRecord();
		main.taskManager().getTaskAndUpdate();
		main.downloadManager().downloadVocaloidPVs();
	   /* ChromeDriver d = new ChromeDriver();
	    d.manage().deleteAllCookies();
        d.manage().window().maximize();
        //synchronization between this application and the website pages, so that my codes can wait for the web elements to come up, then do the work.
        d.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        d.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);*/
	}

}
