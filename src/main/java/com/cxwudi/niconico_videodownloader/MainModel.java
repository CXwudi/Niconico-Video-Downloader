package com.cxwudi.niconico_videodownloader;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.TreeSet;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cxwudi.niconico_videodownloader.entity.NicoDriver;
import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.get_tasks.TasksDecider;
import com.cxwudi.niconico_videodownloader.solve_tasks.TasksSolver;

/**
 * the main model of Nico video downloader project, Manage the NicoDriver,
 * it also is the main M part of MVC, contains 2 subsections---{@link TasksDecider}, and {@link TasksSolver}
 * @author CX无敌
 */
public class MainModel {
	
	private NicoDriver driver;
	private TasksDecider tasksDecider;
	private TasksSolver tasksSolver;
	
	private Set<Vsong> task, done;

	public MainModel() {
		ChromeOptions co = new ChromeOptions();
		co.addArguments("--mute-audio");
		//co.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		driver  = new NicoDriver(new ChromeDriver(co));
		//WARNING don't write done = task = new TreeSet<>(); 
		//this gonna make two pointers point to the same one TreeSet, which is bad.
		done = new TreeSet<>();
		task = new TreeSet<>();
		
		tasksDecider = new TasksDecider(driver, task, done);
		tasksSolver = new TasksSolver(driver, task, done);
	}
	

	public boolean login(String email, String password) {
		driver.get("https://account.nicovideo.jp/login");
		driver.findElement(By.id("input__mailtel")).sendKeys(email);
		WebElement ps = driver.findElement(By.id("input__password"));
		ps.sendKeys(password);
		ps.submit();
		if (driver.getCurrentUrl().contains("www.nicovideo.jp")) {
			logger.info("login success");
			return true;
		} else {
			logger.warn("login fail");
			return false;
		}
	}

	public boolean setupNicoNico() {
		boolean isSuccess = false;
		try {
			if (!driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
				driver.get("http://www.nicovideo.jp/");
			}
		} catch (TimeoutException e) {
			logger.error("get current url may fail");
		}
		
		//change area, no longer available after 2019 April
//		try {
//			((JavascriptExecutor) driver.getChromeDriver()).executeScript("return window.stop");
//			var areaElement = driver.findElement(By.id("areaTrigger"));
//			//if element exists, mean we are currently in US or Taiwan city, since they are using old niconico web page.
//			if (areaElement != null) {
//				areaElement.click();
//				Thread.sleep(50);
//				driver.findElement(By.cssSelector("a.selectType.JP")).click();
//				logger.info("change region success");
//			} else {
//				logger.info("already in Japan region");
//			}
//		} catch (TimeoutException | InterruptedException e) {
//			logger.error("change region may fail");
//			isSuccess = false;
//		}
		
		//change language under Japan region.
		try {
			((JavascriptExecutor) driver.getRealDriver()).executeScript("return window.stop");
			var lanElement = driver.findElement(By.cssSelector("span.CountrySelector-item.CountrySelector-currentItem[data-value='en-us']"));
			if (lanElement == null) lanElement = driver.findElement(By.cssSelector("span.CountrySelector-item.CountrySelector-currentItem[data-value='zh-tw']"));
			
			//if element exists, means we are in either English or Chinese language, change it to Japanese
			if (lanElement != null) {
				lanElement.click();
				Thread.sleep(50);
				driver.findElement(By.cssSelector("li.CountrySelector-item[data-type='language'][data-value='ja-jp']")).click();
				logger.info("change language success");
			} else {
				logger.info("already in Japanese");
			}
			isSuccess = true;
		} catch (TimeoutException | InterruptedException e) {
			logger.warn("change language may fail");
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
	 * @return the tasksDecider
	 */
	public TasksDecider tasksDecider() {
		return tasksDecider;
	}

	/**
	 * @return the tasksSolver
	 */
	public TasksSolver tasksSolver() {
		return tasksSolver;
	}

	/**
	 * @return the task
	 */
	public Set<Vsong> getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(Set<Vsong> task) {
		this.task = task;
	}

	/**
	 * @return the done
	 */
	public Set<Vsong> getUpdate() {
		return done;
	}

	/**
	 * @param done the done to set
	 */
	public void setUpdate(Set<Vsong> update) {
		this.done = update;
	}

	public static void main(String[] args) {
		Main.main(args);
	}

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}
