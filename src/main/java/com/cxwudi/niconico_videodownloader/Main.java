package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.solve_tasks.TasksSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		Config.touch();
		MainModel main = new MainModel();
		main.login();
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

			var task = manager.convertVsongToTask(vsong);
			
			switch (manager.downloadOneVocaloidPV(task)) {
				case SUCCESS:
					logger.info("done");
				break;
				case FAIL_RENAME:
					//do nothing
				break;
			default: //fail initial and fail download
				return;
			} 
					
			manager.markDone(vsong);
			manager.triggerRecord(); //if the currentRuntime.addShutdownHook works in eclipse, then we don't need this line
			
			try {
				Thread.sleep(1000L + new Random().nextInt(3000));
			} catch (InterruptedException e) {
				logger.error("\n {} this should not happen", e);
			}
			
		});
		logger.warn("おめでとう、全部ダウンロードを終わった");
		main.driver().quit();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}


//legacy code
/*while (true) {
	manager.fetchInfo(vsong);
	if (!manager.downloadOneVocaloidPV(vsong)) logger.info(vsong + "doesn't exist!!, plz skip");
	
	var scanner = new Scanner(System.in);
	String answer = "";
	while (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n")) {
		System.err.print("PLZ check the video file, is it integrite? y/n: ");
		answer = scanner.nextLine();
	}
	if (answer.equalsIgnoreCase("y")) {
		logger.info("Good, CXwudi and Miku are moving to next file");
		manager.markDone(vsong);
		manager.triggerRecord();// we add this line for now
		break;
	} else {
		logger.info("Okay, CXwudi and Miku will re-download this song :/");
	}
}*/