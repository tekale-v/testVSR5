package com.pg.pdf.bean.output.xml;



public class OutPutFieldDetails {
	protected String fieldSelectable;
	protected String fieldValue;
	protected String fieldName;
	protected String pdfViewDisplayMethod;
	protected String fieldId;
	
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	protected boolean usePushContext;
	protected String usePushContextOnWhichView;
	protected String usePushContextSelectValue;
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
	
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getPdfViewDisplayMethod() {
		return pdfViewDisplayMethod;
	}
	public void setPdfViewDisplayMethod(String pdfViewDisplayMethod) {
		this.pdfViewDisplayMethod = pdfViewDisplayMethod;
	}
	public String getFieldSelectable() {
		return fieldSelectable;
	}
	public void setFieldSelectable(String fieldSelectable) {
		this.fieldSelectable = fieldSelectable;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	public boolean isUsePushContext() {
		return usePushContext;
	}

	public void setUsePushContext(boolean usePushContext) {
		this.usePushContext = usePushContext;
	}

	public String getUsePushContextOnWhichView() {
		return usePushContextOnWhichView;
	}
       
	public void setUsePushContextOnWhichView(String usePushContextOnWhichView) {
		this.usePushContextOnWhichView = usePushContextOnWhichView;
	}

	public String getUsePushContextSelectValue() {
		return usePushContextSelectValue;
	}
	
	public void setUsePushContextSelectValue(String usePushContextSelectValue) {
		this.usePushContextSelectValue = usePushContextSelectValue;
	}
	//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
}
