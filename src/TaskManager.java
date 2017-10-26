import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

public class TaskManager {
	BufferedReader reader;
	PrintWriter writer;
	File listDownloadedTxt;

	HashMap<String, String> listDownloaded; // same, sm number = song title
	HashMap<String, String> toDownload; // same

	public TaskManager() {
		// TODO Auto-generated constructor stub
		listDownloaded = new HashMap<>();
		toDownload = new HashMap<>();
		listDownloadedTxt = new File(new File("."), "downloaded.txt");

		try {
			reader = new BufferedReader(new FileReader(listDownloadedTxt));

		} catch (IOException e) {
			System.err.println(e);
		}

	}

	// read file from a txt file that records all smNumber of videos that have been
	// downloaded before.
	public HashMap<String, String> isDownloaded() {
		boolean isSM = true;
		String temp = "";
		try {
			for (String i = reader.readLine(); i != null; i = reader.readLine()) {
				if (isSM) {
					listDownloaded.put(i, "");
					temp = i;
				} else {
					listDownloaded.put(temp, i);
				}
				isSM = !isSM;

			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return listDownloaded;
	}

	/**
	 * return the list of video should be downloaded.
	 * 
	 * @param HashMap<sm
	 *            number, song title> lists
	 * @return HashMap<sm number, song title>
	 */
	public HashMap<String, String> downloadList(HashMap<String, String> lists) {

		return null;
	}

	// update the listDownloaded hashset, and write it into txt file.
	public void updateDownloadedList(HashMap<String, String> merge) throws IOException {

		writer = new PrintWriter(new FileWriter(listDownloadedTxt + ".out"));
		listDownloaded.putAll(merge);
		System.out.println(listDownloaded);
		boolean flag = false;
		for (Entry<String, String> entries : listDownloaded.entrySet()) {
			writer.println((flag ? String.format("%n") : "") + entries.getKey());
			writer.print(entries.getValue());
			flag = true;
		}

	}

	public static void main(String[] args) throws IOException {
		TaskManager taskManager = new TaskManager();
		HashMap<String, String> map = taskManager.isDownloaded();
		System.out.println(map);
		HashMap<String, String> map2 = new HashMap<>();
		map2.put("sm333", "Koyori MV");
		map2.put("sm444", "MARETU MV");
		taskManager.updateDownloadedList(map2);

	}
}
