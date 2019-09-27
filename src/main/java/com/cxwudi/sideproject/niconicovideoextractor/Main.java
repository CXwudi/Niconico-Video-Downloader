package com.cxwudi.sideproject.niconicovideoextractor;

import java.io.File;

public class Main {

	public static void main(String[] args) throws Exception {
		File inputRoot = new File(System.getProperty("user.home") + "\\Videos");
		File outputRoot = new File(inputRoot, "2019年V家新曲合集");
		File[] folders = new File[] { 
				new File(inputRoot, "2019年V家新曲"), 
				new File(inputRoot, "2019年V家可能好听") 
				};

		var extractAudioProcess = new AudioExtractor(inputRoot, folders, outputRoot);
		extractAudioProcess.dojob();

	}

}
