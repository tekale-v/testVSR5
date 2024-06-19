/*
Java File Name: ReflectionUtility
Clone From/Reference: NA
Purpose: This file is used to retrieve the getter and setter method names
*/

package com.pdfview.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;

import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.exception.PDFToolFieldIsListException;

import matrix.db.Context;

public class ReflectionUtility {
	/**
	 * @description: Method returns an ArrayList of all the Getter methods on a
	 *               Class
	 * 
	 * @param c
	 * @return ArrayList
	 */
	public static ArrayList<Method> findGetters(Class<?> c) {
		ArrayList<Method> list = new ArrayList<Method>();
		Method[] methods = c.getDeclaredMethods();
		for (Method method : methods)
			if (isGetter(method))
				list.add(method);
		return list;
	}

	/**
	 * @description: Method returns a Method of a Getter on a Class
	 * 
	 * @param c
	 * @param variableName
	 * @return Method
	 */
	public static Method findGetter(Class<?> c, String variableName) {
		Method results = null;
		Method[] methods = c.getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().startsWith("is")) {
				if (method.getName().equals("is" + StringUtils.capitalize(variableName)))
					if (isGetter(method)) {
						results = method;
						break;
					}
			} else if (method.getName().startsWith("get")) {
				if (method.getName().equals("get" + StringUtils.capitalize(variableName)))
					if (isGetter(method)) {
						results = method;
						break;
					}
			}

		}
		return results;
	}

	/**
	 * @description: Method will find the Components
	 * 
	 * @param c
	 * @return Method
	 */
	public static Method findGetComponent(Class<?> c) {
		Method results = null;
		Method[] methods = c.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals("getComponent")) {
				results = method;
				break;
			}
		}
		return results;
	}

	/**
	 * @description: Checks a Method to make sure it is public and starts with
	 *               get(for non-boolean return types) or is(for boolean return
	 *               types)
	 * 
	 * @param method
	 * @return boolean
	 */
	public static boolean isGetter(Method method) {
		if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {
			if (method.getName().matches("^get[A-Z].*") && !method.getReturnType().equals(void.class))
				return true;
			if (method.getName().matches("^is[A-Z].*") && method.getReturnType().equals(boolean.class)
					|| method.getReturnType().equals(Boolean.class))
				return true;
		}
		return false;
	}

	/**
	 * @description: This method will return the Field definition for a specified
	 *               variable
	 * 
	 * @param c
	 * @param variableName
	 * @return Field
	 */
	public static Field getListField(Class<?> c, String variableName) {
		Field results = null;
		Field[] fields = c.getDeclaredFields();
		int iFieldsLength=fields.length;
		for (int i = 0; i < iFieldsLength; i++) {
			if (fields[i].getName().equalsIgnoreCase(variableName)) {
				results = fields[i];
				break;
			}
		}

		return results;
	}

	/**
	 * @description: This method will return the type of a list field
	 * 
	 * @param fTemp
	 * @return String
	 */
	public static String getFieldSubType(Field fTemp) {

		String sListInstance = fTemp.getGenericType().toString();
		String sListClass = getClassName(sListInstance); 
		return sListClass;
	}

	/**
	 * @description: This method will return the class name
	 * 
	 * @param fTgenericName
	 * @return String
	 */

	public static String getClassName(String genericName) {
		String sListClass = genericName.substring(genericName.indexOf("<") + 1, genericName.indexOf(">"));
		return sListClass;
	}

	/**
	 * @description: This method will create an instance of the specified class
	 * 
	 * @param sClassName
	 * @return Object
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object getObjectInstance(String sClassName) throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?> clazz;
		clazz = ReflectionUtility.getClass(sClassName);

		Object subobj = null;

		if (sClassName.equals("java.math.BigInteger")) {
			subobj = java.math.BigInteger.valueOf(0);
		} else if (sClassName.equals("java.math.BigDecimal")) {
			subobj = java.math.BigDecimal.valueOf(0);
		} else {
			Constructor<?> constructor = clazz.getConstructor();
			subobj = constructor.newInstance();
		}
		return subobj;
	}

	/**
	 * @description: This methods tries to load a class. If that fails, then it tries
	 *               to load -the class using the jar files in the configuration
	 *               directory
	 * 
	 * @param sClassName
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	public static Class getClass(String sClassName) throws ClassNotFoundException {
		Class result = null;

		try {
			result = Class.forName(sClassName);
		} catch (ClassNotFoundException e) {
			try {

				result = Class.forName(sClassName, true, PDFToolURLClassLoader.getClassLoader());
			} catch (ClassNotFoundException cnfe) {
				throw cnfe;
			}
		}
		return result;
	}

	/**
	 * @description: This method will get the specified list variable from an object
	 * 
	 * @param targetObject
	 * @param sListClass
	 * @return List
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<?> getListInstance(Object targetObject, String sListClass)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		String listGetter = "get" + StringUtils.capitalize(sListClass);
		Method getter = targetObject.getClass().getDeclaredMethod(listGetter);

		List<?> multiValuedList = (List<?>) getter.invoke(targetObject);
		return multiValuedList;
	}

	/**
	 * @description: This method will set the variable on object
	 * 
	 * @param object
	 * @param variableName
	 * @param value
	 * @return void
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws PDFToolCustomException
	 * @throws PDFToolFieldIsListException
	 */
	public static void setVariableOnObject(Object object, String variableName, Object value)
			throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchFieldException, PDFToolCustomException {

		if (null != object && null != value && !DHUtility.isEmptyText(value.toString())) {
			StringBuilder sb = new StringBuilder();
			sb.append("set").append(StringUtils.capitalize(variableName));

			String sCleanedVariableName = StringUtils.capitalize(variableName);
			sCleanedVariableName = StringUtils.remove(sCleanedVariableName, '_');
			Class<?> variableType = null;

			Method mGetter = findGetter(object.getClass(), sCleanedVariableName);

			if (mGetter != null) {
				variableType = mGetter.getReturnType();
				if (variableType.getName().endsWith("List")) {
					List multiValuedList = ReflectionUtility.getListInstance(object, variableName);
					if (multiValuedList != null) {
						multiValuedList.add(value);
					}

				} else if (variableType.getName().equals("java.math.BigDecimal")) {

					BigDecimal bd = BigDecimal.valueOf(Float.parseFloat((String) value));

					String sSetterName = "set" + sCleanedVariableName;

					Method mTemp = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());

					Class<?>[] params = mTemp.getParameterTypes();

					Object dummy = ReflectionUtility.getObjectInstance(params[0].getName());

					Method setter = object.getClass().getDeclaredMethod(mTemp.getName(), mGetter.getReturnType());

					setter.invoke(object, bd);

				} else if (variableType.getName().equals("boolean")) {
					String sSetterName = "set" + sCleanedVariableName;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, boolean.class);
					boolean bTemp = Boolean.parseBoolean((String) value);
					setter.invoke(object, bTemp);

				} else if (variableType.getName().equals("java.lang.Boolean")) {
					String sSetterName = "set" + sCleanedVariableName;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, Boolean.class);
					boolean bTemp = Boolean.parseBoolean((String) value);
					setter.invoke(object, bTemp);

				} else if (variableType.getName().equals("java.math.BigInteger")) {
					BigInteger bi = null;
					if (!value.equals(""))
						bi = BigInteger.valueOf(Long.parseLong((String) value));

					String sSetterName = "set" + sCleanedVariableName;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());
					setter.invoke(object, bi);

				} else if (variableType.getName().equals("java.lang.Integer")) {

					Integer iTemp = null;
					if (value != null && !value.equals(""))
						try {
							iTemp = Integer.valueOf((String) value);
						} catch (NumberFormatException e) {

						}

					String sSetterName = "set" + sCleanedVariableName;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());

					if (iTemp != null)
						setter.invoke(object, iTemp);
				} else if (variableType.getName().equals("int")) {
					String sSetterName = "set" + sCleanedVariableName;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());

					if (value != null && !value.equals(""))
						setter.invoke(object, value);

				} else if (variableType.getName().equals("javax.xml.datatype.XMLGregorianCalendar")) {

					String sSetterName = "set" + sCleanedVariableName;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
					String initialValue = (String) value;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());
					try {
						java.util.Date date = null;
						try {
							date = sdf.parse(initialValue);
						} catch (ParseException pe) {
							try {
								sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
								date = sdf.parse(initialValue);

							} catch (ParseException pe2) {
								try {
									sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									date = sdf.parse(initialValue);
								} catch (ParseException pe3) {

									sdf = new SimpleDateFormat("MMM dd, yyyy");
									date = sdf.parse(initialValue);
								}
							}
						}

						GregorianCalendar gc = new GregorianCalendar();
						gc.setTime(date);
						XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
						xmlCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
						xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
						setter.invoke(object, xmlCalendar);
					} catch (Exception exception) {
						exception.getMessage();
					}

				} else if (variableType.getName().equals("javax.xml.bind.JAXBElement")) {
					Object sTempValue = null;
					if (!value.equals(""))
						sTempValue = value;

					String jaxbElementType = getClassName(mGetter.getGenericReturnType().toString());
					String sSetterName = "set" + StringUtils.capitalize(variableName);
					Method setter = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());
					setter.invoke(object, getJaxbElementValue(variableName, jaxbElementType, sTempValue));

				} else {
					String sSetterName = "set" + sCleanedVariableName;

					Method setter = object.getClass().getDeclaredMethod(sSetterName, mGetter.getReturnType());

					Class<?>[] params = setter.getParameterTypes();

					try {
						if (value.equals(""))
							setter.invoke(object, null);
						else
							setter.invoke(object, value);
					} catch (java.lang.IllegalArgumentException iae) {
						iae.getMessage();
					}
				}

			}
		}
	}

	/**
	 * @description: This method will get the Jaxb Element value
	 * 
	 * @param variableName
	 * @param jaxbElementType
	 * @param value
	 * @return JAXBElement
	 * @throws ClassNotFoundException
	 */
	private static JAXBElement getJaxbElementValue(String variableName, String jaxbElementType, Object value)
			throws ClassNotFoundException {
		JAXBElement<?> jaxbValue = null;
		switch (jaxbElementType.toLowerCase()) {
		case "java.lang.string":
			jaxbValue = new JAXBElement<String>(new QName(variableName), String.class, value.toString());
			break;
		case "java.lang.integer":
			if (null != value && "" != value.toString()) {
				jaxbValue = new JAXBElement<Integer>(new QName(variableName), Integer.class,
						Integer.parseInt(value.toString()));
			}
			break;
		default:

			jaxbValue = new JAXBElement(new QName(variableName), getClass(jaxbElementType), value);
			break;
		}
		return jaxbValue;
	}

	/**
	 * @description: This method to execute Enovia helper class
	 * 
	 * @param context
	 * @param sOid
	 * @param sHelperClassName
	 * @return Object
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object executeEnoviaHelperClass(Context context, String sOid, String sHelperClassName)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object result = null;

		String sFQName = "com.pdfview.helper." + sHelperClassName;
		Class<?> classHelper = ReflectionUtility.getClass(sFQName);
		Object[] constrParams = { context, sOid };

		Constructor<?> constr = classHelper.getDeclaredConstructor(Context.class, String.class);
		Object objTemp = constr.newInstance(constrParams);
		Method method = ReflectionUtility.findGetComponent(classHelper);
		result = method.invoke(objTemp);
		return result;
	}

	/**
	 * @description: This method used to get Object Instance
	 * 
	 * @param obj
	 * @param sElementName
	 * @return Object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 */
	public static Object getObjectInstance(Object obj, String sElementName)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException,
			NoSuchMethodException, InstantiationException {
		Object results = null;
		Method method = ReflectionUtility.findGetter(obj.getClass(), sElementName);
		results = method.invoke(obj);

		if (results == null) {
			Class cReturn = method.getReturnType();
			results = getObjectInstance(cReturn.getName());
		}

		return results;
	}

	/**
	 * @description: This method check null items
	 * 
	 * @param object
	 * @return boolean
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static boolean containsNonNullItems(Object object)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		boolean bReturn = false;
		if (object != null) {
			Class clazz = object.getClass();
			ArrayList<Method> arMethods = ReflectionUtility.findGetters(clazz);

			for (Method method : arMethods) {
				Object objTemp = method.invoke(object);
				if (objTemp instanceof java.util.List) {
					if (!((List) objTemp).isEmpty()) {
						bReturn = true;
						break;
					}
				} else if (objTemp != null) {
					bReturn = true;
					break;
				}
			}

		}
		return bReturn;
	}
}
