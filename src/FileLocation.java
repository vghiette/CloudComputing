
public class FileLocation {
	String bucket;
	String key;
	
	public FileLocation(String b, String k)
	{
		bucket = b;
		key = k;
	}
	
	public FileLocation(String loc)
	{
		loc = loc.trim();
		loc = loc.replace("[", "");
		loc = loc.replace("]", "");
		
		String[] strs = loc.split(";"); 
		
		bucket = strs[0];
		key = strs[1];
	}
	
	public String toString()
	{
		return "[" + bucket + ";" + key + "]";
	}
	
}
