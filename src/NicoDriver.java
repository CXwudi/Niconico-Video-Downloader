import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

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
public class NicoDriver implements WebDriver{
	
	private ChromeDriver chromeDriver;

	public NicoDriver() {
		chromeDriver = new ChromeDriver();
		setupDriver();
	}

	public NicoDriver(ChromeDriverService service) {
		chromeDriver = new ChromeDriver(service);
		setupDriver();
	}

	public NicoDriver(ChromeOptions options) {
		chromeDriver = new ChromeDriver(options);
		setupDriver();
	}

	public NicoDriver(ChromeDriverService service, ChromeOptions options) {
		chromeDriver = new ChromeDriver(service, options);
		setupDriver();
	}

	private void setupDriver() {
        chromeDriver.manage().deleteAllCookies();
        //chromeDriver.manage().window().maximize();
        //synchronization between this application and the website pages, so that my codes can wait for the web elements to come up, then do the work.
        chromeDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        chromeDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
    }
	
	/**
	 * @return the real chromeDriver inside the wrapper class
	 */
	public ChromeDriver getChromeDriver() {
		return chromeDriver;
	}

	/**
	 * @param chromeDriver the chromeDriver to set
	 * @Warnning this setter doesn't delete and quit the old chromeDriver, better do {@code getDriver().quit()}
	 * before using {@code setDriver()}.
	 */
	public void setChromeDriver(ChromeDriver driver) {
		this.chromeDriver = driver;
	}

	/**
	 * Simply just close the browser and reopen a new one.
	 */
	public void resetDriver() {
		quit();
		chromeDriver = new ChromeDriver();
		setupDriver();
	}

	public void resetDriver(ChromeDriverService service) {
		quit();
		chromeDriver = new ChromeDriver(service);
		setupDriver();
	}

	public void resetDriver(ChromeOptions options) {
		quit();
		chromeDriver = new ChromeDriver(options);
		setupDriver();
	}

	public void resetDriver(ChromeDriverService service, ChromeOptions options) {
		quit();
		chromeDriver = new ChromeDriver(service, options);
		setupDriver();
	}
	@Override
	public void get(String url) {
		
		while (true) {
			try {
				chromeDriver.get(url);
				System.out.println("load website " + url + " success");
				Thread.sleep(100 + new Random().nextInt(400));
				return;
			} catch (TimeoutException e) {
				System.err.println("load website " + url + " timeout:( ");
				System.out.println("don't worry, CXwudi and miku are going to refrash the webpage and make it work!!\"");
			} catch (InterruptedException e) {
				System.err.println(e + "\nthis shouldn't happen");
			}
		}
	}
	
	public void originGet(String url) {
		chromeDriver.get(url);
	}
	
	@Override
	public String getCurrentUrl() {
		return chromeDriver.getCurrentUrl();
	}

	@Override
	public String getTitle() {
		return chromeDriver.getTitle();
	}
	
	@Override
	public WebElement findElement(By by) {
		int i = 0;
		while (true) {
			try {
				return chromeDriver.findElement(by);
			} catch (TimeoutException e) {
				System.err.println("find element timeout:( ");
				if (i++ < 2) System.out.println("don't worry, CXwudi and miku are going to try again and make it work!!");
				else {
					System.err.println("Oh NO, we really failed :(");
					throw e;
				}
				try {
					Thread.sleep(100 + new Random().nextInt(100));
				} catch (InterruptedException e1) {
					System.err.println(e + "\nthis shouldn't happen");
				}

			} catch (NoSuchElementException e) {
				System.err.println("Oh NO, we can't find this element, (maybe) get perpared for NullPointerException 😂");
				return null;
			}
		} 

	}

	@Override
	public List<WebElement> findElements(By by) {
		int i = 0;
		while (true) {
			try {
				return chromeDriver.findElements(by);
			} catch (TimeoutException e) {
				System.err.println("find elements" + by.toString() + " timeout:( ");
				if (i++ < 2) System.out.println("don't worry, CXwudi and miku are going to try again and make it work!!");
				else {
					System.err.println("Oh NO, we really failed :(");
					throw e;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					System.err.println(e + "\nthis shouldn't happen");
				}

			}
		}
	}
	
	@Override
	public void quit() {
	    try {
			chromeDriver.manage().deleteAllCookies();
		} catch (Exception e) {
			System.err.println("fail to delete all cookies before quiting brower");
			e.printStackTrace();
		}
	    chromeDriver.quit();
	    chromeDriver = null;
	}
	
	@Override
	public String getPageSource() {
		return chromeDriver.getPageSource();
	}

	@Override
	public void close() {
		chromeDriver.close();
	}

	@Override
	public Set<String> getWindowHandles() {
		return chromeDriver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return chromeDriver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		return chromeDriver.switchTo();
	}

	@Override
	public Navigation navigate() {
		return chromeDriver.navigate();
	}

	@Override
	public Options manage() {
		return chromeDriver.manage();
	}
}
