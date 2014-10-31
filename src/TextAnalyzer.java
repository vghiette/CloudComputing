import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

// Used to scan through the directories



public class TextAnalyzer {
	
	static int maxTexts = 50;
	ArrayList<String> files;
	
	public TextAnalyzer(ArrayList<String> f)
	{
		files = f;
		// Maybe do something
	}
	
	// GEt the relevant texts, this method is to be called
	public ArrayList<Text> getRelevantTexts(String personName, String locationName, String organisationName)
	{	
		// Analyze all the texts
		ArrayList<Text> texts = analyseTexts(files);
		
		// Filter the texts
		ArrayList<Text> filteredTexts = new ArrayList<Text>();
		
		//This is and or Filter
		if(!personName.isEmpty())
		{
			filteredTexts.addAll(filterPerson(texts, personName));
		}
		if(!locationName.isEmpty())
		{
			filteredTexts.addAll(filterLocation(texts, locationName));
		}
		if(!organisationName.isEmpty())
		{
			filteredTexts.addAll(filterOrganisation(texts, organisationName));
		}
		
		ArrayList<Text> deduplicatedTexts = new ArrayList<Text>();
		
		// Deduplicate the texts
		if(filteredTexts.size() > 0)
		{
			deduplicatedTexts.addAll(removeDuplicates(filteredTexts));
		}
		
		for(Text t: deduplicatedTexts)
		{
			
			t.calculateTotalOccurrences(personName, locationName, organisationName);
		}
		
		ArrayList<Text> sortedtexts = sortTexts(deduplicatedTexts);
		
		return sortedtexts;
	}
	
	// Dies the text analysis
	private ArrayList<Text> analyseTexts(ArrayList<String> f)
	{
		ArrayList<Text> texts = new ArrayList<Text>();
		
		for(int i = 0; i < f.size(); i++){
			Text t = new Text(f.get(i));
			
			t.analyze();
			
			texts.add(t);
		}
		
		return texts;
	}
	
	// Sort the texts on the most relevant
	private ArrayList<Text> sortTexts(ArrayList<Text> texts){		
		
		//Sort the texts amoungst them
		Collections.sort(texts, new Comparator<Text>(){
		    public int compare(Text t1, Text t2) {
		        return t2.getTotalOccurences() - t1.getTotalOccurences();
		    }
		});
		
		//Sort the Names arrays in the texts according to their occurrences
		for(Text t: texts)
		{
			// Sort the person names
			if(t.getNames() != null)
			{
				Collections.sort(t.getNames(), new Comparator<Name>(){
				    public int compare(Name n1, Name n2) {
				        return n2.getOccurences() - n1.getOccurences();
				    }
				});
			}
			
			// Sort the location names
			if(t.getLocations() != null)
			{
				Collections.sort(t.getLocations(), new Comparator<Name>(){
				    public int compare(Name l1, Name l2) {
				        return l2.getOccurences() - l1.getOccurences();
				    }
				});
			}
			
			
			// Sort the organisation names
			if(t.getOrganisations() != null)
			{
				Collections.sort(t.getOrganisations(), new Comparator<Name>(){
				    public int compare(Name o1, Name o2) {
				        return o2.getOccurences() - o1.getOccurences();
				    }
				});
			}
			
		}
		
		return texts;
		
	}
	
	// Filter the names on the person names
	private ArrayList<Text> filterPerson(ArrayList<Text> texts, String personName)
	{
		ArrayList<Text> filteredTexts = new ArrayList<Text>();
		
		for(int i = 0; i < texts.size(); i++)
		{
			// the text contains names
			if(texts.get(i).getNames() != null && texts.get(i).getNames().size() > 0)
			{
				for(int j = 0; j < texts.get(i).getNames().size(); j++)
				{
					if( texts.get(i).getNames().get(j).getName().contains(personName))
					{
						filteredTexts.add(texts.get(i));
					}
				}
			}
		}
		
		return filteredTexts;
	}
	
	// Filter the names on the location
	private ArrayList<Text> filterLocation(ArrayList<Text> texts, String locationName)
	{
		ArrayList<Text> filteredTexts = new ArrayList<Text>();
		
		for(int i = 0; i < texts.size(); i++)
		{
			// the text contains names
			if(texts.get(i).getLocations() != null && texts.get(i).getLocations().size() > 0)
			{
				for(int j = 0; j < texts.get(i).getLocations().size(); j++)
				{
					if( texts.get(i).getLocations().get(j).getName().contains(locationName))
					{
						filteredTexts.add(texts.get(i));
					}
				}
			}
		}
		
		return filteredTexts;
	}
	
	// Filter the names on organisation
	private ArrayList<Text> filterOrganisation(ArrayList<Text> texts, String organisationName)
	{
		ArrayList<Text> filteredTexts = new ArrayList<Text>();
		
		for(int i = 0; i < texts.size(); i++)
		{
			// the text contains names
			if(texts.get(i).getOrganisations() != null && texts.get(i).getOrganisations().size() > 0)
			{
				for(int j = 0; j < texts.get(i).getOrganisations().size(); j++)
				{
					if( texts.get(i).getOrganisations().get(j).getName().contains(organisationName))
					{
						filteredTexts.add(texts.get(i));
					}
				}
			}
		}
		
		return filteredTexts;
	}
	
	// Removes duplicates from name ArrayLists
	private ArrayList<Text> removeDuplicates(ArrayList<Text> texts)
	{
		ArrayList<Text> deduplicatedtexts = new ArrayList<Text>();

		HashSet<Text> hs = new HashSet<Text>();
		hs.addAll(texts);
		deduplicatedtexts.addAll(hs);
		
		return deduplicatedtexts;
	}
	
	// Get the files to be analyzed
	private File[] finder( String dirName)
	{
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() { 
		         public boolean accept(File dir, String filename)
		              { return filename.endsWith(".txt"); }
		} );

	}

	
	
}


