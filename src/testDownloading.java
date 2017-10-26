import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class testDownloading {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		
		ArrayList<String> smList = new ArrayList<>(Arrays.asList(new String[] {
				"sm31985113" }));

		main.login();
		main.setupNicoNico();
		//testing downloading 4 videos
		for (String string : smList) { 
			 main.downloader.getVideoInfoFrom(string);
			 main.downloader.downloadVideoTo(""); 
			 main.downloader.rename(); 
		}
		//test getting mylist
		HashMap<String, String> hashMap = main.listGrabber.getMyList();
		/*for (String string : hashMap.keySet()) {
			main.listGrabber.fetchSMlists(string);
		}*/
		 
	}

}
