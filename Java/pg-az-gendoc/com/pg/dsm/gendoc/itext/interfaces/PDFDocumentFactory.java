/*
 **   PDFDocument.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Factory class
 **
 */
package com.pg.dsm.gendoc.itext.interfaces;

import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.itext.models.ImageFile;
import com.pg.dsm.gendoc.itext.models.TextFile;
import com.pg.dsm.gendoc.models.Document;
import org.apache.commons.lang3.StringUtils;

public class PDFDocumentFactory {

    private PDFDocumentFactory() {
        // do nothing
    }

    /**
     * @param document
     * @param localDownloadPath
     * @param extensions
     * @return
     */
    public static PDFDocument getPDFDocument(Document document, String localDownloadPath, String extensions) {
        String fileExtension = document.getFileExtension();
        PDFDocument pdfDocument = null;
        if (isTextFile(fileExtension) && !isPDFFile(fileExtension)) {
            pdfDocument = new TextFile(document, localDownloadPath);
        }
        if (StringUtils.containsIgnoreCase(extensions, fileExtension) && !isTextFile(fileExtension)) {
            pdfDocument = new ImageFile(document, localDownloadPath);
        }
        return pdfDocument;
    }

    /**
     * @param fileExtension
     * @return
     */
    private static boolean isTextFile(String fileExtension) {
        return StringUtils.containsIgnoreCase(CloudConstants.Basic.FILE_EXTENSION_TXT.getValue(), fileExtension);
    }

    /**
     * @param fileExtension
     * @return
     */
    private static boolean isPDFFile(String fileExtension) {
        return StringUtils.containsIgnoreCase(CloudConstants.Basic.FILE_EXTENSION_PDF.getValue(), fileExtension);
    }
}
