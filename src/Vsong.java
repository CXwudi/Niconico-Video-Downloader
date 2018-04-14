import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


/**
 * The Vocaloid Song Object that contains information of sm-number, title, url, producer, tags and etc.
 * @author CX无敌
 */
public class Vsong implements Comparable<Vsong>{

	private int smId;				//the sm-number of the song
	private String videoURL;		//the url that contains video file for download
	private String videoTitle;		//the title of the song
	private String producerName;	//the name of producer
	private String subDir;			//the name of folder that contains this song
	private boolean isDownloaded;	//have the video been downloaded or not

	private Set<String> vocals;		//Singers who sing this song
	private Set<String> tags;		//Tags of this song

	/**
	 * create Vocaloid Song object using sm-number and song title
	 */
	public Vsong(int id, String title, boolean done) {
		smId = id;
		videoTitle = title;
		subDir = videoURL =  producerName = "";
		vocals = tags = new HashSet<>(0);
		isDownloaded = done;
	}
	
	public Vsong(int id, String title, String folder, boolean done) {
		smId = id;
		videoTitle = title;
		subDir = folder;
		videoURL =  producerName = "";
		vocals = tags = new HashSet<>(0);
		isDownloaded = done;
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
	 * return the string representation of this Vsong as [Song: xxxxxx, sm-number: smxxxxxxxxx, Producer: xxxxxxxx]
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Song: ").append(videoTitle)
		.append(", sm-number: sm").append(smId)
		.append(producerName == "" ? "" :", Producer: ").append(producerName)
		.append(subDir == "" ? "": ", folder: ").append(subDir)
		.append(", status: ").append(isDownloaded ? "downloaded" : "to be downloaded")
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
	 * @return the videoURL
	 */
	public String getVideoURL() {
		return videoURL;
	}

	/**
	 * @param videoURL the videoURL to set
	 */
	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

	/**
	 * @return the producerName
	 */
	public String getProducerName() {
		return producerName;
	}

	/**
	 * @param producerName the producerName to set
	 */
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	/**
	 * @return the vocals
	 */
	public Set<String> getVocals() {
		return vocals;
	}

	/**
	 * @param vocals the vocals to set
	 */
	public void setVocals(Set<String> vocals) {
		this.vocals = vocals;
	}

	/**
	 * @return the tags
	 */
	public Set<String> getTagSet() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTagSet(Set<String> tagSet) {
		this.tags = tagSet;
	}

	/**
	 * @return the smId
	 */
	public int getSmId() {
		return smId;
	}

	/**
	 * @return the videoTitle
	 */
	public String getVideoTitle() {
		return videoTitle;
	}
	
	public static void main(String[] args) {
		testVsong();
	}
	private static void testVsong() {
		TreeSet<Vsong> set = new TreeSet<>();
		System.out.println("add first song: " + set.add(new Vsong(27384957, "40mP MV",true)));
		System.out.println("add duplicated song: " + set.add(new Vsong(27384957, "40mP duplicate",false)));
		System.out.println("add second song: " + set.add(new Vsong(30772034, "LamazeP MV",true)));
		System.out.println("add third song: " + set.add(new Vsong(25446788, "MARETU MV",true)));
		System.out.println("the set is " + set);
	}
	

}
