package com.cxwudi.side_project.niconico_video_extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.junit.Test;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;

import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;

public class Mp4PraserTest {

	@Test
	public void test() throws FileNotFoundException, IOException {
		var directory = new File("test_files");
		var input = new File(directory, "いじめられっ子のルアン／初音ミク【雨の介】.aac");
		var output = new File(directory, "いじめられっ子のルアン／初音ミク【雨の介】.m4a");
		if (!input.exists()) {
			try {
				new FFmpegProcessTest().testExtractAudioWithApis();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InputFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EncoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		var aacTrack = new AACTrackImpl(new FileDataSourceImpl(input));
		var movie = new Movie();
		movie.addTrack(aacTrack);
		
		Container m4aFile = new DefaultMp4Builder().build(movie);
		try (FileChannel ch = new FileOutputStream(output).getChannel()){
			m4aFile.writeContainer(ch);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
