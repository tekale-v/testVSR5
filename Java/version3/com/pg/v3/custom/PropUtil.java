package com.pg.v3.custom;

import java.io.PrintStream;
import java.util.Properties;
import java.io.*;

public class PropUtil
{
  private Properties prop = null;
  
  public PropUtil()
    throws Exception
  {
    this.prop = new Properties();
    loadProperties();
  }
  
  private void loadProperties()
    throws Exception
  {
	File file;
	
	File jarPath=new File(PropUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    String propertiesPath=jarPath.getParentFile().getAbsolutePath();

	file = new File(propertiesPath+"/../classes");
	propertiesPath = file.getCanonicalPath();
	
    prop.load(new FileInputStream(propertiesPath+"/pgv3SpecificRelationship.properties"));
  }
  
  public String getProperty(String name)
  {
    name = name.replace(" ", "_");
    String value = this.prop.getProperty(name);
    return value == null ? "" : value;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    PropUtil pu = new PropUtil();
    System.out.println(pu.getProperty("LogFolder"));
  }
}