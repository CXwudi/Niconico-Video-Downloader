import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import java.util.TreeMap;

public class LocalRecorder {
	BufferedReader reader;
	PrintWriter writer;
	File listDownloadedTxt;

	TreeMap<String, String> alreadyDownloaded; // same, sm number = song title
	TreeMap<String, String> toDownload; // same
	Comparator<String> invereOrder;//the inverse order, from newer to older songs

	public LocalRecorder() {
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

		

	}

	// read file from a txt file that records all smNumber of videos that have been
	// downloaded before.
	public TreeMap<String, String> getIsDownloaded() {
		try {
			reader = new BufferedReader(new FileReader(listDownloadedTxt));
			for (String i = reader.readLine(); i != null && !i.equals(""); i = reader.readLine()) {
				alreadyDownloaded.put(i.substring(0, i.indexOf("\t")), i.substring(i.indexOf("\t")+1, i.length()));
			}
			reader.close();
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
		//System.out.println("your folder has: " + lists);
		TreeMap<String, String> toDoList = new TreeMap<>();
		for (Entry<String, String> smNumber : lists.entrySet()) {
			if (!alreadyDownloaded.containsKey(smNumber.getKey())) {
				toDoList.put(smNumber.getKey(), smNumber.getValue());
			}
		}
		System.out.println("video need to be downloaded: " + toDoList);
		return toDoList;
	}
	
	public void updateDownloadedList(String key, String value) {
		TreeMap<String, String> treeMap = new TreeMap<>();
		treeMap.put(key, value);
		updateDownloadedList(treeMap);
	}

	// update the alreadyDownloaded hashset, and write it into txt file.
	public void updateDownloadedList(TreeMap<String, String> merge) {
		try {
			writer = new PrintWriter(new FileWriter(listDownloadedTxt));
		} catch (IOException e) { }
		alreadyDownloaded.putAll(merge);

		for (Entry<String, String> entries : alreadyDownloaded.entrySet()) {
			writer.println(entries.getKey() + "\t" + entries.getValue());//(flag ? String.format("%n") : "") + 
		}
		System.out.println("add videos' info to downloaded.txt success");
		writer.close();

	}
	

	public static void main(String[] args) throws IOException {
		LocalRecorder localRecorder = new LocalRecorder();
		Map<String, String> map = localRecorder.getIsDownloaded();
		System.out.println(map);
		TreeMap<String, String> map2 = new TreeMap<>();
		map2.put("sm333", "Koyori MV");
		map2.put("sm444", "MARETU MV");
		localRecorder.updateDownloadedList(map2);

	}
}
