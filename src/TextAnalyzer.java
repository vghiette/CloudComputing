import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// Used to scan through the directories



public class TextAnalyzer {

	public static void main(String[] args)
	{
		File files[] = finder("testTexts");
		
		for(int i = 0; i < 5; i++){
			Text t = new Text(files[i].toString());
			
			t.analyze();
			
			ArrayList<Name> locs = t.getlocation();
			ArrayList<Name> pers = t.getNames();
			
			System.out.println(files[i].toString());
			
			System.out.println();
			
			if(locs != null){
				for(Name l : locs){
					System.out.println(l.getName() + " : " + l.getOccurences());
				}
			}
			
			System.out.println();
			
			if(pers != null)
			{
				for(Name p : pers){
					System.out.println(p.getName() + " : " + p.getOccurences());
				}
			}
			
			System.out.println();
			System.out.println();
		}
		
		
		
		
	}

	public static File[] finder( String dirName)
{
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() { 
		         public boolean accept(File dir, String filename)
		              { return filename.endsWith(".txt"); }
		} );

	}
	
	public static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	
}


