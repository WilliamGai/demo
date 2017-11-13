package com.sincetimes.website.app.search;

public class SearchTemplate {
//	private Trie trie;
	// trie.addKeyword("sugar");
	private SearchTemplate() {
	}

	private static class InstanceHolder {
		public static SearchTemplate instance = new SearchTemplate();
	}

	public static SearchTemplate getInstance() {
		return InstanceHolder.instance;
	}
}
