package com.cxwudi.niconico_videodownloader.solve_tasks;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cxwudi.niconico_videodownloader.entity.NicoDriver;
import com.cxwudi.niconico_videodownloader.entity.Vsong;
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

	/**
	 * fulfill Vsong instance's other information including url, producer name, etc. by visiting niconico website.
	 * @param song the Vocaloid song to be filled.
	 * @return {@code true} iff the vsong obj has all info filled. 
	 */
	public boolean fetchInfo(Vsong song) {
		if (song == null) {
			logger.info("song is null, CXwudi and Miku just skip this one");
			return false;
		}
		String videoURL = "";
		String producerName = "";
		String videoTitle = "";
		List<String> tags = new ArrayList<>();

		try {
			driver.get("http://www.nicovideo.jp/watch/" + song.getId());
			//driver.navigate().refresh();
			logger.info("website opened");
			Thread.sleep(700 + new Random().nextInt(300));
			//some check
			if (song.getId().contains("nm")) {
				logger.info("nm-id video unsupported, not gonna grab info for" + song.getId());
				return true;
			}
			if (driver.containsElements(By.cssSelector("div.mb16p4"))) {
    			logger.info("fake webpage, CXwudi and Miku are very ANGRY and wanna exits :(");
    			return false;
    		}
			
			
			videoTitle = driver.findElement(By.cssSelector("h1.VideoTitle")).getText();
			logger.info("title: {}", videoTitle);
			
			videoURL = driver.findElement(By.id("MainVideoPlayer")).findElement(By.cssSelector("video")).getAttribute("src");
			logger.info("url reached: {}", videoURL);
			
			List<WebElement> producer = driver.findElements(By.cssSelector("a.Link.VideoOwnerInfo-pageLink"));
			if (producer.isEmpty()) { //that means it's a non-sm id, like nm-id or pure number id (2019)
				producerName = driver.findElement(By.cssSelector("a.Link.ChannelInfo-pageLink")).getText();
			} else {
				producerName = producer.get(0).getText();
				producerName = producerName.substring(0, producerName.length() - 3);
			}
			logger.info("producer name: {}", producerName);
			
			WebElement tagContainer = driver.findElement(By.cssSelector("ul.TagList"));
			List<WebElement> tagElements = tagContainer.findElements(By.cssSelector("a.Link.TagItem-name"));
			for (WebElement tagElement : tagElements) {
				tags.add(tagElement.getText());
			}
			logger.info("tags: {}", tags);
			//driver.findElement(By.cssSelector("div.ControllerButton-inner")).click();//start the video, fake the server
		} catch (TimeoutException e) {
			e.printStackTrace();
			logger.info("CXwudi and Miku failed due to TimeoutException, we are trying again");
			return fetchInfo(song);
		} catch (InterruptedException e) {
			logger.info(e + "\nthis shouldn't happen at InfoGainer.fetchInfo()");
			return false;
		}
		if (videoURL == null || videoURL.equals("") || producerName.equals("") || tags.isEmpty() || videoTitle.equals("")) {
			logger.info("CXwudi and Miku failed to get some video info, we are trying again");
			return fetchInfo(song);
		} else {
			song.setProducerName(producerName).setURL(videoURL).setTagsList(tags).setTitle(videoTitle);
			logger.info("SEE!! CXwudi and miku get the video info, here is it:\n" + song);
			return true;
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
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

}
