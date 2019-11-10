package com.cxwudi.niconico_videodownloader.entity;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NicoDriver is a WebDriver that contains a ChromeDriver that is optimized for Niconico video website, nicovideo.jp
 * Since the server of nicovideo.jp is in Japan region which is far from North America, 
 * so there is a high chance that loading a website or elements from nicovideo.jp can throw TimeoutException due to the far location network transaction.
 * I override some functions from ChromeDriver so that the method would try again after an TimeoutException is thrown. 
 * However, this implementation can not guarantee to solve the timeout problem entirely. 
 * We still need to write try-catch block and re-do function outside these NicoDriver class.
 * @author CX无敌 
 */
/*
 * here is the note taken from Youtube Tutorial
 * selenium get command for Webdriver:
 * 	get("URL") visit website
 *  getTitle() get website title
 *  getPageSource() get source code of web page
 *  getCurrentUrl() 
 *  getWindowHandle() used for switching tab
 *  getWindowHandles() used for switching tab as well
 */
public class NicoDriver<D extends WebDriver> implements WebDriver{
	
	private D webDriver;

	public NicoDriver(D underlayingDriver) {
		webDriver = underlayingDriver;
		setupDriver();
	}
	
	private static final int IMPLICIT_WAIT = 20;
	private static final int PAGELOAD_TIMEOUT = 30;

	private void setupDriver() {
        webDriver.manage().deleteAllCookies();
        //webDriver.manage().window().maximize();
        //synchronization between this application and the website pages, so that my codes can wait for the web elements to come up, then do the work.
        webDriver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(PAGELOAD_TIMEOUT, TimeUnit.SECONDS);
    }
	
	/**
	 * @return the real webDriver inside the wrapper class
	 */
	public D getRealDriver() {
		return webDriver;
	}

	/**
	 * @param webDriver the webDriver to set
	 * @Warnning this setter doesn't delete and quit the old webDriver, better do {@code getDriver().quit()}
	 * before using {@code setDriver()}.
	 */
	public void setRealDriver(D driver) {
		this.webDriver = driver;
	}

	/**
	 * Simply just close the browser and reopen a new one.
	 */
	public void resetDriver(D driver) {
		quit();
		webDriver = driver;
		setupDriver();
	}

	@Override
	public void get(String url) {
		
		while (true) {
			try {
				webDriver.get(url);
				logger.info("load website " + url + " success");
				Thread.sleep(100L + new Random().nextInt(400));
				return;
			} catch (TimeoutException e) {
				logger.info("load website " + url + " timeout:( ");
				logger.info("don't worry, CXwudi and miku are going to refrash the webpage and make it work!!\"");
			} catch (InterruptedException e) {
				logger.error("{}\nthis shouldn't happen", e);
			}
		}
	}
	
	public void originGet(String url) {
		webDriver.get(url);
	}
	
	@Override
	public String getCurrentUrl() {
		return webDriver.getCurrentUrl();
	}

	@Override
	public String getTitle() {
		return webDriver.getTitle();
	}
	
	@Override
	public WebElement findElement(By by) {
		int i = 0;
		while (true) {
			try {
				return webDriver.findElement(by);
			} catch (TimeoutException e) {
				logger.info("find element " + by.toString() + " timeout:( ");
				if (i++ < 2) logger.info("don't worry, CXwudi and miku are going to try again and make it work!!");
				else {
					logger.info("Oh NO, we really failed :(");
					throw e;
				}
				try {
					Thread.sleep(100L + new Random().nextInt(100));
				} catch (InterruptedException e1) {
					logger.error("{}\nthis shouldn't happen", e);
				}

			} catch (NoSuchElementException e) {
				logger.warn("Oh NO, we can't find element " + by.toString() + " , (maybe) get perpared for NullPointerException 😂");
				return null;
			}
		} 

	}

	@Override
	public List<WebElement> findElements(By by) {
		int i = 0;
		while (true) {
			try {
				return webDriver.findElements(by);
			} catch (TimeoutException e) {
				logger.info("find elements" + by.toString() + " timeout:( ");
				if (i++ < 2) logger.info("don't worry, CXwudi and miku are going to try again and make it work!!");
				else {
					logger.info("Oh NO, we really failed :(");
					throw e;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					logger.error("{}\nthis shouldn't happen", e);
				}

			}
		}
	}
	
	public boolean containsElements(By by) {
		return !findElements(by).isEmpty();
	}
	
	@Override
	public void quit() {
	    try {
			webDriver.manage().deleteAllCookies();
		} catch (Exception e) {
			logger.info("fail to delete all cookies before quiting brower");
			e.printStackTrace();
		}
	    webDriver.quit();
	    webDriver = null;
	}
	
	@Override
	public String getPageSource() {
		return webDriver.getPageSource();
	}

	@Override
	public void close() {
		webDriver.close();
	}

	@Override
	public Set<String> getWindowHandles() {
		return webDriver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return webDriver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		return webDriver.switchTo();
	}

	@Override
	public Navigation navigate() {
		return webDriver.navigate();
	}

	@Override
	public Options manage() {
		return webDriver.manage();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

}
