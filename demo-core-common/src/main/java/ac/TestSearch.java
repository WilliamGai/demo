package ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class TestSearch {
	public static void main(String args[]){
		System.err.println("start");

		String csn = Charset.defaultCharset().name();
		System.out.println(csn);
		BufferedReader br;
		try {
			File file = new File("dirty-word.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				
			   System.out.println(line);
			   int len = line.length();
			   for (int i = 0; i < line.length(); i++) {
				   char c = line.charAt(i);
				   if(c=='\n'||c=='\t'||c==' '){
//					   System.out.println("cccccccc!!!!!!!"+c);
					   continue;
				   }
//				   System.out.print((int)c);
				   System.out.print(c+"="+Character.isAlphabetic(c));
			   }
			   System.out.println();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
