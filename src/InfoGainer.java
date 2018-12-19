import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
/**
 * Grab information about this song from niconico webpage
 * @author CX无敌
 *
 */
public class InfoGainer {
	private NicoDriver driver;

	public InfoGainer(NicoDriver d) {
		this.driver = d;
	}
	
	public void setDriver(NicoDriver driver) {
		this.driver = driver;
	}

	public void fetchInfo(Vsong song) {
		String videoURL = "";
		String producerName = "";
		String videoTitle = "";
		List<String> tags = new ArrayList<>();

		try {
			//driver.get("http://www.nicovideo.jp/");//give program a break, to see broken video or not
			driver.get("http://www.nicovideo.jp/watch/" + song.getId());
			//driver.navigate().refresh();
			System.out.println("website opened");
			Thread.sleep(700 + new Random().nextInt(300));
			if (driver.findElement(By.cssSelector("p.messageTitle")) != null) {
    			System.err.println("fake video, CXwudi and Miku are very ANGRY and wanna exits :(");
    			return;
    		}
			
			
			videoTitle = driver.findElement(By.cssSelector("h1")).getText();
			
			videoURL = driver.findElement(By.id("MainVideoPlayer")).findElement(By.cssSelector("video")).getAttribute("src");
			System.out.println("url reached: " + videoURL);
			if (!song.getId().contains("so")) {
				producerName = driver.findElement(By.cssSelector("a.Link.VideoOwnerInfo-pageLink")).getAttribute("title");
				producerName = producerName.substring(0, producerName.length() - 3);
			} else {
				//TODO: so-id has different webElement contains producer names, fix to later
			}
			List<WebElement> tagElements = driver.findElements(By.cssSelector("a.Link.TagItem-name"));
			for (WebElement tagElement : tagElements) {
				tags.add(tagElement.getText());
			}
			System.out.println("the tags are: " + tags);
			//driver.findElement(By.cssSelector("div.ControllerButton-inner")).click();//start the video, fake the server
		} catch (TimeoutException e) {
			e.printStackTrace();
			System.out.println("CXwudi and Miku failed to get video info, we are trying again");
			fetchInfo(song);
		} catch (InterruptedException e) {
			System.err.println(e + "\nthis shouldn't happen");
		}
		if (videoURL.equals("") || videoURL == null || producerName.equals("") || tags.isEmpty() || videoTitle.equals("")) {
			System.out.println("CXwudi and Miku failed to get video info, we are trying again");
			fetchInfo(song);
		} else {
			song.setProducerName(producerName).setURL(videoURL).setTagsList(tags).setTitle(videoTitle);
			System.out.println("SEE!! CXwudi and miku get the URL, here is video info:\n" + song);
		}

		
	}

	public static void main(String[] args) {
		/*MainModel main = new MainModel(new NicoDriver());
		main.login();
		main.setupNicoNico();
		
		  Object ret = ((JavascriptExecutor)main.driver()).executeAsyncScript(
		  "var request = new XMLHttpRequest();" +
		  "request.onreadystatechange = function() {" +
		  "		if (request.readyState === 4) {" +
		  "			return request.status;" + "		}" + "};" +
		  "request.open(\"GET\", \"http://www.nicovideo.jp/watch/sm" + 123123123 +
		  "\", true);" + "request.send();" );
		 
		main.downloadManager().fetchInfo(new Vsong(123123123, ""));
		main.downloadManager().fetchInfo(new Vsong(32461412, ""));*/
	}

}
