package com.cxwudi.niconico_videodownloader.get_tasks;

import com.cxwudi.niconico_videodownloader.entity.NicoDriver;
import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.setup.Config;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
/**
 * the collection reader that control Chrome browser to navigate Niconico douga mylist page,
 *  and read my Vocaloid collection from it
 * @see CollectionReader
 * @author CX无敌
 *
 */
public class NicoListGrabber extends CollectionReader{
	private NicoDriver driver;

	
	public NicoListGrabber(NicoDriver d) {
		super();
		driver = d;
	}

	public void setDriver(NicoDriver driver) {
		this.driver = driver;
	}

	@Override
	public void readRecord() {
		HashMap<String, String> mylists = getMyListsIdAndName();
		for (Iterator<Entry<String, String>> iterator = mylists.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> list = iterator.next();
			collection.addAll(getOneFolderCollection(list.getKey(), list.getValue()));
		}
		isDone = true;
	}
	
	private HashMap<String, String> getMyListsIdAndName() {
		try {
			HashMap<String, String> myLists = new HashMap<>();
			driver.get("http://www.nicovideo.jp/my/mylist/");
			driver.navigate().refresh();

			WebElement myListContainer = driver.findElement(By.cssSelector("div.navInner"));
			List<WebElement> searchResults = myListContainer.findElements(By.cssSelector("li[id^=SYS_box_group_]"));

			// ex: SYS_box_group_57925968
			for (WebElement webElement : searchResults) {

				String id = webElement.getAttribute("id");
				logger.info("List id found: {}", id);
				String folderName = webElement.findElement(By.cssSelector("span")).getText();
				if (Config.getIncludedListContainStrings().stream().anyMatch(folderName::contains) &&
						Config.getExcludedListContainStrings().stream().noneMatch(folderName::contains)) {
					myLists.put(id.substring(id.lastIndexOf('_') + 1), folderName);
				}
				
			}
			logger.info("{}", myLists);
			return myLists;
		} catch (TimeoutException | StaleElementReferenceException e) {
			e.printStackTrace();
			logger.info("CXwudi and Miku failed to get collections info, we are trying again");
			return getMyListsIdAndName();
		} catch (UnhandledAlertException e) {
			//there was a time where an alert window was pop up and broke the code,
			//so this catch statement is applied to handle this
			driver.switchTo().alert().accept();
			return getMyListsIdAndName();
		}
	}

	private TreeSet<Vsong> getOneFolderCollection(String id, String folderName) {
		TreeSet<Vsong> folder = new TreeSet<>();
		try {
			
			driver.get("http://www.nicovideo.jp/my/mylist/#/" + id);
			//without this refrash, we'll get StaleElementReferenceException
			//this is because simply changing the list id on browser url box doesn't load a new page, but changing the content on current page
			driver.navigate().refresh();
			Thread.sleep(300);
			WebElement sort = driver.findElement(By.cssSelector("select.itemSort[name=sort]"));
			if (sort == null) {//means this folder is empty.
				logger.info("This folder " + folderName + " is currently empty ╮(╯▽╰)╭");
				return folder;
			}
			sort.click();
			sort.findElement(By.cssSelector("[value='1']")).click();
			logger.info("change sort order success");
			Thread.sleep(700);
	
			logger.info("start fetching lists");
			
			do {
				List<WebElement> myFavorMusics = driver.findElements(By.cssSelector("li[id^=SYS_box_item_0_]"));
				for (WebElement webElement : myFavorMusics) {
					WebElement description = webElement.findElement(By.cssSelector("a[href^='/watch/']"));
					String videoLink = description.getAttribute("href");// it gives us url like: http://www.nicovideo.jp/watch/sm31818521
					String Id = videoLink.substring(videoLink.lastIndexOf("/") + 1, videoLink.length());
					String title = description.getText();// it gives us string like: ハチ MV「砂の惑星 feat.初音ミク」
					folder.add(new Vsong(Id, title, folderName));
				}
			} while (hasNextPage());
			
			logger.info("Collection \"" + folderName + "\" has following songs: \n{}", folder);
			
		} catch (StaleElementReferenceException | TimeoutException e) {
			logger.error("CXwudi and Miku failed to get list info due to web server problem, we are trying again", e);
			return getOneFolderCollection(id, folderName);
		} catch (InterruptedException e) {
			logger.error("this shouldn't happen", e);
		} catch (UnhandledAlertException e) {
			//there was a time where an alert window was pop up and broke the code,
			//so this catch statement is applied to handle this
			logger.error("A popup stops CXwudi and Miku to get list info, we are trying again", e);
			driver.switchTo().alert().accept();
			return getOneFolderCollection(id, folderName);
		}
		return folder;
	}

	/**
	 * check if there is more than one page of this folder, if yes, navigate to next page.
	 * @return
	 */
	private boolean hasNextPage() {
		WebElement nextButton = driver.findElement(By.cssSelector("a.SYS_btn_pager_next"));
		if (nextButton == null) return false;
		else {
			nextButton.click();
			logger.info("next page");
			driver.navigate().refresh();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				logger.error("this should not happen in NicoListGrabber.hasNextPage()", e);
			}
			return true;
		}
		
	}

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}
