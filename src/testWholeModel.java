
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class testWholeModel {

	public static void main(String[] args) {
		//open Chrome, login and setup niconico
		MainModel main = new MainModel();
		main.login();
		main.setupNicoNico();
		
		//get all my music collections
		HashMap<String, String> hashMap = main.listGrabber.getMyList();
		
		//for each folder in collections
		for (Entry<String, String> folder : hashMap.entrySet()) {
			
			//get the songs list from the folder
			TreeMap<String, String> List = main.listGrabber.fetchSMlists(folder.getKey());
			
			//get list of videos that are already download from local txt file
			main.taskManager.getIsDownloaded();
			
			//compare two lists to determine the videos need to be download
			TreeMap<String, String> toDoList = main.taskManager.toDoList(List);
			
			//download videos
			main.doTask(folder.getValue(), toDoList);
			
			//update the list of videos that have already been download.
			main.taskManager.updateDownloadedList(toDoList);
			
		}
		main.driver.close();
		System.out.println("CXWUDI SAFE YOUR LIVES FROM DOWNLOADING VIDEOS");
	}

}
