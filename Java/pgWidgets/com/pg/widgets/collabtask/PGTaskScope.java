package com.pg.widgets.collabtask;

import java.util.ArrayList;
import java.util.List;

import com.dassault_systemes.enovia.e6wv2.foundation.FoundationException;
import com.dassault_systemes.enovia.e6wv2.foundation.ServiceConstants;
import com.dassault_systemes.enovia.e6wv2.foundation.db.ObjectUtil;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Datacollection;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Dataobject;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.ExpandData;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Selectable;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;

public interface PGTaskScope extends ServiceConstants {
	
	static final String SYMBOLIC_RELATIONSHIP_CONTRIBUTESTO = "relationship_ContributesTo";
	static final String RELATIONSHIP_CONTRIBUTESTO = PropertyUtil.getSchemaProperty(null, SYMBOLIC_RELATIONSHIP_CONTRIBUTESTO);
	static final String OWNED = "owned";
	static final String ASSIGNED = "assigned";
	static final String ALL = "all";
	static final String OWNER = "owner == \"context.user\"";
	static final String SELECTABLE = "(current != \"Create\" && to[Assigned Tasks].from.name == \"context.user\")";
	 /*
	 * @param context
	 * @param
	 * @returns the collection of all the tasks for which the context user is the contributor
	 * the method is used to get the Tasks for the context user based upon the selectable list
	 * @throws Exception
	 * */
	public static Datacollection getScopeTasks(Context paramContext, String paramString1, List<Selectable> paramList1, List<Selectable> paramList2, String paramString2, String paramString3, String paramString4)
		    throws FoundationException
		  {
		    String str = paramString2;
		    if (str == null) {
		      str = "";
		    }
		    if (!str.isEmpty()) {
		      str = str + " && ";
		    } 
		    str = str + buildWhereClauseBasedOnFilter(paramString4);
		   
		    Dataobject localDataobject = new Dataobject();
		    localDataobject.setId(paramString1);
		    ExpandData localExpandData = new ExpandData();
		    localExpandData.setTypePattern(DomainConstants.TYPE_TASK);
		    localExpandData.setRelationshipPattern(RELATIONSHIP_CONTRIBUTESTO);
		    localExpandData.setGetFrom(Boolean.valueOf(true));
		    localExpandData.setObjectWhere(str);
		    localExpandData.setRelationshipWhere(paramString3);
		    
		    ArrayList<Selectable> localArrayList = new ArrayList<Selectable>();
		    localArrayList.addAll(paramList1);
		    localArrayList.addAll(paramList2);
		    return ObjectUtil.expand(paramContext, localDataobject, localExpandData, localArrayList);
		  }
	
	
	  public static String buildWhereClauseBasedOnFilter(String paramString) {
		    int i = 1;
		    int j = 1;
		    String str = "";
		    if (ALL.equals(paramString)) {
		      return str;
		    }
		    if (OWNED.equals(paramString)) {
		      j = 0;
		    } else if (ASSIGNED.equals(paramString)) {
		      i = 0;
		    }
		    if (i != 0) {
		      str = str + OWNER;
		    }
		    if (j != 0)
		    {
		      if (!str.isEmpty()) {
		        str = str + " || ";
		      }
		      str = str + SELECTABLE;
		    }
		    return str;
	}


	public static Datacollection getScopeTasks(Context paramContext, String paramString1, List<Selectable> paramList, String paramString2)
			    throws FoundationException
			  {
			    return getScopeTasks(paramContext, paramString1, paramList, new ArrayList<Selectable>(), null, null, paramString2);
			  }


}
