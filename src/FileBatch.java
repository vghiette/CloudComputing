import java.util.ArrayList;
import java.util.List;

/**
 * The FileBatch class contains an ArrayList of file locations. This permits to
 * send work batches to slave instances. The batches define files which need to
 * be processed by a slave.
 * 
 * @author Vincent Ghiette
 *
 */
public class FileBatch {

	// Stores a list of FileLocations which refer to the files in the cloud
	// storage
	public List<FileLocation> batch;

	/**
	 * Creates a new instance of FileBatch give a List of FileLocations.
	 * 
	 * @param b
	 *            a list containing the FileLocations.
	 */
	public FileBatch(List<FileLocation> b) {
		batch = b;
	}

	/**
	 * Create a new instance of FileBatch given a string representation of a
	 * FileBatch.
	 * 
	 * @param batchString
	 *            String representation of a FileBatch instance.
	 */
	public FileBatch(String batchString) {
		// Trim unecessary spaces, and remove brackets
		batchString = batchString.trim();
		batchString = batchString.replace("[", "");
		batchString = batchString.replace("]", "");

		// Split the string in different FileLocation instances
		String[] batchStrings = batchString.split(",");

		batch = new ArrayList<FileLocation>();

		// For every FileLocation instance create a new FileLocation instance
		// from the String and add it to the List of FileLocations.
		for (int i = 0; i < batchStrings.length; i++) {
			batch.add(new FileLocation(batchStrings[i]));
		}
	}

	/**
	 * Returns the string representation of the FileBatch
	 */
	public String toString() {
		// Enclose the FileLocations in an array representation.
		String str = "[";

		// For each FileLocation in the batch add the string representation to
		// the array.
		for (int i = 0; i < batch.size(); i++) {
			if (i < batch.size() - 1) {
				str += batch.get(i).toString() + ",";
			} else {
				str += batch.get(i).toString();
			}
		}

		// Close the array representation.
		str += "]";

		return str;
	}
}
