package com.sincetimes.website.app.search;

import ac.ahocorasick.trie.Trie;

public class SearchTemplate {
	private Trie trie;
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
