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
	
	String email = "";
	String password = "";
	
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
		
	}
	
	public void login() {
		MainModel.loadWebPage(driver, "https://account.nicovideo.jp/login");
		driver.findElement(By.id("input__mailtel")).sendKeys(email);
		WebElement ps = driver.findElement(By.id("input__password"));
		ps.sendKeys(password);
		ps.submit();

		if (driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) System.out.println("login success");
		else System.out.println("login fail");
	}
	
	public void setupNicoNico() {

		// driver.findElements
		try {
			driver.findElement(By.id("areaTrigger")).click();
			driver.findElement(By.cssSelector("a.selectType.JP")).click();
			System.out.println("change region success");

		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			System.out.println("change region fail");
			driver.navigate().refresh();
		}

		try {
			driver.findElement(By.id("langTrigger")).click();
			driver.findElement(By.cssSelector("a.selectType.ja-jp")).click();
			System.out.println("change language success");

		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			System.out.println("change language fail");
			driver.navigate().refresh();
		}

	}
	
	
	
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
				System.out.println("load website timeout:( \ndon't worry, CXwudi and miku are going to refrash the webpage and make it work!!");
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		main.login();
		
		
	}

}
