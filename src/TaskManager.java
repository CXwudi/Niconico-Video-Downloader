import java.util.TreeSet;
/**
 * The manager that can generate a set of new videos that need to be downloaded, 
 * by operates two small model---local recorder and list grabber
 * 
 * Details of these two small models will be discuss in their class description. 
 * 
 * @author CX无敌
 */
public class TaskManager {
	
	private NicoDriver driver;
	private LocalRecorder localRecorder;
	private ListGrabber listGrabber;
	/* task: the todo list
	 * local: the record of saved video
	 * online: the list of my favorite video from niconico
	 */
	private TreeSet<Vsong> task, local, online;	
	
	public TaskManager(NicoDriver d, TreeSet<Vsong> set) {
		driver = d;
		localRecorder = new LocalRecorder();
		listGrabber = new ListGrabber(d);
		
		task = set;
		local = online = new TreeSet<>();
		
	}
	
}
