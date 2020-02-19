/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.io.xml;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.io.FormatVersion;
import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.constraints.ValidStringValues;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Default XML schema parser handling complex and simple types.
 * 
 * @author Christian Clausner
 *
 */
public class DefaultSchemaParser implements SchemaModelParser {

	private SchemaHandler handler;
	private SAXParser parser;
	private XmlFormatVersion formatVersion;

	/**
	 * Constructor
	 * @param formatVersion Format version this parser is intended for
	 */
	public DefaultSchemaParser(XmlFormatVersion formatVersion) {
		this.formatVersion = formatVersion;
	    this.handler = new SchemaHandler();
	    createParser();
	}
	
	@Override
	public FormatVersion getVersion() {
		return formatVersion;
	}

	/**
	 * Returns attribute templates for all types that were found in the XML schema
	 * @return Map [type name, attribute templates]
	 */
	public Map<String, VariableMap> getTypeAttributeTemplates() {
		return handler.typeAttrMap;
	}

	/**
	 * Creates the SAX parser for PAGE XML schema.
	 */
	private void createParser() {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
	    	
	    	// Creates a new instance of a SAXParser using the currently configured factory parameters.
    		parser = factory.newSAXParser();

	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	
	/**
	 * Parses a PAGE XML schema file
	 */
	public void parse(URL schemaLocation){
	    try{
	    	XMLReader reader = parser.getXMLReader();
	    	//reader.setErrorHandler(errorHandler);
	    	reader.setContentHandler(handler);
	    	reader.parse(new org.xml.sax.InputSource(schemaLocation.openStream()));
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	}
	
	@Override
	public VariableMap filterAttributes(VariableMap allAttributes, String typeFilter) {
		VariableMap filtered = new VariableMap();
		
		VariableMap templates = handler.typeAttrMap.get(typeFilter);
		
		if (templates != null) {
			for (int i=0; i<allAttributes.getSize(); i++) {
				Variable attr = allAttributes.get(i);
				if (templates.get(attr.getName()) != null)
					filtered.add(attr);
			}
		}
		
		return filtered;
	}
	

	
	
	/**
	 * SAX handler implementation for PAGE XML schema
	 * 
	 * @author Christian Clausner
	 */
	private static class SchemaHandler extends DefaultHandler {
		
		private Map<String, VariableMap> typeAttrMap = new HashMap<String, VariableMap>(); // Map [type name, attribute templates]
		private Map<String, Map<String, String>> pendingSimpleType = new HashMap<String, Map<String, String>>();
		private Map<String, Map<String, SimpleType>> simpleTypes = new HashMap<String, Map<String, SimpleType>>(); //Map [scope, Map[name, type]]
		private VariableMap currentAttributeMap = null; 
		private SimpleType currentSimpleType = null;
		private String currentType = null;
		private Map<String,String> inheritenceMap = new HashMap<String,String>(); //[Child type, Parent type]
		private String pendingAttributeWithInlineType = null;
		
		
		/**
		 * Receive notification of the start of an element.
		 * 
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		      throws SAXException {
			
		    if ("complexType".equals(localName)){
		    	String typeName = atts.getValue("name");
		    	currentType = typeName;
		    	if (!typeAttrMap.containsKey(typeName)) {
		    		currentAttributeMap = new VariableMap();
		    		currentAttributeMap.setType(typeName);
		    		typeAttrMap.put(typeName, currentAttributeMap);
		    	}
		    }
		    else if ("attribute".equals(localName)){
		    	String attrName = atts.getValue("name");
		    	addAttribute(attrName, atts);
		    }
		    else if ("simpleType".equals(localName)) {
		    	
		    	//Inline simpleType?
		    	if (pendingAttributeWithInlineType != null) {
			    	String name = pendingAttributeWithInlineType+"Type";
			    	currentSimpleType = new SimpleType(name);
			    	
			    	Map<String, SimpleType> localSimpleTypes = simpleTypes.get(currentType);
			    	if (localSimpleTypes == null)
			    	{
			    		localSimpleTypes = new HashMap<String,SimpleType>();
			    		simpleTypes.put(currentType, localSimpleTypes);
			    	}
			    	localSimpleTypes.put(name, currentSimpleType);
			    	
					Map<String, String> attsWithPendingType = pendingSimpleType.get(currentAttributeMap.getType());
					if (attsWithPendingType == null) {
						attsWithPendingType = new HashMap<String, String>();
						pendingSimpleType.put(currentAttributeMap.getType(), attsWithPendingType);
					}
					attsWithPendingType.put(pendingAttributeWithInlineType, pendingAttributeWithInlineType+"Type");
		    	}
		    	else {
			    	String name = atts.getValue("name");
			    	currentSimpleType = new SimpleType(name);
			    	
			    	Map<String, SimpleType> localSimpleTypes = simpleTypes.get("");
			    	if (localSimpleTypes == null)
			    	{
			    		localSimpleTypes = new HashMap<String,SimpleType>();
			    		simpleTypes.put("", localSimpleTypes);
			    	}
			    	localSimpleTypes.put(name, currentSimpleType);
		    	}
		    }
		    else if ("restriction".equals(localName)) {
		    	handleRestriction(atts);
		    }
		    else if ("enumeration".equals(localName)) {
		    	handleEnumeration(atts);
		    }
		    else if ("extension".equals(localName)) { //Inheritance
		    	//Get parent type
				int i;
				if ((i = atts.getIndex("base")) >= 0) {
					String parentTypeString  = atts.getValue(i);
					int pos = parentTypeString.indexOf(":");
					if (pos >= 0)
						parentTypeString = parentTypeString.substring(pos+1);
					inheritenceMap.put(currentType, parentTypeString);
				}
		    }
		}
		
		/**
		 * Receive notification of the end of an element.
		 * 
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void endElement(String namespaceURI, String localName, String qName)
		      throws SAXException {
		    if ("complexType".equals(localName)){
		    	currentAttributeMap = null;
		    	currentType = null;
		    }
		    else if ("simpleType".equals(localName)) {
		    	currentSimpleType = null;
		    }
		    else if ("schema".equals(localName)) {
		    	processInheritanceMap();
		    }
		    else if ("attribute".equals(localName)) {
		    	pendingAttributeWithInlineType = null;
		    }
		}
		
		/**
		 * Copies the attributes of parent types to their children
		 */
		private void processInheritanceMap() {
			for (Iterator<String> it = inheritenceMap.keySet().iterator(); it.hasNext(); ) {
				String childType = it.next();
				String parentType = inheritenceMap.get(childType);
				
				if (!childType.isEmpty() && !parentType.isEmpty()) {
					VariableMap childAttrs = typeAttrMap.get(childType);
					VariableMap parentAttrs = typeAttrMap.get(parentType);
					if (childAttrs != null && parentAttrs != null) {
						for (int i=0; i<parentAttrs.getSize(); i++) {
							Variable attr = parentAttrs.get(i);
							if (attr != null && childAttrs.get(attr.getName()) == null) {
								childAttrs.add(attr);
							}
						}
					}
				}
			}
		}
		
		/**
		 * Receive notification of character data inside an element.
		 * @param ch - The characters.
		 * @param start - The start position in the character array.
		 * @param length - The number of characters to use from the character array.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void characters(char[] ch, int start, int length)
		      throws SAXException {
		}
		
		@Override
		public void endDocument() {
			handlePendingSimpleTypes();
		}
		
		private void addAttribute(String name, Attributes atts) {
			if (currentAttributeMap == null)
				return;
			String type = atts.getValue("type");
			if (type == null) //This can happen if the type is defined inline within the attribute
			{
				pendingAttributeWithInlineType = /*"" + currentType + "_" +*/ name;
				return;
			}
			Variable attr = null;
			if (type.endsWith("SimpleType")) { //Handle later when all simple types have been parsed 
				Map<String, String> attsWithPendingType = pendingSimpleType.get(currentAttributeMap.getType());
				if (attsWithPendingType == null) {
					attsWithPendingType = new HashMap<String, String>();
					pendingSimpleType.put(currentAttributeMap.getType(), attsWithPendingType);
				}
				attsWithPendingType.put(name, type);
			} else
				attr = createAttributeVariable(type, name);
			//Add new attribute to attribute map of current complex type
			if (attr != null) {
				currentAttributeMap.add(attr);
			}
		}
		
		private static Variable createAttributeVariable(String type, String name) {
			if ("int".equals(type) || "integer".equals(type))
				return new IntegerVariable(name, null);
			if ("float".equals(type))
				return new DoubleVariable(name, null);
			if ("boolean".equals(type))
				return new BooleanVariable(name, null);
			if ("string".equals(type) || "dateTime".equals(type))
				return new StringVariable(name, null);
			return null;
		}
		
		private void handleRestriction(Attributes atts) {
			if (currentSimpleType != null) {
				String baseType = atts.getValue("base");
				if (baseType != null)
					currentSimpleType.setVariableType(baseType);
			}
		}
		
		/**
		 * Handles enumerations of attribute value restrictions
		 */
		private void handleEnumeration(Attributes atts) {
			if (currentSimpleType != null && currentSimpleType.variable != null) {
				if (currentSimpleType.variable instanceof StringVariable
						&& (currentSimpleType.variable.getConstraint() == null ||
						    currentSimpleType.variable.getConstraint() instanceof ValidStringValues)) {
					ValidStringValues constraint;
					constraint = (ValidStringValues)currentSimpleType.variable.getConstraint();
					if (constraint == null) {
						constraint = new ValidStringValues();
						currentSimpleType.variable.setConstraint(constraint);
					}
					constraint.addValidValue(atts.getValue("value"));
				}
			}
		}
		
		private void handlePendingSimpleTypes() {
			for (Iterator<Entry<String, Map<String, String>>> it = pendingSimpleType.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Map<String, String>> entry = it.next();
				String complexType = entry.getKey();
				Map<String, String> attsWithPendingType = entry.getValue();
				VariableMap attributeMap = typeAttrMap.get(complexType);
				
				if (attsWithPendingType != null && attributeMap != null) {
					for (Iterator<Entry<String, String>> it2 = attsWithPendingType.entrySet().iterator(); it2.hasNext() ;) {
						Entry<String,String> entry2 = it2.next();
						String attrName = entry2.getKey();
						String simpleTypeName = entry2.getValue();
						if (simpleTypeName.contains(":"))
							simpleTypeName = simpleTypeName.substring(simpleTypeName.indexOf(":")+1);
												
						SimpleType simpleType = null;
						Map<String, SimpleType> localSimpleTypes = simpleTypes.get(attributeMap.getType());
						if (localSimpleTypes != null)
							simpleType = localSimpleTypes.get(simpleTypeName);
						
						if (simpleType == null)
						{
							localSimpleTypes = simpleTypes.get("");
							if (localSimpleTypes != null)
								simpleType = localSimpleTypes.get(simpleTypeName);
						}
						
						if (simpleType != null && attrName != null) {
							Variable variable = simpleType.getVariable();
							if (variable != null) {
								Variable clone = variable.clone();
								clone.setName(attrName);
								attributeMap.add(clone);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * XML schema simple type
	 * @author Christian Clausner
	 *
	 */
	private static class SimpleType {
		
		private String name;
		private Variable variable = null; 
		
		public Variable getVariable() {
			return variable;
		}

		public SimpleType(String name) {
			this.name = name;
		}
		
		public void setVariableType(String type) {
			variable = SchemaHandler.createAttributeVariable(type, name);
		}
	}


}
