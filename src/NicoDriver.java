import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * An specific ChromeDriver that is optimized for Niconico video website. More specifically, optimized for nicovideo.jp
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
public class NicoDriver extends ChromeDriver {

	public NicoDriver() {
		super();
	}

	public NicoDriver(ChromeDriverService service) {
		super(service);
	}

	public NicoDriver(ChromeOptions options) {
		super(options);
	}

	public NicoDriver(ChromeDriverService service, ChromeOptions options) {
		super(service, options);
	}
	
	public void setupDriver() {
	    this.manage().deleteAllCookies();
	    this.manage().window().maximize();
        //synchronization between this application and the website pages, so that my codes can wait for the web elements to come up, then do the work.
	    this.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	    this.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
	}

	@Override
	public void get(String url) {
		
		while (true) {
			try {
				super.get(url);
				System.out.println("load website " + url + " success");
				Thread.sleep(200);
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
		super.get(url);
	}

	@Override
	public WebElement findElement(By by) {
		int i = 0;
		while (true) {
			try {
				return super.findElement(by);
			} catch (TimeoutException e) {
				System.err.println("find element" + by.toString() + " timeout:( ");
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
	public List<WebElement> findElements(By by) {
		int i = 0;
		while (true) {
			try {
				return super.findElements(by);
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
	    manage().deleteAllCookies();
	    super.quit();
	}

}
