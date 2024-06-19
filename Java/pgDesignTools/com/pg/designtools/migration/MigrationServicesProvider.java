package com.pg.designtools.migration;

import com.pg.designtools.migration.impl.ProductDTAnalyzer;
import com.pg.designtools.migration.impl.ProductDTMigration;

public class MigrationServicesProvider {
	
	public IDTMigration getAnalyzer(String strModeOfTitle) {
		return new ProductDTAnalyzer(strModeOfTitle);
	}
	
	public IDTMigration getMigrationMaker(String strModeOfTitle) {
		return new ProductDTMigration(strModeOfTitle);
	}
}
