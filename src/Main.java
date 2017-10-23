import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
	WebDriver driver;
	String email = "1113421658@qq.com";
	String password = "2010017980502";
	//learn it by heart, there are 8 elements that can be searched by findElement() method.
	//id, name, classname, tagname, cssSelector,
	//linkText, partialLinkText, xpath
	
	public void openChrome() {
		System.setProperty("webdriver.chrome.driver","D:\\Program Files\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
	}
	
	public void loginAndSetupNiconico() throws Exception {
		driver.get("https://account.nicovideo.jp/login");
		driver.findElement(By.id("input__mailtel")).sendKeys(email);
		WebElement ps = driver.findElement(By.id("input__password"));
		ps.sendKeys(password);
		ps.submit();
		
		if (driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			driver.findElement(By.id("areaTrigger")).click();
			driver.findElement(By.className("selectType JP")).click();
		} else
			throw new Exception("nicovideo main website does not open!");
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Main aMain = new Main();
		aMain.openChrome();
		aMain.loginAndSetupNiconico();
	}

}
