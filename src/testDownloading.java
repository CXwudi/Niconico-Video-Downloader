import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.Cookie;

public class testDownloading {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		
		ArrayList<String> smList = new ArrayList<>(Arrays.asList(new String[] {
				"sm27468592" }));

		main.login();
		main.setupNicoNico();
		//testing downloading some videos
		for (String string : smList) { 
			 main.downloader.getVideoInfoFrom(string);
			 if (main.downloader.videoURL.contains("smile?m")) {
				 //main.driver.get(main.downloader.videoURL);
				 Set<Cookie> cookies = main.driver().manage().getCookies();
				 for (Iterator iterator = cookies.iterator(); iterator.hasNext();) {
					Cookie cookie = (Cookie) iterator.next();
					System.out.println(cookie.toString());
				}
			 } else {
				 main.downloader.downloadVideoTo(""); 
			 }
			
			 //main.downloader.rename(); 
		}
		
		 
	}

}
