package com.sincetimes.website.app.search;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ac.ahocorasick.trie.Emit;
import ac.ahocorasick.trie.Trie;
public class SearchToolnstance{
	private static final Map<String, SearchToolnstance> instances = new ConcurrentHashMap<>();
	
	private Trie trie;
	public static SearchToolnstance inst(String name){
		return instances.computeIfAbsent(name, (k)->new SearchToolnstance());
	}
	public void init(Collection<String> words){
		 trie = new Trie().onlyWholeWords();
		 words.parallelStream().forEach(trie::addKeyword);
	}
	public void searchSingleWord(){
		 Collection<Emit> emits = trie.parseText("sugarcane sugarcane sugar canesugar");
	}
}
