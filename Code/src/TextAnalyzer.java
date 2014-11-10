import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * textAnalyzer class. his class handles the analysis of the texts. it relies
 * heavily on OPENNLP
 * 
 * @author Vincent Ghiette
 *
 */
public class TextAnalyzer {
	// The ArrayList containing the text of the files
	ArrayList<String> files;

	/**
	 * Creates a new instance of TextAnalyzer given the texts to be analyzed
	 * 
	 * @param f
	 *            ArrayList containing the texts in string to be analyzed
	 */
	public TextAnalyzer(ArrayList<String> f) {
		files = f;
	}

	/**
	 * Returns the relevant texts given the person location and organization
	 * names THe operation is an OR operation, therefore it will return texts
	 * containing the person or location or organization
	 * 
	 * @param personName
	 *            the string of the person name to look for
	 * @param locationName
	 *            the string of the location to look for
	 * @param organisationName
	 *            the string of the organization to look for
	 * @return an ArrayList containing the relevant Text instances of the given
	 *         files.
	 */
	public ArrayList<Text> getRelevantTexts(String personName,
			String locationName, String organisationName) {
		// Analyze all the texts
		ArrayList<Text> texts = analyseTexts(files);

		// Filter the texts
		ArrayList<Text> filteredTexts = new ArrayList<Text>();

		// This is an or Filter
		if (!personName.isEmpty()) {
			filteredTexts.addAll(filterPerson(texts, personName));
		}
		if (!locationName.isEmpty()) {
			filteredTexts.addAll(filterLocation(texts, locationName));
		}
		if (!organisationName.isEmpty()) {
			filteredTexts.addAll(filterOrganisation(texts, organisationName));
		}

		ArrayList<Text> deduplicatedTexts = new ArrayList<Text>();

		// Deduplicate the texts
		if (filteredTexts.size() > 0) {
			deduplicatedTexts.addAll(removeDuplicates(filteredTexts));
		}

		for (Text t : deduplicatedTexts) {

			t.calculateTotalOccurrences(personName, locationName,
					organisationName);
		}

		ArrayList<Text> sortedtexts = sortTexts(deduplicatedTexts);

		return sortedtexts;
	}

	/**
	 * Analyzes the given texts
	 * 
	 * @param f
	 *            ArrayList containing the string representation of the texts
	 * @return ArrayList containing analyzed Text instances
	 */
	private ArrayList<Text> analyseTexts(ArrayList<String> f) {
		ArrayList<Text> texts = new ArrayList<Text>();

		for (int i = 0; i < f.size(); i++) {
			Text t = new Text(f.get(i));

			t.analyze();

			texts.add(t);
		}

		return texts;
	}

	/**
	 * Sorts the texts according to the number of matching occurrences
	 * 
	 * @param texts
	 *            unsorted ArrayList of texts
	 * @return sortedA ArrayList of texts according to occurrences
	 */
	private ArrayList<Text> sortTexts(ArrayList<Text> texts) {

		// Sort the texts amoungst them
		Collections.sort(texts, new Comparator<Text>() {
			public int compare(Text t1, Text t2) {
				return t2.getTotalOccurences() - t1.getTotalOccurences();
			}
		});

		// Sort the Names arrays in the texts according to their occurrences
		for (Text t : texts) {
			// Sort the person names
			if (t.getNames() != null) {
				Collections.sort(t.getNames(), new Comparator<Name>() {
					public int compare(Name n1, Name n2) {
						return n2.getOccurences() - n1.getOccurences();
					}
				});
			}

			// Sort the location names
			if (t.getLocations() != null) {
				Collections.sort(t.getLocations(), new Comparator<Name>() {
					public int compare(Name l1, Name l2) {
						return l2.getOccurences() - l1.getOccurences();
					}
				});
			}

			// Sort the organization names
			if (t.getOrganisations() != null) {
				Collections.sort(t.getOrganisations(), new Comparator<Name>() {
					public int compare(Name o1, Name o2) {
						return o2.getOccurences() - o1.getOccurences();
					}
				});
			}

		}

		return texts;
	}

	/**
	 * Remove the texts which do not have the given person name
	 * 
	 * @param texts
	 *            texts to look for the person name
	 * @param personName
	 *            String of the person name
	 * @return ArrayList containing texts in which the person name occurs
	 */
	private ArrayList<Text> filterPerson(ArrayList<Text> texts,
			String personName) {
		ArrayList<Text> filteredTexts = new ArrayList<Text>();

		for (int i = 0; i < texts.size(); i++) {
			// The text contains names
			if (texts.get(i).getNames() != null
					&& texts.get(i).getNames().size() > 0) {
				for (int j = 0; j < texts.get(i).getNames().size(); j++) {
					if (texts.get(i).getNames().get(j).getName()
							.contains(personName)) {
						filteredTexts.add(texts.get(i));
					}
				}
			}
		}

		return filteredTexts;
	}

	/**
	 * Remove the texts which do not have the given location name
	 * 
	 * @param texts
	 *            texts to look for the location name
	 * @param locationName
	 *            String of the location name
	 * @return ArrayList containing texts in which the location name occurs
	 */
	private ArrayList<Text> filterLocation(ArrayList<Text> texts,
			String locationName) {
		ArrayList<Text> filteredTexts = new ArrayList<Text>();

		for (int i = 0; i < texts.size(); i++) {
			// the text contains names
			if (texts.get(i).getLocations() != null
					&& texts.get(i).getLocations().size() > 0) {
				for (int j = 0; j < texts.get(i).getLocations().size(); j++) {
					if (texts.get(i).getLocations().get(j).getName()
							.contains(locationName)) {
						filteredTexts.add(texts.get(i));
					}
				}
			}
		}

		return filteredTexts;
	}

	/**
	 * Remove the texts which do not have the given organization name
	 * 
	 * @param texts
	 *            texts to look for the organization name
	 * @param locationName
	 *            String of the organization name
	 * @return ArrayList containing texts in which the organization name occurs
	 */
	private ArrayList<Text> filterOrganisation(ArrayList<Text> texts,
			String organisationName) {
		ArrayList<Text> filteredTexts = new ArrayList<Text>();

		for (int i = 0; i < texts.size(); i++) {
			// the text contains names
			if (texts.get(i).getOrganisations() != null
					&& texts.get(i).getOrganisations().size() > 0) {
				for (int j = 0; j < texts.get(i).getOrganisations().size(); j++) {
					if (texts.get(i).getOrganisations().get(j).getName()
							.contains(organisationName)) {
						filteredTexts.add(texts.get(i));
					}
				}
			}
		}

		return filteredTexts;
	}

	/**
	 * Removes the duplicate texts out of a list
	 * 
	 * @param texts
	 *            ArrayList containing the texts which needs to be deduplicated
	 * @return an ArrayList containing no duplicate Texts
	 */
	private ArrayList<Text> removeDuplicates(ArrayList<Text> texts) {
		ArrayList<Text> deduplicatedtexts = new ArrayList<Text>();

		// Abuse the HashSet properties to deduplicated
		// HashSet cannot contain duplicate elements
		HashSet<Text> hs = new HashSet<Text>();
		hs.addAll(texts);
		deduplicatedtexts.addAll(hs);

		return deduplicatedtexts;
	}

}
