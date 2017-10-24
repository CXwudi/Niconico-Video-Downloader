import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
	WebDriver driver;
	String email = "";
	String password = "";
	String SMnumber = "sm31832794";
	//learn it by heart, there are 8 elements that can be searched by findElement() method.
	//id, name, classname, tagname, cssSelector,
	//linkText, partialLinkText, xpath
	
	public void openChrome() {
		System.setProperty("webdriver.chrome.driver","C:\\ChromeAuto\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
	}
	
	public void login() {
		driver.get("https://account.nicovideo.jp/login");
		driver.findElement(By.id("input__mailtel")).sendKeys(email);
		WebElement ps = driver.findElement(By.id("input__password"));
		ps.sendKeys(password);
		ps.submit();
	}
	
	public void setupNicoNico() throws Exception {
		
		
		if (!driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			throw new Exception("nicovideo main website does not open after login");
		} 
		driver.findElement(By.id("areaTrigger")).click();
		driver.findElement(By.cssSelector("a.selectType.JP")).click();
		if (!driver.getCurrentUrl().equals("http://www.nicovideo.jp/")) {
			throw new Exception("nicovideo main website does not open after region selection");
		} 
		driver.findElement(By.id("langTrigger")).click();
		driver.findElement(By.cssSelector("a.selectType.ja-jp")).click();	
	}
	
	public void downloadVideo() {
		String videoURL = "";
		
		while (videoURL.equals("") || videoURL == null) {
			//driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
			try {
				driver.get("http://www.nicovideo.jp/watch/" + SMnumber);
				System.out.println("website opened");
				videoURL = driver.findElement(By.id("MainVideoPlayer")).findElement(By.cssSelector("video")).getAttribute("src");
				System.out.println("url reached");
				//WebElement player = driver.findElement(By.id("MainVideoPlayer"));
				//System.out.println(player);
				//WebElement video = player.findElement(By.cssSelector("video"));
				//System.out.println(video);
				//videoURL = video.getAttribute("src");
				//System.out.println(videoURL);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(new TimeoutException("don't worry, CXwudi and miku are going to refrash the webpage and make it work!!"));
			} 
		}
		System.out.println("SEE!! CXwudi and miku get it, here we are. URL = " + videoURL);
		
		try {
			Process downloadingProcess = Runtime.getRuntime().exec("cmd /c C:\\ChromeAuto\\wget64.exe -P D:\\11134\\Download\\Video --no-check-certificate " + videoURL);
			downloadingProcess.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		
		
	}
	
	public void rename() {
		
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Main aMain = new Main();
		aMain.openChrome();
		aMain.login();
		aMain.setupNicoNico();
		aMain.downloadVideo();
	}

}
