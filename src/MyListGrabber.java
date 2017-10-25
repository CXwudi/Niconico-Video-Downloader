import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MyListGrabber {
	WebDriver driver;
	
	HashMap<String, String> myLists;
	ArrayList<String> SMlists;
	HashSet<String> listDownloaded;
	
	public MyListGrabber(WebDriver d) {
		// TODO Auto-generated constructor stub
		driver = d;
		
		myLists = new HashMap<>();
		SMlists = new ArrayList<>();
		listDownloaded = new HashSet<>();
	}
	
	public void getMyList() {
		MainModel.loadWebPage(driver, "http://www.nicovideo.jp/my/mylist/");

		WebElement myListContainer = driver.findElement(By.cssSelector("div.navInner"));
		List<WebElement> searchResults = myListContainer.findElements(By.cssSelector("li[id^=SYS_box_group_]"));

		// ex: SYS_box_group_57925968
		for (WebElement webElement : searchResults) {

			String id = webElement.getAttribute("id");
			System.out.println("List id found: " + id);
			myLists.put(id.substring(id.lastIndexOf("_") + 1, id.length()), webElement.findElement(By.cssSelector("span")).getText());

		}
		System.out.println(myLists);

	}

	public void fetchSMlists(String listNumber) {
		MainModel.loadWebPage(driver, "http://www.nicovideo.jp/my/mylist/#/" + listNumber);

		WebElement sort = driver.findElement(By.cssSelector("select.itemSort[name=sort]"));
		sort.click();

		// driver.findElement(By.xpath("//*[@id=\"myContBody\"]/div[4]/div/div[1]/form[1]/select/option[1]")).click();
		sort.findElement(By.cssSelector("[value='1']")).click();
		System.out.println("change sort order");
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
		}
		List<WebElement> myFavorMusics = driver.findElements(By.cssSelector("li[id^=SYS_box_item_0_]"));
		SMlists.clear();
		for (WebElement webElement : myFavorMusics) {
			String sm = webElement.findElement(By.cssSelector("a[href^=watch]")).getAttribute("href");
			SMlists.add(sm.substring(sm.lastIndexOf("/") + 1, sm.length()));
		}
		System.out.println(SMlists);
	}
}
