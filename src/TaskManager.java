import java.util.Iterator;
import java.util.TreeSet;
/**
 * The manager that can generate a set of new videos that need to be downloaded, 
 * by operates two small model---local recorder and list grabber
 * 
 * Details of these two small models will be discuss in their class description. 
 * 
 * @author CX无敌
 */
public class TaskManager{
	
	private LocalReader localReader;
	private NicoListGrabber nicoListGrabber;
	
	private TreeSet<Vsong> task, update;

	public TaskManager(NicoDriver d, TreeSet<Vsong> task, TreeSet<Vsong> update) {	
		localReader = new LocalReader();
		nicoListGrabber = new NicoListGrabber(d);
		this.task = task;
		this.update = update;
	}
	/**
	 * Assign localRecord and NicoListGrabber to read their own Vocaloid Songs Collection
	 * @return true if both successfully done.
	 */
	public void readRecord() {
		Thread a = new Thread(new Runnable() {
			public void run() {
				localReader.readRecord();
			}
		});
		Thread b = new Thread(new Runnable() {
			public void run() {
				nicoListGrabber.readRecord();
			}
		});
		a.start();
		b.start();
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			System.err.println(e + "\nthis shouldn't happen");
		}
	}
	/**
	 * Grab localRecord and NicoListGrabber's songs collection to determine the task for downloading Vocaloid Songs,
	 * and the list of downloaded songs for record.
	 * @param task the collection of video that needed to be downloaded
	 * @param update the collection of video that already been downloaded
	 * @return true if the function fulfills both task and update.
	 */
	public boolean getTaskAndUpdate() {
		if (!localReader.isDone() || !nicoListGrabber.isDone()) 
			return false;
		TreeSet<Vsong> local = getLocalCollection(), online = getOnlineCollection();
		for (Iterator<Vsong> iterator = online.iterator(); iterator.hasNext();) {
			Vsong vsong = iterator.next();
			TreeSet<Vsong> pointer = local.contains(vsong)? update : task;
			pointer.add(vsong);
		}
		System.out.println("PV that needed to be downloaded: \n" + task);
		System.out.println("PV that already been downloaded: \n" + update);
		return true;
	}
	/**
	 * mark all PV in niconico collections as downloaded
	 * @return true if success
	 */
	public boolean setAllDownload() {
		if(!nicoListGrabber.isDone()) return false;
		update.addAll(getOnlineCollection());
		return true;
	}
	/**
	 * @return the local record of downloaded PV
	 */
	public TreeSet<Vsong> getLocalCollection() {
		return localReader.getCollection();
	}

	/**
	 * @return the online collection of Vocaloid PV
	 */
	public TreeSet<Vsong> getOnlineCollection() {
		return nicoListGrabber.getCollection();
	}
	/**
	 * @return the task
	 */
	public TreeSet<Vsong> getTask() {
		return task;
	}
	/**
	 * @return the update
	 */
	public TreeSet<Vsong> getUpdate() {
		return update;
	}
	
}
