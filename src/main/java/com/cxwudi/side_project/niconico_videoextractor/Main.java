package com.cxwudi.side_project.niconico_videoextractor;

import java.io.File;

public class Main {

	public static void main(String[] args) throws Exception {
		File inputRoot = new File("D:\\11134\\Videos");
		File outputRoot = new File(inputRoot, "2019年V家新曲合集");
		File[] folders = new File[] { 
				new File(inputRoot, "2019年V家新曲"), 
				new File(inputRoot, "2019年V家可能好听") 
				};

		var extractAudioProcess = new AudioExtractor(folders, outputRoot);
		extractAudioProcess.dojob();

	}

}
