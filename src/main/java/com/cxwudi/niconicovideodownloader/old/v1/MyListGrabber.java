package com.cxwudi.niconicovideodownloader.old.v1;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MyListGrabber {
	WebDriver driver;
	
	HashMap<String, String> myLists; // list id = list name
	TreeMap<String, String> smNumberMap; // sm number = song title, for one single folder!
	
	public MyListGrabber(WebDriver d) {
		// TODO Auto-generated constructor stub
		driver = d;
		myLists = new HashMap<>();
		smNumberMap = new TreeMap<>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		
	}
	
	public HashMap<String, String> getMyList() {
		try {
			Safely.loadWebPage(driver, "http://www.nicovideo.jp/my/mylist/");
			
			WebElement myListContainer = driver.findElement(By.cssSelector("div.navInner"));
			List<WebElement> searchResults = myListContainer.findElements(By.cssSelector("li[id^=SYS_box_group_]"));

			// ex: SYS_box_group_57925968
			for (WebElement webElement : searchResults) {

				String id = webElement.getAttribute("id");
				System.out.println("List id found: " + id);
				myLists.put(id.substring(id.lastIndexOf("_") + 1, id.length()), webElement.findElement(By.cssSelector("span")).getText());

			}
			System.out.println(myLists);
			return myLists;
		} catch (TimeoutException | StaleElementReferenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("CXwudi and Miku failed to get collections info, we are trying again");
			return getMyList();
		}

	}

	public TreeMap<String, String> fetchSMlists(String listNumber) {
		try {
			Safely.loadWebPage(driver, "http://www.nicovideo.jp/my/mylist/#/" + listNumber);
			WebElement sort = driver.findElement(By.cssSelector("select.itemSort[name=sort]"));
			sort.click();
			Thread.sleep(10);
			sort.findElement(By.cssSelector("[value='1']")).click();
			System.out.println("change sort order success");
			Thread.sleep(700);

			System.out.println("start fetching lists");
			List<WebElement> myFavorMusics = driver.findElements(By.cssSelector("li[id^=SYS_box_item_0_]"));

			smNumberMap.clear();
			for (WebElement webElement : myFavorMusics) {
				WebElement description = webElement.findElement(By.cssSelector("a[href^='/watch/']"));
				String sm = description.getAttribute("href");// it gives us url like: http://www.nicovideo.jp/watch/sm31818521
				String smNumber = sm.substring(sm.indexOf("sm"), sm.length());
				String title = description.getText();// it gives us string like: ハチ MV「砂の惑星 feat.初音ミク」
				smNumberMap.put(smNumber, title);
			}
			System.out.println(smNumberMap);
			return smNumberMap;
		} catch (StaleElementReferenceException | InterruptedException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("CXwudi and Miku failed to get list info, we are trying again");
			return fetchSMlists(listNumber);
		}
		 
		
		
	}
}
