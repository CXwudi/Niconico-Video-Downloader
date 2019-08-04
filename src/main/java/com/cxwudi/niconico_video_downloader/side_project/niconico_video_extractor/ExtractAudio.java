package com.cxwudi.niconico_video_downloader.side_project.niconico_video_extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class ExtractAudio {

	public static void main(String[] args) {
		
		//ProcessBuilder
	}
	
	private static void syncStream(InputStream input, StringBuilder sb, PrintStream out) {
		try(var output = new BufferedReader(new InputStreamReader(input))){
			String s;
			
			while ((s = output.readLine()) != null) {
				if (sb != null) sb.append(s).append("\n");
				out.println(s);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
