import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;

// Used to scan through the directories



public class TextAnalyzer {
	
	static int maxTexts = 3;
	
	public TextAnalyzer()
	{
		// Maybe do something
	}
	
	// GEt the relevant texts, this method is to be called
	public ArrayList<Text> getRelevantTexts(String personName, String locationName, String organisationName)
	{
		// Get the files from the directory
		File files[] = finder("testTexts");
		
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
		
		return deduplicatedTexts;
	}
	
	// Dies the text analysis
	private ArrayList<Text> analyseTexts(File[] files)
	{
		ArrayList<Text> texts = new ArrayList<Text>();
		
		for(int i = 0; i < maxTexts; i++){
			Text t = new Text(files[i].toString());
			
			t.analyze();
			
			texts.add(t);
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


