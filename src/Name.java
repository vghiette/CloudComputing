
public class Name {

	private String name;
	private int occurrences;
	
	public Name(String name)
	{
		this.name = name;
	}
	
	public void addOccurence(){
		occurrences++;
	}
	
	public int getOccurences(){
		return occurrences;
	}
	
	public String getName(){
		return name;
	}
}
