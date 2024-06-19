package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;

import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationAttribute;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.Note;
import com.pdfview.impl.FPP.Notes;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetNotes {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetNotes(Context context, String sOID){
		_context = context;
		_OID = sOID;
	}

	/**
	 * Method for retrieving  the Notes table data
	 * @return
	 * throws exception
	 */
	public Notes getComponent() throws MatrixException{
		Notes notes = new Notes();
		List<Note> lsNotes = notes.getNote();
		
		String strRelId = DomainConstants.EMPTY_STRING;
		String strIdParent= DomainConstants.EMPTY_STRING;
		String strId = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strNo = DomainConstants.EMPTY_STRING;
		String strDescription = DomainConstants.EMPTY_STRING;
		String[] argsFPPNo = null;
		Vector vNo = null;
		String[] argsDes = null;
		try {
			Map Argmap = new HashMap();
			Argmap.put("objectId", _OID);
			String[] argsFPP= JPO.packArgs(Argmap);
			MapList mlNotesAtt = (MapList)PDFPOCHelper.executeMainClassMethod(_context, "pgIPMTablesJPO", "getTableCharacteristicSequenceData", argsFPP);
			if(mlNotesAtt != null && !mlNotesAtt.isEmpty()){
				int mlNotesAttsize=mlNotesAtt.size();
				Map mapObject = null;
				MapList mlObj =null;
				Map mpObj =null;
				Map mpObjDes =null;
				MapList mlObjDes =null;
				Map mpCPNFieldType =null;
				Map mpsettings =null;
				Map argmapDes =null;
				Map mpParamList =null;
				StringList slDes =null;
				Map argmapNo = null;
				for (int i = 0;i< mlNotesAttsize; i++){
					mapObject = (Map) mlNotesAtt.get(i);
					strRelId = (String)mapObject.get(PDFConstant.ID_CONNECTION);
					strIdParent = (String)mapObject.get("id[parent]");
					strId = (String)mapObject.get(DomainConstants.SELECT_ID);
					strType = (String)mapObject.get(DomainConstants.SELECT_TYPE);
					mpObj = new HashMap();
					mpObj.put(PDFConstant.ID_CONNECTION,strRelId );
					mlObj = new MapList();
					mlObj.add(mpObj);
					argmapNo = new HashMap();
					argmapNo.put("objectList", mlObj);
					argsFPPNo = JPO.packArgs(argmapNo);
					vNo =(Vector)getSequenceOrder(_context, argsFPPNo);
					
					if((vNo !=null) && (!vNo.isEmpty()) )
						strNo = (String)vNo.get(0);

					mpObjDes = new HashMap();
					mpObjDes.put("id[parent]",strIdParent );
					mpObjDes.put(DomainConstants.SELECT_ID,strId );
					mpObjDes.put(DomainConstants.SELECT_TYPE,strType );
					mlObjDes = new MapList();
					mlObjDes.add(mpObjDes);
					mpCPNFieldType = new HashMap();
					mpCPNFieldType.put("CPNFieldType","description" );
					mpsettings = new HashMap();
					mpsettings.put("settings",mpCPNFieldType );
					argmapDes = new HashMap();
					argmapDes.put("objectList", mlObjDes);
					argmapDes.put("columnMap", mpsettings);
					mpParamList = new HashMap();
					mpParamList.put("reportFormat", null);
					argmapDes.put("paramList", mpParamList);
					argsDes = JPO.packArgs(argmapDes);
					slDes =(StringList)PDFPOCHelper.executeMainClassMethod(_context, "emxCPNCharacteristicList", "getCharacteristicColumnVal", argsDes);
					
					if((slDes !=null) && (!slDes.isEmpty()) )
						strDescription = (String)slDes.get(0).trim();
					if(UIUtil.isNotNullAndNotEmpty(strDescription)) {
						strDescription =strDescription.replaceAll("&gt;","#GREATER_THAN");
						strDescription = strDescription.replaceAll("[<]","#LESS_THAN");
						strDescription =strDescription.replaceAll("&#x3a;", ":");
						strDescription =strDescription.replaceAll("&#x40;", "@");
						strDescription =strDescription.replaceAll("&amp;", "&");
						strDescription =strDescription.replaceAll("&#x3d;", "=");
						strDescription =strDescription.replaceAll("&#x3b;", ";");
						strDescription =strDescription.replaceAll("&#x28;", "(");
						strDescription =strDescription.replaceAll("&#x29;", ")");
						strDescription =strDescription.replaceAll("&#x2f;", "/");
						strDescription =strDescription.replaceAll("&#x23;", "#");
						strDescription =strDescription.replaceAll("&#x21;", "!");
						strDescription =strDescription.replaceAll("&#x27;", "'");
						strDescription =strDescription.replaceAll("&#x3f;", "?");
						strDescription =strDescription.replaceAll("&#x5e;", "^");
						strDescription =strDescription.replaceAll("&#x7b;", "{");
						strDescription =strDescription.replaceAll("&#x7d;", "}");
						strDescription =strDescription.replaceAll("&#x5b;", "[");
						strDescription =strDescription.replaceAll("&#x5d;", "]");
						strDescription =strDescription.replaceAll("&#x7c;", "|");
						strDescription =strDescription.replaceAll("&#x7e;", "~");
						strDescription =strDescription.replaceAll("&#x60;", "`");
						strDescription =strDescription.replaceAll("&quot;", "\"");
						strDescription =strDescription.replaceAll("&#xa;", "<BR/>");
						strDescription=StringEscapeUtils.escapeJava(strDescription);
					} else{
						strDescription = DomainConstants.EMPTY_STRING;
					}

					Note note = new Note();
					note.setNumber(strNo);
					note.setNoteData(strDescription);
					lsNotes.add(note);
				}
			}

		} catch (Exception e) {
			throw new MatrixException(e);
		}
		return notes;
	}

	private Vector getSequenceOrder(Context context, String[] args) throws Exception {
		HashMap paramMap = (HashMap)JPO.unpackArgs(args);
		String relID = DomainConstants.EMPTY_STRING;
		MapList objectList = (MapList)paramMap.get("objectList");
		Vector sequenceOrder = new Vector();
		String sequence = DomainConstants.EMPTY_STRING;
		try {
			if (objectList != null && !objectList.isEmpty())
			{
				Map mpTemp = null;
				int objectListSize=objectList.size();
				for (int i = 0; i <objectListSize;i++)
				{
					mpTemp = (Map)objectList.get(i);
					sequence=(String)mpTemp.get(FormulationAttribute.SHARED_TABLE_CHARACTERISTIC_SEQUENCE.getAttributeSelect(context));
					if(sequence==null){
						 relID = (String)mpTemp.get(PDFConstant.ID_CONNECTION);
						sequence = DomainRelationship.getAttributeValue(context,relID,pgV3Constants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
					}
					sequenceOrder.addElement(sequence);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sequenceOrder;
	}
}
