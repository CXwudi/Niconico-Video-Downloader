package com.cxwudi.niconico_video_downloader.side_project.niconico_video_extractor;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;

import org.junit.Test;

import ws.schild.jave.DefaultFFMPEGLocator;

public class FFmpegProcessTest {

	@Test
	public void testProcess() throws IOException {
//		var ffpmegProcess = new ProcessBuilder("ffmpeg", "--version");
//		ffpmegProcess = ffpmegProcess.redirectInput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
//		ffpmegProcess.start();
		DefaultFFMPEGLocator locator= new  DefaultFFMPEGLocator();
        String exePath= locator.getFFMPEGExecutablePath();
        System.out.println("ffmpeg executable found in <"+exePath+">");
	
		
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
