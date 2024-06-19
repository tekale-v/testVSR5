package com.pg.orchestration;
import java.util.List;
public class PgPoaRequestData {
	
	    private String initiativeName;

	    private String initiativeID;
	    
	    private String iLLeaderId;
	    
	    private String ownerId;

	    private List<PgPoaRequestPoaData> POA;
	    
	    
	    public PgPoaRequestData() {
			super();
		}

	    public void setInitiativeName(String initiativeName){
	        this.initiativeName = initiativeName;
	    }
	    public String getInitiativeName(){
	        return this.initiativeName;
	    }
	    public void setInitiativeID(String initiativeID){
	        this.initiativeID = initiativeID;
	    }
	    public String getInitiativeID(){
	        return this.initiativeID;
	    }
		//Start modified for 22x changes - Matching the method name with keys from request body
	    public void setiLLeaderId(String iLLeaderId){
	        this.iLLeaderId = iLLeaderId;
	    }
	    public String getiLLeaderId(){
	        return this.iLLeaderId;
	    }
		//End modified for 22x changes - Matching the method name with keys from request body
	    
	    public void setOwnerId(String ownerId){
	        this.ownerId = ownerId;
	    }
	    public String getOwnerId(){
	        return this.ownerId;
	    }
	    public void setPOA(List<PgPoaRequestPoaData> POA){
	        this.POA = POA;
	    }
	    public List<PgPoaRequestPoaData> getPOA(){
	        return this.POA;
	    }
}


