package com.cxwudi.niconico_videodownloader.entity;
import java.util.ArrayList;
import java.util.List;

import com.cxwudi.niconico_videodownloader.util.NicoStringTool;


/**
 * The Vocaloid Song Object that contains information of id, title, url, producer, tags and etc.
 * @author CX无敌
 */
public class Vsong implements Comparable<Vsong>{

	private String id;				//the sm-number or nm-number of the song
	private String URL;				//the url that contains video file for download
	private String title;			//the title of the song
	private String producerName;	//the name of producer
	private String subDir;			//the name of folder that contains this song
	private List<String> vocals;	//Singers who sing this song
	private List<String> tags;		//Tags of this song
	/**
	 * create Vocaloid Song object using only
	 * @param id the sm-number of the song
	 */
	public Vsong(String id) {
		this(id, "");
	}
	/**
	 * create Vocaloid Song object using
	 * @param id the sm-number of the song
	 * @param title the title of the song
	 */
	public Vsong(String id, String title) {
		this(id, title, "");
	}
	/**
	 * create Vocaloid Song object using
	 * @param id the sm-number of the song
	 * @param title the title of the song
	 * @param folder the folder where the song is stored.
	 */
	public Vsong(String id, String title, String folder) {
		this.id = id;
		this.title = title;
		this.subDir = folder;
		this.URL =  this.producerName = "";
		this.vocals = new ArrayList<>(3);
		this.tags = new ArrayList<>();
	}

	/** the hashcode of Vsong is the id number itself.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return NicoStringTool.filterIntsFromString(this.id);
	}

	/**
	 * return true if compared Vsong has same sm-number as this one.
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (!(obj instanceof Vsong)) return false;
		else return ((Vsong) obj).hashCode() == this.hashCode();
	}

	/**
	 * return the string representation of this Vsong as [Song: xxxxxx, ID: smxxxxxxxxx, Producer: xxxxxxxx]
	 */
	@Override
	public String toString() {
		return new StringBuilder()
				.append("\n[Song: ").append(title)
				.append(", ID: ").append(id)
				.append(producerName.equals("") ? "" : ", Producer: ").append(producerName)
				.append(subDir.equals("") ? "" : ", Folder: ").append(subDir)
				// .append(", status: ").append(isDownloaded ? "done" : "to be downloaded")
				.append("]")
				.toString();
	}

	/**
	 * define the natural order of Vsong, which is the increasing order of id.
	 */
	@Override
	public int compareTo(Vsong o) {
		return this.id.compareTo(o.id);
	}

	/**
	 * @return the URL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * @param URL the URL to set
	 * @return a reference to this Vocaloid Song object.
	 */
	public Vsong setURL(String URL) {
		this.URL = URL;
		return this;
	}

	/**
	 * @return the producerName
	 */
	public String getProducerName() {
		return producerName;
	}

	/**
	 * @param producerName the producerName to set
	 * @return a reference to this Vocaloid Song object.
	 */
	public Vsong setProducerName(String producerName) {
		this.producerName = producerName;
		return this;
	}

	//private boolean isDownloaded;	//have the video been downloaded or not
	
	/**
	 * @return the subDir
	 */
	public String getSubDir() {
		return subDir;
	}
	
	/** 
	 * @param subDir the subDir to set
	 * @return a reference to this Vocaloid Song object. 
	 */
	public Vsong setSubDir(String subDir) {
		this.subDir = subDir;
		return this;
	}
	/**
	 * @return the vocals
	 */
	public List<String> getVocalsList() {
		return vocals;
	}

	/**
	 * @param vocals the vocals to set
	 * @return a reference to this Vocaloid Song object.
	 */
	public Vsong setVocalsList(List<String> vocals) {
		this.vocals = vocals;
		return this;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTagsList() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 * @return a reference to this Vocaloid Song object.
	 */
	public Vsong setTagsList(List<String> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 * @return a reference to this Vocaloid Song object.
	 */
	public Vsong setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public static void main(String[] args) {
		testVsong();
		testString();
		Vsong vsong = new Vsong("sm23379461", "My PV");
		testVsongAtt(vsong);
		System.out.println(vsong);
	}
	private static void testVsongAtt(Vsong song) {
		String a = song.getSubDir();
		a = "asdasd";
		song.setSubDir(a);
		
	}
	private static void testString() {
		String a, b;
		a = b = new String("aaa");
		b = "bbb";
		System.out.println(a);
		String c = new String("aaa");
		System.out.println("c == a : " + (c == a));
		System.out.println("c == \"aaa\": " + (c == "aaa"));
		
	}
	private static void testVsong() {
		java.util.TreeSet<Vsong> set = new java.util.TreeSet<>(), set2 = new java.util.TreeSet<>(), pointer = null;
		System.out.println("add first song: " + set.add(new Vsong("sm27384957", "40mP MV")));
		System.out.println("add duplicated song: " + set.add(new Vsong("sm27384957", "40mP duplicate")));
		System.out.println("add second song: " + set.add(new Vsong("sm30772034", "LamazeP MV")));
		System.out.println("add third song: " + set.add(new Vsong("sm25446788", "MARETU MV")));
		System.out.println("the set is \n" + set);
		set2.add(new Vsong("sm27384957", "40mP MV"));
		set2.add(new Vsong("sm29987635", "Hachi PV"));
		set2.addAll(set);
		System.out.println("merge two set that contains same songs with different status: \n" + set2);
		pointer = set2;
		pointer.add(new Vsong("sm29882986", "Deco*27 PV"));
		System.out.println("pointer and reference theory applied in Java: \n" + set2);
		
	}
	

}
