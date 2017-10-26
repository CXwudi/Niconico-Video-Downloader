import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class testDownloading {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		
		ArrayList<String> smList = new ArrayList<>(Arrays.asList(new String[] {
				"sm32047871" }));

		main.login();
		main.setupNicoNico();
		//testing downloading some videos
		for (String string : smList) { 
			 main.downloader.getVideoInfoFrom(string);
			 main.downloader.downloadVideoTo(""); 
			 //main.downloader.rename(); 
		}
		
		 
	}

}
