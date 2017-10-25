import java.util.ArrayList;
import java.util.Arrays;

public class testDownloading {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainModel main = new MainModel();
		
		ArrayList<String> smList = new ArrayList<>(Arrays.asList(new String[] { "sm32047871", "sm32012728", "sm31995557", "sm31985113" }));

		main.login();
		main.setupNicoNico();
		//testing downloading 4 videos
		for (String string : smList) { 
			 main.downloader.getVideoInfo(string);
			 main.downloader.downloadVideo(); 
			 main.downloader.rename(); 
		}
		//test getting mylist
		main.listGrabber.getMyList();
		for (String string : main.listGrabber.myLists.keySet()) {
			main.listGrabber.fetchSMlists(string);
		}
		 
	}

}
