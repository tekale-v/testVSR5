/*
 * Added by APOLLO Team
 * For Characteristics Related Web services
 */

package com.png.apollo.characteristics;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("PGCharacteristicModeler")
public class PGCharacteristicModeler extends ModelerBase{

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {CharacteristicServices.class};
	}
}
