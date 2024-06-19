package com.pg.pdf.bean.output.xml;

import java.util.List;

import matrix.util.StringList;

public class OutPutXMLSections {

		protected String section;
		protected List<OutPutFieldDetails> fieldDetails;
		protected StringList slSelectableList;
		protected StringList slRelSelectableList;
		public StringList getSlRelSelectableList() {
			return slRelSelectableList;
		}
		public void setSlRelSelectableList(StringList slRelSelectableList) {
			this.slRelSelectableList = slRelSelectableList;
		}
		public StringList getSlSelectableList() {
			return slSelectableList;
		}
		public void setSlSelectableList(StringList slSelectableList) {
			this.slSelectableList = slSelectableList;
		}
		public String getSection() {
			return section;
		}
		public void setSection(String section) {
			this.section = section;
		}
		public List<OutPutFieldDetails> getFieldDetails() {
			return fieldDetails;
		}
		public void setFieldDetails(List<OutPutFieldDetails> fieldDetails) {
			this.fieldDetails = fieldDetails;
		}
		
	}



