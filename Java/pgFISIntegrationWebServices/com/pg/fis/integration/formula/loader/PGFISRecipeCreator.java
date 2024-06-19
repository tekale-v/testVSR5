package com.pg.fis.integration.formula.loader;

import com.pg.fis.integration.formula.loader.actions.*;
import com.pg.fis.integration.util.PGFISCommonUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.HttpCallResponse;
import com.pg.fis.integration.util.PGFISCASAuthenticator;
import com.google.gson.*;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class PGFISRecipeCreator extends PGFISDataCreator {

	
	private static final Logger logger = Logger.getLogger("PGFISRecipeCreator");
	private static Map< String, Map.Entry< String, String > > singlePieceDescriptionCacheMap = new HashMap<> ();
	private static Map< String, Map.Entry< String, String > > recipeClassificationCacheMap = new HashMap<> ();
	
	static {
		try {
			PGFISRecipeCreator recipeCreator = new PGFISRecipeCreator();
			PGFISCASAuthenticator casAuthenticator = recipeCreator.getCasAuthenticator();
			PGFISSinglePieceDescriptionsAction spdfa = new PGFISSinglePieceDescriptionsAction();
	    	spdfa.doDataAction(casAuthenticator, null);
	    	singlePieceDescriptionCacheMap = spdfa.getFunctionsMap();
	    
	    	PGFISRecipeClassificationsAction recipeClassificationAction = new PGFISRecipeClassificationsAction();
	    	recipeClassificationAction.doDataAction(casAuthenticator, null);
	    	recipeClassificationCacheMap = recipeClassificationAction.getFunctionsMap();
		} catch(Exception ex) {
			logger.info("Error while initializing Single Piece Description and Recipe Classifiction");
		}
	}
	/**
	 * Iterate through the mixture ratio of each material json and replace the ID of
	 * each substance with the ID specified in the mapping file for the specific
	 * tenant
	 *
	 * @param recipeJson
	 * @param idMapping
	 * @throws Exception 
	 */
	private void updateIngredients(JsonObject recipeJson) throws Exception {

		// We will need to fix childSections -> childElements -> hasIngredient to fix
		// order of addition
		// Then iterate into hasMaterial to fix the ID UUID of "resourceId"
		
        
        if(null != recipeJson && recipeJson.has("recipe")) {
        	JsonObject jsonReceipe = recipeJson.get("recipe").getAsJsonObject();
    		if (jsonReceipe.has("singlePieceDescription")) {
    			JsonObject jsonSPD = jsonReceipe.get("singlePieceDescription").getAsJsonObject();
    			String strSPD = DomainConstants.EMPTY_STRING;
    			if(jsonSPD.has("prefLabel")) {
    				strSPD = jsonSPD.get("prefLabel").getAsString();
    			} else if(jsonSPD.has("id")) {
    				strSPD = jsonSPD.get("id").getAsString();
    			}
    				
    			
    			if (UIUtil.isNotNullAndNotEmpty(strSPD) && (null!=singlePieceDescriptionCacheMap && singlePieceDescriptionCacheMap.containsKey(strSPD.toLowerCase()))) {
    				JsonObject obj = new JsonObject();
    				obj.addProperty("id", singlePieceDescriptionCacheMap.get(strSPD.toLowerCase()).getKey());
    				obj.addProperty("prefLabel", strSPD);
    				jsonReceipe.remove("singlePieceDescription");
    				jsonReceipe.add("singlePieceDescription", obj);
    			}
    			else {
                    logger.warning( "ID " + strSPD + " was not available" );
                    addError( "singlePieceDescription: ID " + strSPD + " was not available");
                    logger.info("singlePieceDescription: ID " + strSPD + " was not available");
    			}
    		}
    		
    		if(jsonReceipe.has("hasRecipeClassifications")) {
    			JsonArray recipeClassArray = jsonReceipe.get("hasRecipeClassifications").getAsJsonArray();
    			for(JsonElement el: recipeClassArray) {
    				JsonObject recipeClassificationObj = el.getAsJsonObject();
    				if(null != recipeClassificationObj && recipeClassificationObj.has("prefLabel")) {
    					String recipClassName = recipeClassificationObj.getAsJsonObject().get( "prefLabel" ).getAsString();
    					 if(null != recipeClassificationCacheMap && recipeClassificationCacheMap.containsKey(recipClassName.toLowerCase())) {
    						 recipeClassificationObj.addProperty("id", recipeClassificationCacheMap.get(recipClassName.toLowerCase()).getKey());
							 recipeClassificationObj.remove("prefLabel");
    					} else {
    						 logger.warning( "ID " + recipClassName + " was not available" );
    		                 addError( "RecipeClassification: ID " + recipClassName + " was not available");
    		                 logger.info("RecipeClassification: ID " + recipClassName + " was not available");
    					 }
    				}
    				if(null !=recipeClassificationObj && recipeClassificationObj.has("isActive")) {
    					recipeClassificationObj.remove("isActive");
    				}
    			}
    		}
        }
		
	}
	
	@SuppressWarnings("deprecation")
	public String loadOnPremFormulas(String onpremFormulaJSON, String formulaPhysicalID, PGFISCASAuthenticator casAuthenticator) throws Exception {
		String returnMessage = "";
		JsonObject formulaJSON = JsonParser.parseString(onpremFormulaJSON).getAsJsonObject();
		String importedIdentifier = "";
		if(formulaJSON.has("recipe")) {
			JsonObject jsonRecipe = formulaJSON.get("recipe").getAsJsonObject();
			if(jsonRecipe.has("importedIdentifier")) {
				importedIdentifier = jsonRecipe.get("importedIdentifier").getAsString();
			}
		}
		if(formulaJSON.has("error")) {
			returnMessage += "FAILED: Import Operation Failed.\n";
			returnMessage += "Selected Formula(or Consumed Intermediate Formula) have blank BOM Units Value or Invalid FBOM Structure in Enovia.\n";
			returnMessage += "Please review formula directly in Enovia and correct the data there before trying to import to FIS again.";
			return returnMessage;
		}
		updateIngredients(formulaJSON);
		logger.info("RecipeCreator Transformed JSON - " + formulaJSON);
		HttpCallResponse response = new PGFISRecipeCreateAction().doDataAction(getCasAuthenticator(),	formulaJSON);
		logger.info("RecipeCreator response" + response);
		logger.info("RecipeCreator response.getReponse()" + response.getReponse());
		
		if (response.getStatus() > 202) {
			
			logger.warning("\n\n\n\n\n");
			logger.warning("**********  FAILED ***********");
			logger.warning(formulaJSON.toString());
			logger.warning("HTTP result code: " + response.getStatus());
			logger.warning("Server Response: " + response.getReponse());
			logger.warning("\n\n\n\n\n");
			returnMessage ="FAILED: Failed to load Formula into FIS\n";
			if(UIUtil.isNotNullAndNotEmpty(importedIdentifier)) {
				returnMessage += " - " + importedIdentifier;
			}
			String errorMessage = response.getReponse();
			if(null != errorMessage && (errorMessage.indexOf("composed")>-1 || errorMessage.indexOf("hasRecipeClassifiation")>-1)) {
				returnMessage +=  "Formulation Part contains business area which is not available in FIS system.\n";
				returnMessage +=  "Allowed Business Areas for FIS Formulas are.\n";
				returnMessage += "Corporate,Global Flavors,Global Perfumes,Hair Care,Oral Care,Personal Health Care,PG Chemical,PG Professional,PG Ventures,Shave Care,Skin and Personal Care,System VTO 1,System VTO 2,TAOS \n";
				returnMessage += "Please review formula directly in Enovia and correct Business Area as needed before trying to import to FIS again.";

			} else if(response.getStatus() == 400 && null != errorMessage && (errorMessage.indexOf("Archived")>-1 || errorMessage.indexOf("Externally Archived")>-1)) {
	            JsonObject jsonObject = new JsonParser().parse(errorMessage).getAsJsonObject();
	            String message  = jsonObject.get("message").getAsString();
	            message = message.substring(1, message.length()-1);
	            String [] messageArray = message.split(", ");
	            String materialNames = "";
	            for (String material : messageArray) {
	            	 materialNames += material.split("is in the")[0];
	            	 materialNames += "|";
	            }
	            materialNames = materialNames.substring(0, materialNames.length()-1);
	            String finalMessage = "is in Obsolete state in Enovia. Please review formula directly in Enovia and either correct it there, or recreate in FIS using currently valid materials.";
	            if(messageArray.length > 1) {
	                finalMessage = "are in Obsolete state in Enovia. Please review formula directly in Enovia and either correct it there, or recreate in FIS using currently valid materials.";
	            }
	            returnMessage += ". "+materialNames+finalMessage;
			} else {
				returnMessage += "Failed to import. Please raise a support ticket to investigate";
			}
		}
		
		if (response.getStatus() == 201 && response.getReponse() != null && response.getReponse().length() > 0) {
			JsonObject jsonObject = new JsonParser().parse(response.getReponse()).getAsJsonObject();
			
			JsonObject jsonRecipeObject = jsonObject.get("recipe").getAsJsonObject();
			JsonObject jsonFormulatedMaterial = jsonObject.get("formulatedMaterial").getAsJsonObject();
			String newId = jsonRecipeObject.get("id").getAsString();
			String identifier = jsonRecipeObject.get("recipeNumberDisplay").getAsString();
			String strFormulatedMatId = jsonFormulatedMaterial.get("id").getAsString();
			PGFISCommonUtil commonUtil = new PGFISCommonUtil();
			PGFISCommonUtil.strFormulatedMatId = strFormulatedMatId;
			HttpCallResponse onPremResponse = commonUtil.updateReferenceURIAttributes(casAuthenticator,PGFISWSConstants.TYPE_FORMULATION_PROCESS, formulaPhysicalID, newId, PGFISWSConstants.FIS_CLOUD_TENANT, identifier ); 
			if(onPremResponse.getStatus() == 200)
			{ //OK
				  logger.info("Reference Attributes successfully updated for physical ID - " +formulaPhysicalID); 
			} else {
				  logger.info("Error occured while updating Reference URI Attributes"); 
			}
			returnMessage ="SUCCESS: Formula Got successfully loaded into FIS";
			if(UIUtil.isNotNullAndNotEmpty(importedIdentifier)) {
				returnMessage += " - " + importedIdentifier+"|"+newId;
			}
		} else  if (response.getReponse() == null || response.getReponse().length() == 0) {
			logger.warning("No response from experiment service");
			returnMessage ="FAILED: Failed to load Formula into FIS";
			if(UIUtil.isNotNullAndNotEmpty(importedIdentifier)) {
				returnMessage += " - " + importedIdentifier;
			}
			returnMessage += "\n Reason For Failure as below\n";
			returnMessage += response.getReponse();
		}
		
		return returnMessage;
	}

	@Override
	public void migrateData() throws Exception {
		
	}

}