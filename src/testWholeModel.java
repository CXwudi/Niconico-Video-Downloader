
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
			
			//read downloaded.txt to generate a list of which videos are already download
			main.taskManager.getIsDownloaded();
			
			//compare two lists to determine new videos to be download
			TreeMap<String, String> toDoList = main.taskManager.toDoList(List);
			
			//download videos while update the list of videos that have already been download.
			main.doTaskWhileUpdate(folder.getValue(), toDoList);
			 
			
		}
		main.driver.close();
		System.err.println("全部のドンロードを終わった，ありがとうございます。");
		
	}

}
