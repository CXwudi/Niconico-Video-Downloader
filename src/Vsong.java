import java.util.Set;

/**
 * 
 */

/**
 * The Vocaloid Song Object that contains information of sm-number, title, url, producer, tags and etc.
 * @author CX无敌
 */
public class Vsong implements Comparable<Vsong>{

	private int smId;				//the sm-number of the song
	private String videoURL;		//the url that contains video file for download
	private String videoTitle;		//the title of the song
	private String producerName;	//the name of producer

	private Set<String> vocals;		//Singers who sing this song
	private Set<String> tags;		//Tags of this song

	/**
	 * create Vocaloid Song object using sm-number and song title
	 */
	public Vsong(int id, String title) {
		smId = id;
		videoTitle = title;
		videoURL =  producerName = "";
		vocals = tags = null;
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
		sb.append("Song: ").append(videoTitle).append(", sm-number: sm").append(smId).append(", Producer: ").append(producerName);
		return sb.toString();
	}

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
	

}
