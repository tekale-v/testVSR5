package com.pg.dsm.ui;

public class PdfConstants {
	public enum Basic {
        EXTRACT_ANCHOR_TAG_DATA("<a[^>]*>(.*?)</a>");
		private final String name;
        Basic(String name) {
            this.name = name;
        }
        public String get() {
            return this.name;
        }
    }
}
