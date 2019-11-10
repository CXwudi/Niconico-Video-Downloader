package com.cxwudi.niconico_videodownloader;
import java.util.Random;

import com.cxwudi.niconico_videodownloader.solve_tasks.TasksSolver;
import com.cxwudi.niconico_videodownloader.util.Config;

public class Main {

	public static void main(String[] args) {
		Config.touch();
		MainModel main = new MainModel();
		main.login(Config.EMAIL, Config.PASSWORD);
		main.setupNicoNico();
		main.tasksDecider().readRecord();
		main.tasksDecider().getTaskAndUpdate();
//		main.taskManager().setAllDownload();//WARNING: remove this line to download songs
//		main.driver().resetDriver(); // no needed if using youtube-dl
//		main.setupNicoNico(); 
		TasksSolver manager = main.tasksSolver();
		main.tasksSolver().forEachVsongInTask(vsong -> {
			
			if (!manager.fetchInfo(vsong)) {
				return;
			}
			
			switch (manager.downloadOneVocaloidPV(vsong)) {
				case SUCCESS:
					System.out.println("done");
				break;
				case FAIL_RENAME:
					//do nothing
				break;
			default: //fail initial and fail download
				return;
			} 
					
			manager.markDone(vsong);
			manager.triggerRecord(); //if the currentRuntime.addShutdownHook works in eclipse, then we don't need this line
			System.out.println("record updated");
			
			try {
				Thread.sleep(1000L + new Random().nextInt(3000));
			} catch (InterruptedException e) {
				System.err.println(e + "\n this should not happen");
			}
			
		});
		System.err.println("おめでとう、全部ダウンロードを終わった");
		main.driver().quit();
	}

}


//legacy code
/*while (true) {
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
}*/