import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import java.util.TreeMap;

public class TaskManager {
	BufferedReader reader;
	PrintWriter writer;
	File listDownloadedTxt;

	TreeMap<String, String> alreadyDownloaded; // same, sm number = song title
	TreeMap<String, String> toDownload; // same
	Comparator<String> invereOrder;//the inverse order, from newer to older songs

	public TaskManager() {
		// TODO Auto-generated constructor stub
		invereOrder = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		};
		alreadyDownloaded = new TreeMap<>(invereOrder);
		toDownload = new TreeMap<>(invereOrder);
		listDownloadedTxt = new File(new File("."), "downloaded.txt");

		try {
			reader = new BufferedReader(new FileReader(listDownloadedTxt));

		} catch (IOException e) {
			System.err.println(e);
		}

	}

	// read file from a txt file that records all smNumber of videos that have been
	// downloaded before.
	public TreeMap<String, String> getIsDownloaded() {

		try {
			for (String i = reader.readLine(); i != null; i = reader.readLine()) {
				alreadyDownloaded.put(i.substring(0, i.indexOf("\t")), i.substring(i.indexOf("\t")+1, i.length()));
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return alreadyDownloaded;
	}

	/**
	 * return the list of video should be downloaded.
	 * 
	 * @param HashMap<sm number, song title> lists
	 * @return HashMap<sm number, song title>
	 */
	public TreeMap<String, String> toDoList(TreeMap<String, String> lists) {
		System.out.print("your folder has: " + lists);
		TreeMap<String, String> toDoList = new TreeMap<>();
		for (Entry<String, String> smNumber : lists.entrySet()) {
			if (!alreadyDownloaded.containsKey(smNumber.getKey())) {
				toDoList.put(smNumber.getKey(), smNumber.getValue());
			}
		}
		System.out.println("video need to be downloaded: " + toDoList);
		return toDoList;
	}

	// update the alreadyDownloaded hashset, and write it into txt file.
	public void updateDownloadedList(TreeMap<String, String> merge) {
		try {
			writer = new PrintWriter(new FileWriter(listDownloadedTxt));
		} catch (IOException e) { }
		alreadyDownloaded.putAll(merge);
		System.out.println(alreadyDownloaded);
		boolean flag = false;
		for (Entry<String, String> entries : alreadyDownloaded.entrySet()) {
			writer.print((flag ? String.format("%n") : "") + entries.getKey() + "\t" + entries.getValue());
			flag = true;
		}
		writer.close();

	}

	public static void main(String[] args) throws IOException {
		TaskManager taskManager = new TaskManager();
		Map<String, String> map = taskManager.getIsDownloaded();
		System.out.println(map);
		TreeMap<String, String> map2 = new TreeMap<>();
		map2.put("sm333", "Koyori MV");
		map2.put("sm444", "MARETU MV");
		taskManager.updateDownloadedList(map2);

	}
}
