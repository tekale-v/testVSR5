package com.pg.pdf.bean.output.xml;

import java.util.List;

public class OutPutXML {

		protected List<OutPutXMLSections> sectionDetails;
		protected List<OutPutXMLTableSections> tableSections;
		public List<OutPutXMLTableSections> getTableSections() {
			return tableSections;
		}

		public void setTableSections(List<OutPutXMLTableSections> tableSections) {
			this.tableSections = tableSections;
		}

		protected String sectionName;
		public List<OutPutXMLSections> getSectionDetails() {
			return sectionDetails;
		}

		public void setSectionDetails(List<OutPutXMLSections> sectionDetails) {
			this.sectionDetails = sectionDetails;
		}

		public String getSectionName() {
			return sectionName;
		}

		public void setSectionName(String sectionName) {
			this.sectionName = sectionName;
		}

	}


