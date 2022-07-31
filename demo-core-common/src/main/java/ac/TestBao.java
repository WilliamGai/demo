package ac;

import java.util.Collection;
import java.util.TreeMap;

import ac.ahocorasick.trie.Emit;
import ac.ahocorasick.trie.Token;
import ac.ahocorasick.trie.Trie;
/***
 * 关键词过滤
 */
public class TestBao {
    public static void main(String args[]){
        System.out.println("start");
//        test0();
//        test1();
//        test2();
        test3();
//        test4();
//        test5();
        TreeMap<String,String> map9 = new TreeMap<>();
        TreeMap<String,String> map8 = new TreeMap<>();
        TreeMap<String,String> map7 = new TreeMap<>();
        TreeMap<String,String> map6 = new TreeMap<>();
        TreeMap<String,String> map5 = new TreeMap<>();
        TreeMap<String,String> map4 = new TreeMap<>();
        TreeMap<String,String> map3 = new TreeMap<>();
        TreeMap<String,String> map2 = new TreeMap<>();
        TreeMap<String,String> map = new TreeMap<>();
        String plt = "冀A95486675";
        map9.put("冀A95486675", plt);
        map8.put("A95486675", plt);
        map7.put("95486675", plt);
        map6.put("5486675", plt);
        map5.put("486675", plt);
        map4.put("86675", plt);
        map3.put("6675", plt);
        map2.put("675", plt);
        map.put("75", plt);
        
//        map3.
    }
    //
    public static void test0(){
         Trie trie = new Trie();
            trie.addKeyword("a");
            trie.addKeyword("b");
            trie.addKeyword("c");
            trie.addKeyword("d");
            Collection<Emit> emits = trie.parseText("aabbccdd");
        System.out.println(emits);
    }
    //[2:3=he, 1:3=she, 2:5=hers]
    public static void test1(){
        Trie trie = new Trie();
        trie.addKeyword("hers");
        trie.addKeyword("his");
        trie.addKeyword("she");
        trie.addKeyword("he");
        Collection<Emit> emits = trie.parseText("ushers");
        System.out.println(emits);
    }
    //[0:12=hot chocolate]
    public static void test2(){
         Trie trie = new Trie().removeOverlaps();
         trie.addKeyword("hot");
         trie.addKeyword("hot chocolate");
         Collection<Emit> emits = trie.parseText("hot chocolate");
        System.out.println(emits);
    }
    //[20:24=sugar]   匹配全文
    public static void test3(){
        Trie trie = new Trie().onlyWholeWords();
        trie.addKeyword("sugar");
        Collection<Emit> emits = trie.parseText("sugarcane sugarcane sugar canesugar");
        System.out.println(emits);
    }
    //[0:5=casing]
    public static void test4(){
        Trie trie = new Trie().caseInsensitive();
        trie.addKeyword("casing");
        Collection<Emit> emits = trie.parseText("CaSiNg");
        System.out.println(emits);
    }
    //所有关键字进行替换或标记
    public static void test5(){
        String speech = "The Answer to the Great Question... Of Life, " +
                "the Universe and Everything... Is... Forty-two,' said " +
                "Deep Thought, with infinite majesty and calm.";
        Trie trie = new Trie().removeOverlaps().onlyWholeWords().caseInsensitive();
        trie.addKeyword("great question");
        trie.addKeyword("forty-two");
        trie.addKeyword("deep thought");
        Collection<Token> tokens = trie.tokenize(speech);
        StringBuffer html = new StringBuffer();
        html.append("<html><body><p>");
        for (Token token : tokens) {
            if (token.isMatch()) {
                html.append("<i>");
            }
            html.append(token.getFragment());
            if (token.isMatch()) {
                html.append("</i>");
            }
        }
        html.append("</p></body></html>");
        System.out.println(html);

    }
}
