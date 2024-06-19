package com.pg.v3.custom;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.pg.util.EncryptCrypto;

public class pgV3Util extends DomainObject
{
  private static pgV3Util instance = null;
  private HashMap sampleMapForSupplier = null;
  private HashMap sampleMapForManufacturer = null;

  public static pgV3Util getInstance()  
  {
    if (instance == null) {
      instance = new pgV3Util();
    }
    return instance;
  }

  public void setSupplierMap(HashMap attrVal)
  {
    this.sampleMapForSupplier = attrVal;
  }

  public HashMap getSupplierMap()
  {
    return this.sampleMapForSupplier;
  }

  public void setManufacturerMap(HashMap attrVal)
  {
    this.sampleMapForManufacturer = attrVal;
  }

  public HashMap getManufacturerMap()
  {
    return this.sampleMapForManufacturer;
  }

  public void generateBrandBookReport(Context context, String strObjectID, String strLevel, String strBOMBaseQuantity, String strBaseQuantity, String rootPath)
    throws Exception
  {
    try
    {
      String[] args = { strObjectID, strLevel, strBOMBaseQuantity, strBaseQuantity, rootPath };
      JPO.invoke(context, "pgIPMBrandBook", null, "generateBrandBookReport", args);
    }
    catch (Exception ex)
    {
      System.out.println("----Error invoking JPO method pgIPMBrandBook:generateBrandBookReport()-->" + ex.getMessage());
      throw ex;
    }
  }

  public static StringList getUniqueStringList(Collection cItems)
  {
    StringList slUniqueItems = new StringList();
    if ((cItems instanceof Set))
      slUniqueItems.addAll(cItems);
    else {
      slUniqueItems.addAll(new LinkedHashSet(cItems));
    }
    return slUniqueItems;
  }

  public static MapList getUniqueMapList(Context context, MapList mpList, String sKey)
    throws Exception
  {
    MapList mpUniqueList = new MapList();
    StringList slKeyList = new StringList();
    try
    {
      if (mpList != null)
      {
        int i = 0;
        for (int sz = mpList.size(); i < sz; i++)
        {
          Map mpTDL = (Map)mpList.get(i);
          String sToken = (String)mpTDL.get(sKey);
          if ((!"".equalsIgnoreCase(sToken)) && 
            (!slKeyList.contains(sToken)) && 
            (!mpUniqueList.contains(mpTDL))) {
            mpUniqueList.add(mpTDL);
          }

          slKeyList.addElement(sToken);
        }
      }
    }
    catch (Exception e)
    {
      System.out.println(":Error in getUniqueMapList:" + e);
    }
    return mpUniqueList;
  }

  public static boolean isNotBlank(String sInput)
  {
    return (sInput != null) && (!"".equals(sInput)) && (!"null".equals(sInput));
  }

  public static boolean isBlank(String sInput)
  {
    return (sInput == null) || ("".equals(sInput)) || ("null".equals(sInput));
  }

  public static String removeNull(String sInput)
  {
    if ((sInput == null) || ("".equals(sInput)) || ("null".equals(sInput))) {
      return "";
    }
    return sInput;
  }

  public static boolean patternMatch(String pattern, String match)
  {
    pattern = pattern.toLowerCase();
    match = match.toLowerCase();
    if ((pattern == null) || (match == null)) {
      return false;
    }
    if (pattern.equals("*")) {
      return true;
    }
    int p = 0;
    int m = 0;
    while ((p < pattern.length()) && (m <= match.length())) {
      switch (pattern.charAt(p))
      {
      case '?':
        p++;
        m++;
        break;
      case '*':
        if (p + 1 >= pattern.length()) {
          return true;
        }
        if (m < match.length())
        {
          if (patternMatch(pattern.substring(p + 1), match.substring(m))) {
            return true;
          }
          m++;
        }
        else
        {
          return false;
        }

      default:
        if (m >= match.length()) {
          return false;
        }
        if (pattern.charAt(p) != match.charAt(m)) {
          return false;
        }
        p++;
        m++;
      }
    }
    return (p >= pattern.length()) && (m >= match.length());
  }

  public static String getStringFromMap(Context context, Map map, String key)
    throws Exception
  {
    String returnVal = "";
    Object objectVal = map.get(key);
    if ((objectVal instanceof String))
    {
      returnVal = (String)objectVal;
    }
    else if ((objectVal instanceof StringList))
    {
      StringList slVal = (StringList)objectVal;
      returnVal = (String)slVal.get(0);
    }
    return returnVal;
  }

  public static void sendEMailToUser(String[] args)
    throws Exception
  {
    try
    {
      String host = args[0];

      String from = args[1];

      String to = args[2];

      String fileAttachment = args[3];

      String strSubject = args[4];

      String strBodyText = args[5];

      String strPersonal = args[6];

      String strCc = "";
      if (args.length > 7) {
        strCc = args[7];
      }
      String strBcc = "";
      if (args.length > 8) {
        strBcc = args[8];
      }
      Properties props = System.getProperties();

      props.put("mail.smtp.host", host);

      Session session = Session.getInstance(props, null);

      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from, strPersonal));
      if ((to != null) && (to.indexOf(",") != -1))
        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      else {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      }
      if ((strCc != null) && (strCc.indexOf(",") != -1))
      {
        if (isNotBlank(strCc)) {
          message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(strCc));
        }
      }
      else if (isNotBlank(strCc)) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(strCc));
      }
      if ((strBcc != null) && (strBcc.indexOf(",") != -1))
      {
        if (isNotBlank(strBcc)) {
          message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(strBcc));
        }
      }
      else if (isNotBlank(strBcc)) {
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(strBcc));
      }
      message.setSubject(strSubject);

      MimeBodyPart messageBodyPart = new MimeBodyPart();

      messageBodyPart.setText(strBodyText);
      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart);
      if ((fileAttachment != null) && (!"".equals(fileAttachment)))
      {
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(fileAttachment);
        messageBodyPart.setDataHandler(new DataHandler(source));
        String fileName = "";
        if (fileAttachment.indexOf("/") != -1) {
          fileName = fileAttachment.substring(fileAttachment.lastIndexOf("/") + 1, fileAttachment.length());
        }
        messageBodyPart.setFileName(fileName);
        multipart.addBodyPart(messageBodyPart);
      }
      message.setContent(multipart);

      Transport.send(message);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void zipFile(String folderName, String filenames) throws Exception
  {
    try
    {
      byte[] buffer = new byte[1024];
      FileOutputStream fos = new FileOutputStream(folderName + File.separator + filenames + ".zip");
      ZipOutputStream zos = new ZipOutputStream(fos);

      String extn = "";
      if ("pgIPMUserMaterialStatusReportbyCompany".equalsIgnoreCase(filenames))
      {
        extn = ".csv";
      }
      else
      {
        extn = ".xls";
      }
      ZipEntry ze = new ZipEntry(filenames + extn);
      zos.putNextEntry(ze);
      FileInputStream in = new FileInputStream(folderName + File.separator + filenames + extn);
      int len;
      while ((len = in.read(buffer)) > 0)
      {
        zos.write(buffer, 0, len);
      }
      in.close();
      zos.closeEntry();

      zos.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public String authenticate(String username, String pwd)
    throws Exception
  {
    String errorMass = "";

    Hashtable authEnv = new Hashtable();
    String base = "o=world";
    String dn = "extShortName=" + username;
    String strAuthLDAPURL= FrameworkProperties.getProperty("emxFramework.Login.AuthLDAPURL");
	String strLDAPUserUid = FrameworkProperties.getProperty("emxFramework.Login.AuthLDAPUserId"); 
	String strLDAPUserPassword = FrameworkProperties.getProperty("emxFramework.Login.AuthLDAPUserPassword");
	authEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    //authEnv.put("java.naming.provider.url", "ldap://eddev.pg.com");
    authEnv.put("java.naming.provider.url", strAuthLDAPURL);
    authEnv.put("java.naming.security.authentication", "simple");
    authEnv.put("java.naming.security.principal", "uid="+strLDAPUserUid+",ou=people,ou=pg,o=world");
    authEnv.put("java.naming.security.credentials", EncryptCrypto.decryptString(strLDAPUserPassword));    
    //authEnv.put("java.naming.security.credentials", strLDAPUserPassword);
    authEnv.put("java.naming.security.protocol", "ssl");
    authEnv.put("com.sun.jndi.ldap.connect.pool", "true");
    authEnv.put("java.naming.referral", "ignore");
    LdapContext ldapCtx = null;
    try
    {
      ldapCtx = new InitialLdapContext(authEnv, null);
    }
    catch (AuthenticationException e)
    {
      errorMass = "Connection fail";
    }
    String strResult = "";
    try
    {
      SearchControls ctls = new SearchControls();
      int searchcope = 2;
      ctls.setSearchScope(searchcope);

      NamingEnumeration answer = ldapCtx.search(base, dn, ctls);
      if ((answer == null) || (!answer.hasMore())) {
        throw new Exception("Invalid User / Password");
      }
      SearchResult result = (SearchResult)answer.next();
      strResult = getDistinguishedName(ldapCtx, base, result);
    }
    catch (NamingException namEx)
    {
      errorMass = "Invalid User";
      namEx.printStackTrace();
    }
    ldapCtx.addToEnvironment("java.naming.security.principal", strResult);

    ldapCtx.addToEnvironment("java.naming.security.credentials", pwd);
    try
    {
      ldapCtx.getAttributes("", null);
    }
    catch (AuthenticationException ex)
    {
      errorMass = "Invalid Password";
      ex.printStackTrace();
    }
    ldapCtx.close();

    return errorMass;
  }

  protected String getDistinguishedName(LdapContext context, String base, SearchResult result)
    throws NamingException
  {
    if (result.isRelative())
    {
      NameParser parser = context.getNameParser("");
      Name contextName = parser.parse(context.getNameInNamespace());
      Name baseName = parser.parse(base);
      Name entryName = 
        parser.parse(new CompositeName(result.getName()).get(0));
      Name name = contextName.addAll(baseName);
      name = name.addAll(entryName);
      return name.toString();
    }
    String absoluteName = result.getName();
    try
    {
      NameParser parser = context.getNameParser("");
      URI userNameUri = new URI(absoluteName);
      String pathComponent = userNameUri.getPath();
      if (pathComponent.length() < 1) {
        throw new InvalidNameException(
          "Search returned unparseable absolute name: " + 
          absoluteName);
      }
      Name name = parser.parse(pathComponent.substring(1));
      return name.toString();
    }
    catch (URISyntaxException e) {
    }
    throw new InvalidNameException(
      "Search returned unparseable absolute name: " + 
      absoluteName);
  }

  public void callJPOForReport(Context context, String programName, String methodName, Map paramMap)
    throws Exception
  {
    try
    {
      JPO.invoke(context, programName, null, methodName, JPO.packArgs(paramMap));
    }
    catch (Exception ex)
    {
      System.out.println("----Error invoking JPO method " + programName + " : " + methodName + "-->" + ex.getMessage());
      throw ex;
    }
  }

  private class CommonXMLValidator extends DefaultHandler
  {
    public boolean validationError = false;
    public Exception saxParseException = null;

    private CommonXMLValidator() {  } 
    public void error(SAXParseException exception) throws SAXException { this.validationError = true;
      this.saxParseException = exception;
      throw exception; }

    public void fatalError(SAXParseException exception) throws SAXException {
      this.validationError = true;
      this.saxParseException = exception;
      throw exception;
    }

    public void warning(SAXParseException exception)
      throws SAXException
    {
    }
  }
}