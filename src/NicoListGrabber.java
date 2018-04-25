import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


public class NicoListGrabber extends CollectionReader{
	private NicoDriver driver;

	
	public NicoListGrabber(NicoDriver d) {
		super();
		driver = d;
	}
	
	@Override
	public boolean readRecord() {
		HashMap<String, String> mylists = getMyListsIdAndName();
		for (Iterator<Entry<String, String>> iterator = mylists.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> list = iterator.next();
			collection.addAll(getOneFolderCollection(list.getKey(), list.getValue()));
		}
		isDone = true;
		return isDone;
	}
	
	private HashMap<String, String> getMyListsIdAndName() {
		try {
			HashMap<String, String> myLists = new HashMap<>();
			driver.get("http://www.nicovideo.jp/my/mylist/");

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
			e.printStackTrace();
			System.out.println("CXwudi and Miku failed to get collections info, we are trying again");
			return getMyListsIdAndName();
		}
	}

	private TreeSet<Vsong> getOneFolderCollection(String id, String folderName) {
		TreeSet<Vsong> folder = new TreeSet<>();
		try {
			
			driver.get("http://www.nicovideo.jp/my/mylist/#/" + id);
			WebElement sort = driver.findElement(By.cssSelector("select.itemSort[name=sort]"));
			sort.click();
			Thread.sleep(10);
			sort.findElement(By.cssSelector("[value='1']")).click();
			System.out.println("change sort order success");
			Thread.sleep(700);
	
			System.out.println("start fetching lists");
			List<WebElement> myFavorMusics = driver.findElements(By.cssSelector("li[id^=SYS_box_item_0_]"));
	
			
			for (WebElement webElement : myFavorMusics) {
				WebElement description = webElement.findElement(By.cssSelector("a[href^='/watch/']"));
				String videoLink = description.getAttribute("href");// it gives us url like: http://www.nicovideo.jp/watch/sm31818521
				int smId = Integer.parseInt(videoLink.substring(videoLink.indexOf("sm") + 2, videoLink.length()));
				String title = description.getText();// it gives us string like: ハチ MV「砂の惑星 feat.初音ミク」
				folder.add(new Vsong(smId, title, folderName));
			}
			System.out.println("Collection \"" + folderName + "\" has following songs: " + folder);
			
		} catch (StaleElementReferenceException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("CXwudi and Miku failed to get list info, we are trying again");
			return getOneFolderCollection(id, folderName);
		} catch (InterruptedException e) {
			System.err.println(e + "\nthis shouldn't happen");
		}
		return folder;
	}

}
