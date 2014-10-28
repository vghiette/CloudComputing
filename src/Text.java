import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class Text {

	String path;
	String text;
	Boolean analyzed = false;
	ArrayList<Name> names;
	ArrayList<Name> locations;
	
	public Text(String path) {
		this.path = path;
		
		try {
			this.text = readFile(path, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public void analyze(){
		String tokens[];
		
		try {
			tokens = tokenize(text);
			names = findNames(tokens);
			locations = findLocations(tokens);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	private ArrayList<Name> findNames(String[] tokens) throws IOException {
		InputStream is = new FileInputStream("models/en-ner-person.bin");
	 
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
	 
		NameFinderME nameFinder = new NameFinderME(model);
	 
		Span nameSpans[] = nameFinder.find(tokens);
		
		if(nameSpans.length == 0){
			return null;
		}
		
		names = new ArrayList<Name>();
		
		for(int i = 0; i < nameSpans.length; i++)
		{
			String tempName = "";
			
			for(int j = nameSpans[i].getStart(); j< nameSpans[i].getEnd(); j++){
				tempName +=  tokens[j] + " ";
			}
			
			addName(tempName, names);
		}
		
		return names;
	}
	
	// Add a name entry to the corresponding arraylist
	private void addName(String name, ArrayList<Name> list){
		name = name.trim();
		name = name.toLowerCase();
		
		//Check if name is already in the names list
		int index = findName(name, list);
		if (index > -1){
			list.get(index).addOccurence();
		}
		else {
			Name tempName = new Name(name);
			tempName.addOccurence();
			list.add(tempName);
		}
	}
	
	// Returns the index of the Name corresponding to the given string, if not in array then return -1
	private int findName(String name, ArrayList<Name> list){
		int index = -1;
		
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getName().equals(name)){
				index = i;
			}
		}
		
		return index;
		
	}
	
	public ArrayList<Name> getNames(){
		return names;
	}
	
	public ArrayList<Name> getLocations(){
		return locations;
	}
	
	private ArrayList<Name> findLocations(String[] tokens) throws IOException {
		InputStream is = new FileInputStream("models/en-ner-location.bin");
	 
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
	 
		NameFinderME locationFinder = new NameFinderME(model);
	 
		Span locationSpans[] = locationFinder.find(tokens);
 
		if(locationSpans.length == 0){
			return null;
		}
		
		locations = new ArrayList<Name>();
		
		for(int i = 0; i < locationSpans.length; i++)
		{
			String tempName = "";
			
			for(int j = locationSpans[i].getStart(); j< locationSpans[i].getEnd(); j++){
				tempName +=  tokens[j] + " ";
			}
			
			addName(tempName, locations);
		}
		
		return locations;
						
	}
	
	private String[] tokenize(String text) throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("models/en-token.bin");
	 
		TokenizerModel model = new TokenizerModel(is);
	 
		Tokenizer tokenizer = new TokenizerME(model);
	 
		String tokens[] = tokenizer.tokenize(text);
	 
		is.close();
		
		return tokens;
	}
	
}
