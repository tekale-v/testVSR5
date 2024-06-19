/*
 **   ArtworkFailureViewFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Artwork Failure View factory bean
 **   DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
 */
package com.pg.dsm.veeva.helper.enovia.failure.factory;

import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.helper.enovia.failure.ArtworkFailureView;
import com.pg.dsm.veeva.helper.enovia.failure.FailureView;

import matrix.db.Context;

public class ArtworkFailureViewFactory implements FailureViewAbstractFactory {

	private String message;
	private String artworkID;
	private Context context;
	private String currentDate;
	private Configurator configurator;
	private String sToEmailId;
	private String sPMP;
	private String sDocumentNumber;
	private String sCcEmailId;
	
	public ArtworkFailureViewFactory(Configurator configurator, Context context, String message, String sDocumentNumber, String artworkID, String currentDate, String sToEmailId, String sPMP, String sCcEmailId) {
		this.context = context;
		this.message = message;
		this.artworkID = artworkID;
		this.currentDate = currentDate;
		this.configurator = configurator;
		this.sToEmailId = sToEmailId;
		this.sPMP = sPMP;
		this.sDocumentNumber = sDocumentNumber;
		this.sCcEmailId = sCcEmailId;
	}

	@Override
	public FailureView createFailureView() {
		// TODO Auto-generated method stub
		return new ArtworkFailureView(configurator, context, message, sDocumentNumber, artworkID, currentDate, sToEmailId, sPMP, sCcEmailId);
	}
}
