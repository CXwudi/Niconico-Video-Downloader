import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Safely {

	public static void loadWebPage(WebDriver driver, String URL) {
		while (true) {
			// driver.findElements
			try {
				driver.get(URL);
				System.out.println("load website " + URL + " success");
				Thread.sleep(300);
				break;
			} catch (TimeoutException | InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("load website timeout:( ");
				System.out.println("don't worry, CXwudi and miku are going to refrash the webpage and make it work!!\"");
			}
		}
	}
	
	public static WebElement findElement(WebDriver driver, By by) {
		while (true) {
			try {
				WebElement element =  driver.findElement(by);
				Thread.sleep(150);
				return element;
			} catch (TimeoutException | InterruptedException e) {
				System.err.println("find element timeout:( ");
			}
		}
		
	}
	public static List<WebElement> findElements(WebDriver driver, By by) {
		while (true) {
			try {
				List<WebElement> elementList =  driver.findElements(by);
				Thread.sleep(150);
				return elementList;
				
			} catch (TimeoutException | InterruptedException e) {
				
			}
		}
	}
	
	public static WebElement findElement(WebElement webElement, By by) {
		while (true) {
			try {
				WebElement element =  webElement.findElement(by);
				Thread.sleep(150);
				return element;
			} catch (TimeoutException | InterruptedException e) {
				System.err.println("find element timeout:( ");
			}
		}
		
	}
	public static List<WebElement> findElements(WebElement webElement, By by) {
		while (true) {
			try {
				List<WebElement> elementList =  webElement.findElements(by);
				Thread.sleep(150);
				return elementList;
				
			} catch (TimeoutException | InterruptedException e) {
				
			}
		}
	}
}
