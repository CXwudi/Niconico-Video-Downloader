package com.cxwudi.niconico_video_downloader.side_project.niconico_video_extractor;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
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
		DefaultFFMPEGLocator locator= new  DefaultFFMPEGLocator();
        String exePath= locator.getFFMPEGExecutablePath();
        System.out.println("ffmpeg executable found in <"+exePath+">");
        File testFile = new File("いじめられっ子のルアン／初音ミク【雨の介】.mp4");
		var ffpmegProcess = new ProcessBuilder(exePath,
				"-i", testFile.getName(), //input file
				"-y", //answer yes for overwriting files
				"-vn","-acodec","copy", //copy audio stream from video
				testFile.getName().replace(".mp4", ".aac")); //output file
		ffpmegProcess.directory(new File("C:\\Users\\11134\\Videos\\ffmpeg-4.1.4-win64-static\\bin"));
		ffpmegProcess = ffpmegProcess.redirectInput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
		ffpmegProcess.start();

        
		
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
