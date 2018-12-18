import java.util.Random;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		MainModel main = new MainModel();
		main.login("1113421658@qq.com", "2010017980502");
		main.setupNicoNico();
		main.taskManager().readRecord();
		main.taskManager().getTaskAndUpdate();
		main.driver().resetDriver();
		main.setupNicoNico();
		main.downloadManager().forEachVsong(vsong -> {
			DownloadManager manager = main.downloadManager();
			while (true) {
				manager.fetchInfo(vsong);
				if (!manager.downloadOneVocaloidPV(vsong)) System.out.println(vsong + "doesn't exist!!, plz skip");
				var scanner = new Scanner(System.in);
				String answer = "";
				while (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n")) {
					System.err.print("PLZ check the video file, is it integrite? y/n: ");
					answer = scanner.nextLine();
				}
				if (answer.equalsIgnoreCase("y")) {
					System.out.println("Good, CXwudi and Miku are moving to next file");
					manager.markDone(vsong);
					manager.triggerRecord();// we add this line for now
					break;
				} else {
					System.out.println("Okay, CXwudi and Miku will re-download this song :/");
				}
			}
			try {
				Thread.sleep(4000L + new Random().nextInt(5000));// does 20 seconds help?
			} catch (InterruptedException e) {
				System.err.println(e + "\n this should not happen");
			}
			
		});
	}

}
