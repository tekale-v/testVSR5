package com.pg.orchestration;
import java.util.List;
public class PgPmpRequestData{
	    
		private List<PgPoaPmpRequestData> POA;
	    
	    public void setPOA(List<PgPoaPmpRequestData> POA){
	        this.POA = POA;
	    }
	    public List<PgPoaPmpRequestData> getPOA(){
	        return this.POA;
	    }
}



