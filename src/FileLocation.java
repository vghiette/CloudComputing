/**
 * The FileLocation class represents a file stored in a Amazon S3 server. It
 * contains information about the bucket in which the file is stored and the key
 * of the file in that Bucket. For serialization purposes (sending data in json)
 * the toString method has been implemented and the constructor accepts a string
 * representation of the FileLocation.
 * 
 * @author Vincent Ghiette
 *
 */
public class FileLocation {
	// Stores the name of the bucket in which the file is stored in
	String bucket;
	// Stores the key of the file
	String key;

	/**
	 * Creates a new instance of FileLocation
	 * 
	 * @param b
	 *            contains the name of the bucket in which the file has been
	 *            stored.
	 * @param k
	 *            contains the corresponding key of the item in the bucket.
	 */
	public FileLocation(String b, String k) {
		bucket = b;
		key = k;
	}

	/**
	 * Creates a new instance of file location given the String equivalent.
	 * 
	 * @param loc
	 *            the String representation of a file location.
	 */
	public FileLocation(String loc) {
		loc = loc.trim();
		loc = loc.replace("[", "");
		loc = loc.replace("]", "");

		String[] strs = loc.split(";");

		bucket = strs[0];
		key = strs[1];
	}

	/**
	 * Returns the string equivalent of the FileLocation [<bucket>;<key>].
	 */
	public String toString() {
		return "[" + bucket + ";" + key + "]";
	}

}
