import java.util.ArrayList;
import java.util.List;




/**
 * The Vocaloid Song Object that contains information of sm-number, title, url, producer, tags and etc.
 * @author CX无敌
 */
public class Vsong implements Comparable<Vsong>{

	private int smId;				//the sm-number of the song
	private String URL;		//the url that contains video file for download
	private String title;		//the title of the song
	private String producerName;	//the name of producer
	private String subDir;			//the name of folder that contains this song
	private List<String> vocals;	//Singers who sing this song
	private List<String> tags;		//Tags of this song
	/**
	 * create Vocaloid Song object using only
	 * @param id the sm-number of the song
	 */
	public Vsong(int id) {
		this(id, "");
	}
	/**
	 * create Vocaloid Song object using
	 * @param id the sm-number of the song
	 * @param title the title of the song
	 */
	public Vsong(int id, String title) {
		this(id, title, "");
	}
	/**
	 * create Vocaloid Song object using
	 * @param id the sm-number of the song
	 * @param title the title of the song
	 * @param folder the folder where the song is stored.
	 */
	public Vsong(int id, String title, String folder) {
		this.smId = id;
		this.title = title;
		this.subDir = folder;
		this.URL =  this.producerName = "";
		this.vocals = new ArrayList<>(3);
		this.tags = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.smId;
	}

	/**
	 * return true if compared Vsong has same sm-number as this one.
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (!(obj instanceof Vsong)) return false;
		return ((Vsong) obj).hashCode() == this.hashCode();
	}

	/**
	 * return the string representation of this Vsong as [Song: xxxxxx, sm-id: smxxxxxxxxx, Producer: xxxxxxxx]
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Song: ").append(title)
		.append(", SM-id: sm").append(smId)
		.append(producerName == "" ? "" :", Producer: ").append(producerName)
		.append(subDir == "" ? "": ", folder: ").append(subDir)
		//.append(", status: ").append(isDownloaded ? "done" : "to be downloaded")
		.append("]\n");
		return sb.toString();
	}

	/**
	 * define the natural order of Vsong, which is the newest song go first.
	 * In another word, bigger sm-number songs go first, rather than the Java integer natural order that smaller go first
	 */
	@Override
	public int compareTo(Vsong o) {
		return o.smId - this.smId;
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
	 * @return the smId
	 */
	public int getSmId() {
		return smId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	public static void main(String[] args) {
		testVsong();
		testString();
		Vsong vsong = new Vsong(23379461, "My PV");
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
		System.out.println("add first song: " + set.add(new Vsong(27384957, "40mP MV")));
		System.out.println("add duplicated song: " + set.add(new Vsong(27384957, "40mP duplicate")));
		System.out.println("add second song: " + set.add(new Vsong(30772034, "LamazeP MV")));
		System.out.println("add third song: " + set.add(new Vsong(25446788, "MARETU MV")));
		System.out.println("the set is \n" + set);
		set2.add(new Vsong(27384957, "40mP MV"));
		set2.add(new Vsong(29987635, "Hachi PV"));
		set2.addAll(set);
		System.out.println("merge two set that contains same songs with different status: \n" + set2);
		pointer = set2;
		pointer.add(new Vsong(29882986, "Deco*27 PV"));
		System.out.println("pointer and reference theory applied in Java: \n" + set2);
		
	}
	

}
