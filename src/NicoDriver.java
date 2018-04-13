import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * An specific ChromeDriver that is optimized for Niconico video website. More specifically, optimized for nicovideo.jp
 * Since the server of nicovideo.jp is in Japan region which is far from North America, 
 * so there is a high chance that loading a website or elements from nicovideo.jp can throw TimeoutException due to the far location network transaction
 * In order to deal with these random and unexpective TimeoutException,
 * I override some functions from ChromeDriver so that the method would try again after an TimeoutException is thrown. 
 * However, this implementation can not guarantee to solve the timeout problem entirely. 
 * We still need to write try-catch block and re-do function outside these NicoDriver class.
 * @author CX无敌 
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

	@Override
	public void get(String url) {
		// TODO Auto-generated method stub

		while (true) {
			// driver.findElements
			try {
				super.get(url);
				System.out.println("load website " + url + " success");
				Thread.sleep(200);
				break;
			} catch (TimeoutException e) {
				System.err.println("load website " + url + " timeout:( ");
				System.out.println("don't worry, CXwudi and miku are going to refrash the webpage and make it work!!\"");
			} catch (InterruptedException e) {
				System.err.println(e + "\nthis shouldn't happen");
			}
		}
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

}
