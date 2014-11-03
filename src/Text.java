import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * Text class, this class handles the storage and analysis of the texts Relies
 * heavily on OPENNLP
 * 
 * @author Vincent Ghiette
 *
 */
public class Text {

	// Number of occurrences in a text
	int totalOccurrences;
	// Path of the text
	String path;
	// The text of the text
	String text;
	// The title of the text
	String title;
	// Indicates weather the text has been analyzed, NOT USED
	Boolean analyzed = false;
	// List of person names occurring in the text
	ArrayList<Name> names;
	// List of location names occurring in the text
	ArrayList<Name> locations;
	// List of organization name occurring in the text
	ArrayList<Name> organisations;

	/**
	 * Creates a new instance of the Text, it sets the test's text to the given
	 * text String.
	 * 
	 * @param t
	 *            the String representation of the text
	 */
	public Text(String t) {
		text = t;
		// Generate the title of the text
		generateTitle(text);
	}

	/**
	 * Generate the title of the text given the body of the Text
	 * 
	 * @param text
	 *            the body of the text
	 */
	private void generateTitle(String text) {
		String[] splitted = text.split("-");
		if (splitted.length > 1) {
			title = splitted[0];
		} else {
			title = text.substring(0, 30);
		}
	}

	/**
	 * Analyzes the Text and gets all of the name occurrences This function
	 * relies on the OPNNLB library
	 */
	public void analyze() {
		String tokens[];

		try {
			// Split the text into tokens.
			tokens = tokenize(text);
			// Get the person names
			names = findNames(tokens);
			// Get the location names
			locations = findLocations(tokens);
			// Get the organization names
			organisations = findOrganisations(tokens);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Find the person names in the text body
	 * 
	 * @param tokens
	 *            The token representation of the text
	 * @return ArrayList containing Name instances of the person names and their
	 *         occurrences in the text
	 * @throws IOException
	 *             if something goes wrong
	 */
	private ArrayList<Name> findNames(String[] tokens) throws IOException {
		// Load the person name training file view OPENNLB
		InputStream is = new FileInputStream("models/en-ner-person.bin");

		// Create the Name finder
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		NameFinderME nameFinder = new NameFinderME(model);

		// Find the names
		Span nameSpans[] = nameFinder.find(tokens);

		// Check if names are found, if not return null
		if (nameSpans.length == 0) {
			return null;
		}

		names = new ArrayList<Name>();

		// For each name found add it to the names Arraylist
		for (int i = 0; i < nameSpans.length; i++) {
			String tempName = "";

			for (int j = nameSpans[i].getStart(); j < nameSpans[i].getEnd(); j++) {
				tempName += tokens[j] + " ";
			}

			addName(tempName, names);
		}

		return names;
	}

	/**
	 * Find the organization names in the text body
	 * 
	 * @param tokens
	 *            The token representation of the text
	 * @return ArrayList containing Name instances of the organization names and
	 *         their occurrences in the text
	 * @throws IOException
	 *             if something goes wrong
	 */
	private ArrayList<Name> findOrganisations(String[] tokens)
			throws IOException {
		// Load the learning model
		InputStream is = new FileInputStream("models/en-ner-organization.bin");

		// Find the organization names
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		NameFinderME organisationFinder = new NameFinderME(model);

		Span organisationSpans[] = organisationFinder.find(tokens);

		// If no organization is found return null
		if (organisationSpans.length == 0) {
			return null;
		}

		organisations = new ArrayList<Name>();

		// for each organization add it to the organization ArrayList
		for (int i = 0; i < organisationSpans.length; i++) {
			String tempName = "";

			for (int j = organisationSpans[i].getStart(); j < organisationSpans[i]
					.getEnd(); j++) {
				tempName += tokens[j] + " ";
			}

			addName(tempName, organisations);
		}

		return organisations;
	}

	/**
	 * Find the location names in the text body
	 * 
	 * @param tokens
	 *            The token representation of the text
	 * @return ArrayList containing Name instances of the location names and
	 *         their occurrences in the text
	 * @throws IOException
	 *             if something goes wrong
	 */
	private ArrayList<Name> findLocations(String[] tokens) throws IOException {
		// load the training location finder
		InputStream is = new FileInputStream("models/en-ner-location.bin");

		// Find the location names
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		NameFinderME locationFinder = new NameFinderME(model);

		Span locationSpans[] = locationFinder.find(tokens);

		// If no locations are found return null
		if (locationSpans.length == 0) {
			return null;
		}

		// For each found location add it to the locations ArrayList
		locations = new ArrayList<Name>();

		for (int i = 0; i < locationSpans.length; i++) {
			String tempName = "";

			for (int j = locationSpans[i].getStart(); j < locationSpans[i]
					.getEnd(); j++) {
				tempName += tokens[j] + " ";
			}

			addName(tempName, locations);
		}

		return locations;

	}

	/**
	 * Adds a Name instance to the given Name instance list This can be the
	 * person, location or organization ArrayList
	 * 
	 * @param name
	 *            the name instance to be added to the list
	 * @param list
	 *            the list instance to which the name instance should be added
	 */
	private void addName(String name, ArrayList<Name> list) {
		name = name.trim();
		name = name.toLowerCase();

		// Check if name is already in the names list
		// If so the occurrence is increased, else the name is added and the
		// occurrence is also increased
		int index = findName(name, list);
		if (index > -1) {
			list.get(index).addOccurence();
		} else {
			Name tempName = new Name(name);
			tempName.addOccurence();
			list.add(tempName);
		}
	}

	/**
	 * Gets the index of the Name instance given the List instance
	 * 
	 * @param name
	 *            the name instance to look for
	 * @param list
	 *            the list instance in which to look for the name
	 * @return the index of the name instance in the given list
	 */
	private int findName(String name, ArrayList<Name> list) {
		int index = -1;

		// Loop through the list for the given instance
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(name)) {
				index = i;
			}
		}

		return index;

	}

	/**
	 * Given the person, location and organization give the total amount of
	 * occurrences in the text
	 * 
	 * @param personName
	 *            name of the person to count the occurrences for
	 * @param locationName
	 *            name of the location to count the occurrences for
	 * @param organisationName
	 *            name of the organization to count the occurrences for
	 */
	public void calculateTotalOccurrences(String personName,
			String locationName, String organisationName) {
		int occurrences = 0;

		// Only count the person occurrences which have been searched for
		if (personName != null && !personName.isEmpty()) {
			occurrences += getTotalMatchingOccurences(names, personName);
		}

		// Only count the location occurrences which have been searched for
		if (locationName != null && !locationName.isEmpty()) {
			occurrences += getTotalMatchingOccurences(locations, locationName);
		}

		// Only count the organization occurrences which have been searched for
		if (organisationName != null && !organisationName.isEmpty()) {
			occurrences += getTotalMatchingOccurences(organisations,
					organisationName);
		}

		// Return the total of occurrences
		totalOccurrences = occurrences;
	}

	/**
	 * Getter for the total amount of occurrences
	 * 
	 * @return the total amount of occurrences
	 */
	public int getTotalOccurences() {
		return totalOccurrences;
	}

	/**
	 * Getter for the name instances of the person name sin the text
	 * 
	 * @return ArrayList contain the person Name instances
	 */
	public ArrayList<Name> getNames() {
		return names;
	}

	/**
	 * Getter for the location instances of the person name sin the text
	 * 
	 * @return ArrayList contain the location Name instances
	 */
	public ArrayList<Name> getLocations() {
		return locations;
	}

	/**
	 * Getter for the organization instances of the person name sin the text
	 * 
	 * @return ArrayList contain the organization Name instances
	 */
	public ArrayList<Name> getOrganisations() {
		return organisations;
	}

	/**
	 * Getter for the title of the text
	 * 
	 * @return title of the text
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the matching occurrences given a ArrayList of Name instances and a
	 * sString representing the name to look for the function look if the given
	 * string is a substring of a Name instance in the list
	 * 
	 * @param listName
	 *            the list in which the function should search
	 * @param strName
	 *            the string representation of a name for which the function
	 *            should look for in a Name list
	 * @return the total matching occurrences of the string in the given list
	 */
	public int getTotalMatchingOccurences(ArrayList<Name> listName,
			String strName) {
		int oc = 0;
		if (listName != null) {
			for (Name n : listName) {
				if (n.getName().contains(strName)) {
					oc += n.getOccurences();
				}
			}
		}

		return oc;
	}

	/**
	 * Tokenizes the string so that PENNLP can be used to parse the text
	 * 
	 * @param text
	 *            the text to be tokenized
	 * @return tokenized version of the text
	 * @throws InvalidFormatException
	 *             if something goes wrong
	 * @throws IOException
	 *             if something goes wrong
	 */
	private String[] tokenize(String text) throws InvalidFormatException,
			IOException {
		// load the OPENNLP tokenizer model
		InputStream is = new FileInputStream("models/en-token.bin");

		// Create new tokenizer
		TokenizerModel model = new TokenizerModel(is);

		Tokenizer tokenizer = new TokenizerME(model);

		// Tokenize the text
		String tokens[] = tokenizer.tokenize(text);

		is.close();

		// Returns tokenized text
		return tokens;
	}

}
