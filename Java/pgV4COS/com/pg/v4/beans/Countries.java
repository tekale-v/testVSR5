package com.pg.v4.beans;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Countries  implements Serializable {
	 private List<CountryClearance> lCountryclearance = new ArrayList();
	 
    public List<CountryClearance> getCountryclearance() {
    	 if (lCountryclearance == null) {
    		 lCountryclearance = new ArrayList<CountryClearance>();
         }
         return this.lCountryclearance;
	}


    public void addCountryClearance(CountryClearance countryclearanceObj) {
    	lCountryclearance.add(countryclearanceObj);
    }
}