package ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class TestBao2 {
	public static void main(String args[]){
		System.err.println("start");
		test1();
		
		System.out.println("start");
		String csn = Charset.defaultCharset().name();
		System.out.println(csn);
		BufferedReader br;
		try {
			File file = new File("dirty-word.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				
				
//			   System.out.println(new String(line.getBytes("utf-8"),"utf-8"));
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
	//[2:3=he, 1:3=she, 2:5=hers]
	public static void test1(){
	    System.out.println(Character.isAlphabetic('a'));
	    System.out.println(Character.isAlphabetic(' '));
	    System.out.println(Character.isAlphabetic('1'));
	    System.out.println(Character.isAlphabetic('.'));
	    System.out.println(Character.isAlphabetic('#'));
	    System.out.println(Character.isAlphabetic('Ｂ'));
	}
}
