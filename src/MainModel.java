
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

/**
 * the main model of Nico video downloader project, Manage the NicoDriver,
 * it also is the main M part of MVC, contains 2 subsections---{@link TaskManager}, and {@link DownloadManager}
 * @author CX无敌
 */
public class MainModel {
	
	private NicoDriver driver;
	private TaskManager taskManager;
	private DownloadManager downloadManager;
	
	private TreeSet<Vsong> task, done;

	public MainModel() {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
		ChromeOptions co = new ChromeOptions();
		co.addArguments("--mute-audio");
		//co.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		driver  = new NicoDriver(co);
		//WARNING don't write done = task = new TreeSet<>(); 
		//this gonna make two pointers point to the same one TreeSet, which is bad.
		done = new TreeSet<>();
		task = new TreeSet<>();
		
		taskManager = new TaskManager(driver, task, done);
		downloadManager = new DownloadManager(driver, task, done);
	}
	

	public boolean login(String email, String password) {
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

	public void setupNicoNico() {
		boolean isSuccess = true;
		if (!driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			driver.get("http://www.nicovideo.jp/");
		}
		
		//change area
		try {
			var areaElement = driver.findElement(By.id("areaTrigger"));
			//if element exists, mean we are currently in US or Taiwan city, since they are using old niconico web page.
			if (areaElement != null) {
				areaElement.click();
				Thread.sleep(50);
				driver.findElement(By.cssSelector("a.selectType.JP")).click();
				System.out.println("change region success");
			} else {
				System.out.println("already in Japan region");
			}
		} catch (TimeoutException | InterruptedException e) {
			System.err.println("change region may fail");
			/*((JavascriptExecutor) driver).executeScript("return window.stop");
			driver.navigate().refresh();
			*/
			isSuccess = false;
		}
		
		//change language under Japan region.
		try {
			var lanElement = driver.findElement(By.cssSelector("span.CountrySelector-item.CountrySelector-currentItem[data-value='en-us']"));
			if (lanElement == null) lanElement = driver.findElement(By.cssSelector("span.CountrySelector-item.CountrySelector-currentItem[data-value='zh-tw']"));
			((JavascriptExecutor) driver).executeScript("return window.stop");
			//if element exists, means we are in either English or Chinese language, change it to Japanese
			if (lanElement != null) {
				lanElement.click();
				Thread.sleep(50);
				driver.findElement(By.cssSelector("li.CountrySelector-item[data-type='language'][data-value='ja-jp']")).click();
				System.out.println("change language success");
			} else {
				System.out.println("already in Japanese");
			}
			

		} catch (TimeoutException | InterruptedException e) {
			System.err.println("change language may fail");
			//driver.navigate().refresh();
			isSuccess = false;
		}
		if (!isSuccess) {
            setupNicoNico();
        }

	}

	public void reset() {
		driver.quit();
		driver = new NicoDriver();
		taskManager.setDriver(driver);
		downloadManager.setDriver(driver);
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
		main.login("1113421658@qq.com", "2010017980502");
		main.setupNicoNico();
		main.taskManager().readRecord();
		main.taskManager().getTaskAndUpdate();
		
		main.downloadManager().forEachVsong(vsong -> {
			main.reset();
			main.setupNicoNico();
			DownloadManager manager = main.downloadManager();
			manager.fetchInfo(vsong);
			if (manager.downloadOneVocaloidPV(vsong))
				manager.markDone(vsong);
			manager.triggerRecord();//we add this line for now
			}
		);
	}

}
