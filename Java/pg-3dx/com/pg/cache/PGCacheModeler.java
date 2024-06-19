package com.pg.cache;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/pgcache")
public class PGCacheModeler extends ModelerBase {
	public Class<?>[] getServices() {
		return new Class[] { PGCacheService.class };
	}
}
