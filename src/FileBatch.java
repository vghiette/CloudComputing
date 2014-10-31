import java.util.ArrayList;
import java.util.List;


public class FileBatch {
	public List<FileLocation> batch;
	
	public FileBatch(List<FileLocation> b)
	{
		batch = b;
	}
	
	public FileBatch(String batchString)
	{
		batchString = batchString.trim();
		batchString = batchString.replace("[",  "");
		batchString = batchString.replace("]",  "");
		
		String[] batchStrings = batchString.split(",");
		
		for(int i = 0; i < batchStrings.length; i++)
		{
			batch.add(new FileLocation(batchStrings[i]));
		}		
	}
	
	public String toString()
	{
		String str = "[";
		
		for(int i = 0; i < batch.size(); i++){
			if(i < batch.size() -1)
			{
				str += batch.get(i).toString() +  ",";
			}
			else
			{
				str += batch.get(i).toString();
			}
		}
		
		str += "]";
		
		return str;
	}
}
