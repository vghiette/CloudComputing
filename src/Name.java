/**
 * This class contains the information about a name instance which is detected
 * in a text and its number of occurrences in the text
 * 
 * @author Vincent Ghiette
 *
 */
public class Name {

	// The string representation of the occurrence, this might be a name such a
	// John
	private String name;
	// the number of times the instance occurs in the text
	private int occurrences;

	/**
	 * Returns a new Name instance form a string
	 * 
	 * @param name
	 *            the string of the name
	 */
	public Name(String name) {
		this.name = name;
	}

	/**
	 * Increment the number of occurrence of this name
	 */
	public void addOccurence() {
		occurrences++;
	}

	/**
	 * get the number of occurrences of that name
	 * 
	 * @return an integer representing the number of occurrences of this name
	 */
	public int getOccurences() {
		return occurrences;
	}

	/**
	 * Gets the string representation of the name.
	 * 
	 * @return a String representation of the name
	 */
	public String getName() {
		return name;
	}
}
