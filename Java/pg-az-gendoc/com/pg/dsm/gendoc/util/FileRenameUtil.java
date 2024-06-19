package com.pg.dsm.gendoc.util;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileRenameUtil {
    boolean isOperationSuccessful;
    String errorMessage;
    List<File> renamedFileList;

    /**
     * @param processBuilder
     */
    private FileRenameUtil(ProcessBuilder processBuilder) {
        this.isOperationSuccessful = processBuilder.isOperationSuccessful;
        this.errorMessage = processBuilder.errorMessage;
        this.renamedFileList = processBuilder.renamedFileList;
    }

    public boolean isOperationSuccessful() {
        return isOperationSuccessful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<File> getRenamedFileList() {
        return renamedFileList;
    }

    public static class ProcessBuilder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        List<File> fileList;
        String invalidCharacters;
        boolean renameWithTimeStamp;
        List<File> renamedFileList;
        boolean operationStatus;
        boolean isOperationSuccessful;
        int inputFileCount;
        int renamedFileCount;
        String errorMessage;

        /**
         * @param fileList
         * @param invalidCharacters
         * @param renameWithTimeStamp
         */
        public ProcessBuilder(List<File> fileList, String invalidCharacters, boolean renameWithTimeStamp) {
            this.fileList = fileList;
            this.invalidCharacters = invalidCharacters;
            this.renameWithTimeStamp = renameWithTimeStamp;
            this.renamedFileList = new ArrayList<>();
            this.operationStatus = Boolean.TRUE;
            //Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
            this.errorMessage = DomainConstants.EMPTY_STRING;
            //Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
            this.inputFileCount = fileList.size();
        }

        /**
         * Modified by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - To remove throw InterruptedException
         * @return
         */
        public FileRenameUtil build() {
            performFileOperation();
            this.renamedFileCount = renamedFileList.size();
            logger.info(String.format("Input File Count %s and Renamed File Count %s", inputFileCount, renamedFileCount));
            if(operationStatus && inputFileCount == renamedFileCount) {
                this.isOperationSuccessful = Boolean.TRUE;
                logger.info("File Rename/Remove Special Characters was successful");
            } else {
                logger.error("File Rename/Remove Special Characters failed");
            }
            return new FileRenameUtil(this);
        }


        /**
         * Modified by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - To remove throw InterruptedException
         *
         */
        private void performFileOperation() {
            String fileDir;
            String fileName;
            String fileNameWithoutExtension;
            String fileNameClean;
            boolean rename;
            StringBuilder newFileNameBuilder;
            File newFileName;
            CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
            for (File file : fileList) {
                rename = Boolean.FALSE;
                fileDir = file.getParent();
                fileName = file.getName();
                fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
                fileNameClean = cloudGenDocUtil.getCleanedUpFileName(fileNameWithoutExtension, invalidCharacters);
                newFileNameBuilder = new StringBuilder();
                newFileNameBuilder.append(fileDir);
                newFileNameBuilder.append(File.separator);
                newFileNameBuilder.append(fileNameClean);
                if(renameWithTimeStamp) {
                    // irm case only - rename with timestamp
                    // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
                    newFileNameBuilder.append(CloudGenDocUtil.getRandomUUIDForFileNamePostFix(fileName));
                    // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
                    rename = Boolean.TRUE;
                } else {
                    // dsm case - rename only when actual file and clean file are different.
                    if (!fileNameWithoutExtension.equals(fileNameClean)) {
                        rename = Boolean.TRUE;
                    } else {
                        // if file names are same - do not rename (this applies only for DSM)
                        renamedFileList.add(file);
                    }
                }
                if(rename) {
                    newFileName = new File(newFileNameBuilder.toString());
                    if (newFileName.exists()) {
                        logger.error(String.format("Issue while rename/remove special characters from checkout file. File name already exist: %s", newFileName.getName()));
                        operationStatus = Boolean.FALSE;
                        //Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
                        this.errorMessage = CloudConstants.Basic.FAILED_TO_RENAME_CHECK_OUT_FILE.getValue();
                        //Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
                    } else {
                        if (file.renameTo(newFileName)) {
                            renamedFileList.add(newFileName);
                        } else {
                            logger.error(String.format("Failed while rename/remove special characters from checkout file: %s", fileName));
                            operationStatus = Boolean.FALSE;
                            //Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
                            this.errorMessage = CloudConstants.Basic.FAILED_TO_RENAME_CHECK_OUT_FILE.getValue();
                            //Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
                        }
                    }
                }
            }
        }
    }
}
