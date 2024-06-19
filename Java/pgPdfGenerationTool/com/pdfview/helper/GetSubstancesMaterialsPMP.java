package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.PMP.SubstancesMaterial;
import com.pdfview.impl.PMP.SubstancesMaterials;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetSubstancesMaterialsPMP {

	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetSubstancesMaterialsPMP(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Retrieve Material tab information for DSO types
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public SubstancesMaterials getComponent() {
		SubstancesMaterials SubstancesMaterials = new SubstancesMaterials();
		List<SubstancesMaterial> substancesMaterials = SubstancesMaterials.getSubstancesandMaterials();
		try {
			Map Argmap = new HashMap();
			Argmap.put("objectId", _OID);
			Argmap.put("expandLevel", "0");
			String[] argsMat = JPO.packArgs(Argmap);
			String strSequenceNumber = DomainConstants.EMPTY_STRING;
			MapList mlMaterial = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "emxCPNMaterial",
					"getAllRelatedMaterials", argsMat);
			boolean havingMaterialData = false;
			if (mlMaterial != null && !mlMaterial.isEmpty()) {
				havingMaterialData = true;
			}
			if (havingMaterialData) {
				int nMapMPP = mlMaterial.size();
				Map mpMaterials = null;
				MapList mlMaterialsSort = new MapList();
				for (int i = 0; i < nMapMPP; i++) {

					mpMaterials = (Map) mlMaterial.get(i);
					strSequenceNumber = (String) mpMaterials.get(pgV3Constants.SELECT_ATTRIBUTE_PGSEQUENCE);
					mpMaterials.put("Sequence Number", strSequenceNumber);
					mlMaterialsSort.add(mpMaterials);
				}
				mlMaterialsSort.addSortKey("Sequence Number", "ascending", "String");
				mlMaterialsSort.sortStructure();
				StringList selectStmtMat = new StringList(11);
				selectStmtMat.add(DomainConstants.SELECT_NAME);
				selectStmtMat.add(DomainConstants.SELECT_TYPE);
				selectStmtMat.add(DomainConstants.SELECT_CURRENT);
				selectStmtMat.add(DomainConstants.SELECT_DESCRIPTION);
				selectStmtMat.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				selectStmtMat.add(pgV3Constants.SELECT_ATTRIBUTE_MARKETINGNAME);
				selectStmtMat.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSTCONSUMERRECYCLEDCONTENT);
				selectStmtMat.add(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLEGACYENVCLASS);
				selectStmtMat.add(PDFConstant.SELECT_ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE);
				selectStmtMat.add(pgV3Constants.SELECT_ATTRIBUTE_MANUFACTURER);
				selectStmtMat.add(pgV3Constants.SELECT_POLICY);
				StringList selectStmtsRel = new StringList(7);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLAYER);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINIMUMPERCENTWEIGHTBYWEIGHT);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXIMUMPERCENTWEIGHTBYWEIGHT);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGSEQUENCE);
				selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
				String strpgMinimumPercentWeightbyWeight = DomainConstants.EMPTY_STRING;
				String strpgMaximumPercentWeightbyWeight = DomainConstants.EMPTY_STRING;
				String strQuantity = DomainConstants.EMPTY_STRING;
				String strMatlLayer = DomainConstants.EMPTY_STRING;
				String strObjId = DomainConstants.EMPTY_STRING;
				String strRelId = DomainConstants.EMPTY_STRING;
				String strMaterialName = DomainConstants.EMPTY_STRING;
				String strMaterialType = DomainConstants.EMPTY_STRING;
				String strMaterialState = DomainConstants.EMPTY_STRING;
				String strMaterialTitle = DomainConstants.EMPTY_STRING;
				String strMaterialDesc = DomainConstants.EMPTY_STRING;
				String sManufacturer = DomainConstants.EMPTY_STRING;
				String strPolicy = DomainConstants.EMPTY_STRING;
				String strTradeName = DomainConstants.EMPTY_STRING;
				String strMatlPCRC = DomainConstants.EMPTY_STRING;
				String strMatlPPIR = DomainConstants.EMPTY_STRING;
				String strMatlComm = DomainConstants.EMPTY_STRING;
				String strmtrl = DomainConstants.EMPTY_STRING;
				String strMatlSeq = DomainConstants.EMPTY_STRING;
				String strUOMValue = DomainConstants.EMPTY_STRING;
				MapList mlToId = new MapList();
				Map mapObject = new HashMap();
				Map mpMatl = new HashMap();
				Map mData = new HashMap();
				StringList busSelect = new StringList(3);
				busSelect.add(DomainConstants.SELECT_NAME);
				busSelect.add(DomainConstants.SELECT_TYPE);
				busSelect.add(DomainConstants.SELECT_CURRENT);
				int mlMaterialsSortsize = mlMaterialsSort.size();
				int mlToIdsize = 0;
				for (int i = 0; i < mlMaterialsSortsize; i++) {
					SubstancesMaterial substancesMaterial = new SubstancesMaterial();
					mapObject = (Map) mlMaterialsSort.get(i);
					strObjId = (String) mapObject.get(DomainConstants.SELECT_ID);
					DomainObject doObj = DomainObject.newInstance(_context, strObjId);
					mpMatl = (Map) doObj.getInfo(_context, selectStmtMat);
					strRelId = (String) mapObject.get(PDFConstant.ID_CONNECTION);
					String[] relargs = new String[1];
					relargs[0] = strRelId;
					
					mlToId = (MapList) DomainRelationship.getInfo(_context, relargs, selectStmtsRel);
					if (null != mlToId && !mlToId.isEmpty()) {
						mlToIdsize = mlToId.size();
						for (int j = 0; j < mlToIdsize; j++) {
							mData = (Map) mlToId.get(j);
							strMatlLayer = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLAYER);
							strpgMinimumPercentWeightbyWeight = (String) mData
									.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINIMUMPERCENTWEIGHTBYWEIGHT);
							strpgMaximumPercentWeightbyWeight = (String) mData
									.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXIMUMPERCENTWEIGHTBYWEIGHT);
							strQuantity = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
							strMatlComm = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
							strMatlSeq = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSEQUENCE);
							strUOMValue = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
						}
					}
					strMaterialName = (String) mpMatl.get(DomainConstants.SELECT_NAME);
					strMaterialType = (String) mpMatl.get(DomainConstants.SELECT_TYPE);
					strMaterialState = (String) mpMatl.get(DomainConstants.SELECT_CURRENT);
					strMaterialTitle = (String) mpMatl.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
					strMaterialDesc = (String) mpMatl.get(DomainConstants.SELECT_DESCRIPTION);
					strPolicy = (String) mpMatl.get(DomainConstants.SELECT_POLICY);
					if (UIUtil.isNullOrEmpty(strMaterialDesc)) {
						strMaterialDesc = DomainConstants.EMPTY_STRING;
					}
					if (UIUtil.isNullOrEmpty(strMatlComm)) {
						strMatlComm = DomainConstants.EMPTY_STRING;
					}
					if (UIUtil.isNullOrEmpty(strMatlSeq)) {
						strMatlSeq = DomainConstants.EMPTY_STRING;
					}
					strTradeName = (String) mpMatl.get(pgV3Constants.SELECT_ATTRIBUTE_MARKETINGNAME);
					strMatlPCRC = (String) mpMatl.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOSTCONSUMERRECYCLEDCONTENT);
					strMatlPPIR = (String) mpMatl.get(PDFConstant.SELECT_ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE);
					strmtrl = (String) mpMatl.get(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLEGACYENVCLASS);
					sManufacturer = (String) mpMatl.get(pgV3Constants.SELECT_ATTRIBUTE_MANUFACTURER);
					strMaterialDesc = StringHelper.filterLessAndGreaterThanSign(strMaterialDesc);
					strMatlComm = StringHelper.filterLessAndGreaterThanSign(strMatlComm);
					strMaterialState = EnoviaHelper.getStateName(_context, strMaterialState, strPolicy);
					substancesMaterial.setName(strMaterialName);
					substancesMaterial.setTargetPercentageWeightbyWeight(strQuantity);
					substancesMaterial.setManufacturer(sManufacturer);
					substancesMaterial.setType(strMaterialType);
					substancesMaterial.setSeq(strMatlSeq);
					substancesMaterial.setDescription(strMaterialDesc);
					substancesMaterial.setTitle(strMaterialTitle);
					substancesMaterial.setTradeName(strTradeName);
					substancesMaterial.setLegacyEnvironmentalClass(strmtrl);
					substancesMaterial.setLayerComponentDescription(strMatlLayer);
					substancesMaterial.setMinimumPercentageWeightbyWeight(strpgMinimumPercentWeightbyWeight);
					substancesMaterial.setMaximumPercentageWeightbyWeight(strpgMaximumPercentWeightbyWeight);
					substancesMaterial.setPostConsumerRecycledContent(strMatlPCRC);
					substancesMaterial.setPostIndustrialRecycledContent(strMatlPPIR);
					substancesMaterial.setComments(strMatlComm);
					substancesMaterial.setState(strMaterialState);
					substancesMaterial.setQuantityUnitOfMeasure(strUOMValue);
					substancesMaterials.add(substancesMaterial);

				}
			}
			mlMaterial.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SubstancesMaterials;
	}
}
