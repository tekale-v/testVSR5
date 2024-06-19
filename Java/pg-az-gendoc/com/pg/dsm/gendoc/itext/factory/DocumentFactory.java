/*
 **   DocumentFactory.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6 July CW.
 **   About - Factory class
 **
 */
package com.pg.dsm.gendoc.itext.factory;

import com.pg.dsm.gendoc.itext.interfaces.PDFDocument;
import com.pg.dsm.gendoc.itext.models.AdobeIllustratorFile;
import com.pg.dsm.gendoc.itext.models.ImageFile;
import com.pg.dsm.gendoc.itext.models.TextFile;
import com.pg.dsm.gendoc.models.Document;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;

public class DocumentFactory {
	private DocumentFactory() {
		// do nothing
	}

	/**
	 * @param document
	 * @param localDownloadPath
	 * @param extensions
	 * @return
	 */
	public static PDFDocument getDocType(Document document, String localDownloadPath, String extensions) {
		PDFDocument pdfDocument = null;
		final DocumentType[] documents = DocumentType.values();
		for (DocumentType doc : documents) {
			pdfDocument = doc.getDocumentType(document, localDownloadPath, extensions);
			if (null != pdfDocument) {
				break;
			}
		}
		return pdfDocument;
	}

	public enum DocumentType {
		TEXT_FILE {
			@Override
			public PDFDocument getDocumentType(Document document, String localDownloadPath, String extensions) {
				PDFDocument pdfDocument = null;
				if (CloudGenDocUtil.isTextFile(document.getFileExtension())) {
					pdfDocument = new TextFile(document, localDownloadPath);
				}
				return pdfDocument;
			}
		},
		ADOBE_ILLUSTRATOR_FILE {
			@Override
			public PDFDocument getDocumentType(Document document, String localDownloadPath, String extensions) {
				PDFDocument pdfDocument = null;
				if (CloudGenDocUtil.isAdobeIllustratorFile(document.getFileExtension())) {
					pdfDocument = new AdobeIllustratorFile(document, localDownloadPath);
				}
				return pdfDocument;
			}
		},
		IMAGE_FILE {
			@Override
			public PDFDocument getDocumentType(Document document, String localDownloadPath, String extensions) {
				PDFDocument pdfDocument = null;
				if (CloudGenDocUtil.isImageFile(extensions, document.getFileExtension())) {
					pdfDocument = new ImageFile(document, localDownloadPath);
				}
				return pdfDocument;
			}
		};

		DocumentType() {
		}

		public abstract PDFDocument getDocumentType(Document document, String localDownloadPath, String extensions);
	}
}
