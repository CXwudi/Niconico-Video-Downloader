import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
	WebDriver driver;
	
	public void openChrome() {
		System.setProperty("webdriver.chrome.driver", "D:\\Program Files\\chromedriver.exe");
		driver = new ChromeDriver();
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main myMainChrome = new Main();
		myMainChrome.openChrome();
	}

}
