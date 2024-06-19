/**
*************************************************************************
###############################################################################
#
#   Copyright (c) 2001 Procter & Gamble, Inc.  All Rights Reserved.
#   This program contains proprietary and trade secret information of
#   Procter & Gamble, Inc.  Copyright notice is precautionary only and does not
#   evidence any actual or intended publication of such program.
#
###############################################################################
Modifications:
Date			Author					Comments
--------		-----------				--------
09/09/01        Paul Spencer			Originally modified from emxMQLNotice.jsp

**********************************************************************
*/
package com.pg.plm.util;

import java.util.ListIterator;
import java.util.Vector;

import matrix.db.ClientTask;
import matrix.db.ClientTaskItr;
import matrix.db.ClientTaskList;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;


/**
 * A collection of Matrix error catching methods
 * <p>
 * Methods that iterates through the Matrix ClientTaskLists and
 *  displays the messages generated from: NoticeTask, WarningTask,
 *  and ErrorTask.
 * 
 * @author Paul Spencer
 */
public class MatrixNotice
{
  
  // private static DEBUG myDebug = new DEBUG();

    // Developer comments: not included within javadoc
    // Assumptions: 1) Class methods are static
    //
    // Modified:    9/6/01  Paul Spencer
    //              originally modified from emxMQLNotice.jsp

    /**
     * Create an error message from the context object's
     * ClientTask objects
     * <p>
     * @param context - The current user's context object
     * @return An error string used to display on the HTML page
     */
    public static String getMQLNotice(Context context)
    {

        //
        // Declare the message variables and message vector
        //
        String sTaskData = "";
        int iReason = -1;
        Vector msgVector = new Vector();
        Vector reasonVector = new Vector();

        //
        // Loop twice to make sure that get Client Tasks returns results.
        //
        msgVector.clear();
        reasonVector.clear();
        ClientTaskList listNotices = null ;

    for (int i=0; i < 4 ; i++)
        {
            listNotices = context.getClientTasks();
        ClientTaskItr itrClientNotices = new ClientTaskItr(listNotices);
            while (itrClientNotices.next()) {
                  ClientTask clientNoticeTaskMessage =  itrClientNotices.obj();
                  sTaskData = (String) clientNoticeTaskMessage.getTaskData();
              iReason = clientNoticeTaskMessage.getReason();

          }
        }

        for (int i=0; i < 2 ; i++)
        {
        listNotices = context.getClientTasks();
        }

        ClientTaskItr itrNotices = new ClientTaskItr(listNotices);

        while (itrNotices.next()) {
        ClientTask clientTaskMessage =  itrNotices.obj();
        sTaskData = (String) clientTaskMessage.getTaskData();
        iReason = clientTaskMessage.getReason();
        if(sTaskData != null && sTaskData.length()>0) {
          msgVector.addElement(sTaskData);
          reasonVector.addElement(new Integer(iReason));
        }
        }

        context.clearClientTasks();

        //
        // Get the messages from the vector and prepare them to be displayed in JavaScript alert
        //
        String msg = "";


        for (int m = 0; m < msgVector.size(); m++) {
            String sMessage = (String) msgVector.elementAt(m);
            Integer IntReason2 = (Integer) reasonVector.elementAt(m);
            iReason = IntReason2.intValue();
            Vector vStringBuffers = new Vector();

            if (sMessage != null && sMessage.length() > 0) {
              StringBuffer sbMsg = new StringBuffer();

              for (int i = 0; i < sMessage.length(); i++) {
                char ch = sMessage.charAt(i);
                int unicode = Character.getNumericValue(ch);
                Character CH = new Character(ch);
                int hashCode = CH.hashCode();

                if (hashCode != 10 && i < sMessage.length()-1) {  // hashcode: 10 -check for carriage return
                  sbMsg  = sbMsg.append(ch);
                } else {
                  vStringBuffers.addElement(sbMsg);
                  sbMsg = new StringBuffer("");
                }
              }
            }

            //
            // Display the message before going to the next message element.
            // Display the heading based on the reason. And set the heading text message based on the Reason.
            //

            String sExternalTask = "External Program";            // ExternalTask = 0
            String sMQLTCLTask = "MQL Tcl";                       // MQLTCLTask = 1
            String sApplTask = "Application Script";              // ApplTask = 2
            String sNoticeTask = "Notice";                        // NoticeTask = 3
            String sWarningTask = "Warning";                      // WarningTask = 4
            String sErrorTask = "Error";                          // ErrorTask = 5
            String sOpenViewTask = "Open View";                   // OpenViewTask = 6
            String sOpenEditTask = "Open Edit";                   // OpenEditTask = 7
            String sCheckinTask = "Checkin";                      // CheckinTask = 8
            String sCheckoutTask = "Checkout";                    // CheckoutTask = 9
            String sUpdateClientTask = "Update Client";           // UpdateClientTask = 10
            String sPopcontextTask = "Pop context";               // PopcontextTask = 11
            String sPushcontextTask = "Push context";             // PushcontextTask = 12
            String sNoTask = null;                                // NoTask = 13

            String sHeaderText = null;
            switch (iReason) {
              case 0 : sHeaderText = sExternalTask; break;
              case 1 : sHeaderText = sMQLTCLTask; break;
              case 2 : sHeaderText = sApplTask; break;
              case 3 : sHeaderText = sNoticeTask; break;
              case 4 : sHeaderText = sWarningTask; break;
              case 5 : sHeaderText = sErrorTask; break;
              case 6 : sHeaderText = sOpenViewTask; break;
              case 7 : sHeaderText = sOpenEditTask; break;
              case 8 : sHeaderText = sCheckinTask; break;
              case 9 : sHeaderText = sCheckoutTask; break;
              case 10 : sHeaderText = sUpdateClientTask; break;
              case 11 : sHeaderText = sPopcontextTask; break;
              case 12 : sHeaderText = sPushcontextTask; break;
              case 13 : sHeaderText = sNoTask; break;
              default : sHeaderText = null;
            }


            // Display only if the reason is Notice, Warning or Error
            if (iReason == 3 || iReason == 4 || iReason == 5) {

              if (sHeaderText != null && sHeaderText.length() > 0 && vStringBuffers.size() > 0) {

                msg += sHeaderText + ":\n\n";

              }
              String sMsg = null;
              for (int i=0; i < vStringBuffers.size(); i++) {
                StringBuffer sbAlertMsg = (StringBuffer) vStringBuffers.elementAt(i);
                sMsg = sbAlertMsg.toString();
                sMsg = sMsg.replace('\'','\"');  // replace the ' chr with " only.

                msg += sMsg + "\n";
              }
            }
        } // END msgVector for loop

        return msg;
    }

  /**
   * Gets MQL messages of the types Warning, Error or Notices
   *
   * @param boolean Warning - indicates if we have to return Warning messages
   * @param boolean Error
   * @param boolean Notice
   */
   public static Vector getMQLMessages(Context _context, boolean Warning,
   boolean Error, boolean Notice)
    {
      Vector vMQLWarningMessages = new Vector();

      // Declare the message variables and message vector
    //  String sTaskData = "";
      int iReason = -1;

      try
      {

      _context.updateClientTasks();

      //
      // Loop twice to make sure that get Client Tasks returns results.
      //
      ClientTaskList listNotices = null ;

      for (int i=0; i < 2 ; i++)
      {
        listNotices = _context.getClientTasks();
      }

      ClientTaskItr itrNotices = new ClientTaskItr(listNotices);

      while (itrNotices.next())
      {

          ClientTask clientTaskMessage =  itrNotices.obj();
          String sTaskData = (String) clientTaskMessage.getTaskData();
          iReason = clientTaskMessage.getReason();
          System.out.println("MatrixNotice ==> sTaskData : " + sTaskData);
          System.out.println("MatrixNotice ==> iReason : " + iReason);
          if(sTaskData != null && sTaskData.length()>0)
          {
              if ( ( (iReason == ClientTask.reasonWarningMsg) && Warning) ||
                    ( (iReason == ClientTask.reasonErrorMsg) && Error) ||
                    ( (iReason == ClientTask.reasonNoticeMsg) && Notice) )
              {
                        sTaskData = processCarraigeReturn(sTaskData);
                        vMQLWarningMessages.addElement(sTaskData);

              }
          }

      }

      _context.clearClientTasks();

    }
    catch (MatrixException e)
    {
         e.printStackTrace();
//        debug.fatal("Exception in getMQLMessages : " + e.getMessage());
    }
    catch (Exception e)
    {
         e.printStackTrace();
//        debug.fatal("Exception in getMQLMessages : " + e.getMessage());

    }

    return vMQLWarningMessages;

  }

   /**
    * Format the error message by adding carriage returns where appropriate
    * <p>
    * @param sMessage The error message without carriage returns
    * @return The error message with carriage returns
    */
   private static String processCarraigeReturn(String sMessage)
  {
    String msg = "";
    Vector vStringBuffers = new Vector();

    if (sMessage != null && sMessage.length() > 0)
    {
        StringBuffer sbMsg = new StringBuffer();

        for (int i = 0; i < sMessage.length(); i++) {
          //  myDebug.Debug("i: " + i);
            char ch = sMessage.charAt(i);
            int unicode = Character.getNumericValue(ch);
            Character CH = new Character(ch);
            int hashCode = CH.hashCode();

          if (hashCode != 10 && i < sMessage.length()-1) {  // hashcode: 10 -check for carriage return
              sbMsg  = sbMsg.append(ch);
          } else {
              vStringBuffers.addElement(sbMsg);
              sbMsg = new StringBuffer("");
          }
        }
    }

      String sMsg = null;
      for (int i=0; i < vStringBuffers.size(); i++) {
          StringBuffer sbAlertMsg = (StringBuffer) vStringBuffers.elementAt(i);
          sMsg = sbAlertMsg.toString();
          sMsg = sMsg.replace('\'','\"');  // replace the ' chr with " only.
          msg += sMsg + "\n";
      }

    return msg;
  }

  /**
   * Gets MQL messages of the types Warning, Error or Notices
   *
   * @param boolean Warning - indicates if we have to return Warning messages
   * @param boolean Error
   * @param boolean Notice
   */
   public static String getMQLMessageString(Context _context, boolean Warning,
   boolean Error, boolean Notice)
    {
      Vector vMessages = MatrixNotice.getMQLMessages(_context,Warning,Error,Notice);
      StringBuffer sbfMQLMessages = new StringBuffer();
      if (vMessages.size() > 0)
      {
            ListIterator lMessageItr = vMessages.listIterator();
            while (lMessageItr.hasNext())
            {
                sbfMQLMessages.append((String) lMessageItr.next());
                sbfMQLMessages.append("\n");
            }
      }
      return sbfMQLMessages.toString();

  }

  /**
   * Returns a string containing MQL warnings.
   *
   * @return String containing MQL Warnings
   */
   public static String getMQLWarningsString(Context _context)
   {
        return getMQLMessageString(_context, true, false, false);
   }


  /**
   * Set an MQL warning message
   *
   * @param Context _context
   * @param String message to be used for the warning
   *
   */
   public static void setMQLWarning(Context _context, String sMsg)
   {
      try
      {
          MQLCommand mqlCmd = new MQLCommand();
          StringBuffer sbfCmd = new StringBuffer();
          sbfCmd.append("warning '").append(sMsg).append("'");
          boolean cmdStat = mqlCmd.executeCommand(_context, sbfCmd.toString());

      }
      catch (MatrixException e)
      {
    	  e.printStackTrace();
      }
   }

   /**
   * Gets MQL messages of the type Warning
   *
   * @returns Vector of MQL Warnings
   */
   public static Vector getMQLWarnings(Context _context)
   {
        return getMQLMessages(_context, true, false, false);
   }

}