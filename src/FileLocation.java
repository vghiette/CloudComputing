
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
		loc = loc.replaceAll("[", "");
		loc = loc.replaceAll("]", "");
		
		String[] strs = loc.split(";"); 
		
		bucket = strs[0];
		key = strs[1];
	}
	
	public String toString()
	{
		return "[" + bucket + ";" + key + "]";
	}
	
}
