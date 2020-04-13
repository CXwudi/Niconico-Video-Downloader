package com.cxwudi.side_project.niconico_video_extractor;

import org.junit.jupiter.api.Test;
import ws.schild.jave.*;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FFmpegProcessTest {

	@Test
	public void testExtractAudioWithProcessBuilder() throws IOException, InterruptedException {
		DefaultFFMPEGLocator locator= new DefaultFFMPEGLocator();
        var ffmpegPath = locator.getFFMPEGExecutablePath();
        System.out.println("ffmpeg executable found in <"+ffmpegPath+">");
        String testFile = "いじめられっ子のルアン／初音ミク【雨の介】.mp4";
		var ffpmegProcessBuilder = new ProcessBuilder(ffmpegPath,
				"-i", testFile, //input file
				"-y", //answer yes for overwriting files
				"-vn","-acodec","copy", //copy audio stream from video
				testFile.replace(".mp4", ".aac")); //output file
		ffpmegProcessBuilder.directory(new File("test_files"));
		ffpmegProcessBuilder = ffpmegProcessBuilder.redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT).redirectErrorStream(true);
		var ffpmegProcess = ffpmegProcessBuilder.start();
		ffpmegProcess.waitFor();
        
		assertTrue(true);
	}
	
	@Test
	public void testExtractAudioWithApis() throws IllegalArgumentException, InputFormatException, EncoderException {
		var audio = new AudioAttributes();
		audio.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
		
		var attributes = new EncodingAttributes();
		attributes.setAudioAttributes(audio);
		attributes.setVideoAttributes(null);
		
		var ffmpegEncoder = new Encoder();
		var directory = new File("test_files");
		var input = new File(directory, "【初音ミクDark】休憩【keeno】.flv");
		var output = new File(directory, "【初音ミクDark】休憩【keeno】.aac");
		ffmpegEncoder.encode(new MultimediaObject(input), output, attributes,
				new EncoderProgressListener() {
					
					@Override
					public void sourceInfo(MultimediaInfo info) {
						System.out.println("format: " + info.toString());
						
					}
					
					@Override
					public void progress(int permil) {
						System.out.println("current progress: " + (permil/10.0) + "%");
						
					}
					
					@Override
					public void message(String message) {
						System.err.println("message: ");
						System.err.println(message);
						
					}
				});
		
		assertTrue(output.exists());
	}

	@Test
	public void testVersion() throws IOException, InterruptedException {
		DefaultFFMPEGLocator locator= new DefaultFFMPEGLocator();
		var ffmpegPath = locator.getFFMPEGExecutablePath();
		var ffmpegProcessBuilder = new ProcessBuilder(ffmpegPath, "--version");
		var p = ffmpegProcessBuilder.inheritIO().start();
		p.waitFor();
	}
}
