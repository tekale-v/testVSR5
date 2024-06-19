package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.pdfview.UI.TableUI;
import com.pdfview.impl.FPP.Plant;
import com.pdfview.impl.FPP.Plants;

import matrix.db.Context;
import matrix.db.JPO;

public class GetPlant {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetPlant(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Method to retrieve the Plant table
	 * 
	 * @return throws exception
	 */

	public Plants getComponent() {
		Plants plants = new Plants();
		List<Plant> lsPlant = plants.getPlant();
		try {

			Map Argmap = new HashMap();
			Map mapObject = new HashMap();
			Argmap.put("objectId", _OID);
			String[] argsFPP = JPO.packArgs(Argmap);
			MapList mlPlants = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "pgIPMProductData", "getPlants",
					argsFPP);

			String strPlantName = DomainConstants.EMPTY_STRING;
			String strAuthorisedToUse = DomainConstants.EMPTY_STRING;
			String strAuthorizedToProduce = DomainConstants.EMPTY_STRING;
			String activated = DomainConstants.EMPTY_STRING;
			String strAuthorised = DomainConstants.EMPTY_STRING;
			MapList mlPlantsList = TableUI.executeTable(_context, _OID, "pgDSOPlantDataSummary", mlPlants);

			if (mlPlantsList != null && !mlPlantsList.isEmpty()) {
				int mlPlantsListSize = mlPlantsList.size();
				for (int i = 0; i < mlPlantsListSize; i++) {
					mapObject = (Map) mlPlantsList.get(i);
					strPlantName = (String) mapObject.get("name");
					strAuthorisedToUse = (String) mapObject.get("Authorized to Use");
					strAuthorizedToProduce = (String) mapObject.get("Authorized to Produce");
					activated = (String) mapObject.get("Activated");
					strAuthorised = (String) mapObject.get("Authorized");
					Plant plant = new Plant();
					plant.setPlantss(strPlantName);
					plant.setPlantsAuthorizedToUse(strAuthorisedToUse);
					plant.setPlantsAuthorizedtoProduce(strAuthorizedToProduce);
					plant.setActivated(StringHelper.validateString1(activated));
					plant.setAuthorizedactivated(strAuthorised);
					lsPlant.add(plant);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plants;
	}
}
