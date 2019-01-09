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
package org.primaresearch.io.xml.variable;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerValue;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;
import org.primaresearch.shared.variable.constraints.MinMaxConstraint;
import org.primaresearch.shared.variable.constraints.ValidStringValues;
import org.primaresearch.shared.variable.VariableMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads variables from XML files.
 * 
 * @author Christian Clausner
 *
 */
public class XmlVariableFileReader {

	private SAXParser parser;
	private VariableHandler handler;

	
	public XmlVariableFileReader() {
		createParser();
	}
	
	/**
	 * Loads variables from the specified variable XML file.
	 * @param url XML file location
	 * @return Map with variables
	 */
	public VariableMap read(URL url) {
		parse(url);
				
		return handler.getVariableMap();
	}
		
	/**
	 * Creates the parser that finds the schema version only.
	 */
	private void createParser() {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
	    	
    		parser = factory.newSAXParser();
    		
    		handler = new VariableHandler();
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	
	private void parse(URL url) {
	    try{
	    	XMLReader reader = parser.getXMLReader();
	    	//reader.setErrorHandler(errorHandler); //TODO
	    	reader.setContentHandler(handler);
	    	org.xml.sax.InputSource saxInput = new org.xml.sax.InputSource(url.openStream());
	    	//saxInput.setEncoding("utf-8");
	    	reader.parse(saxInput);
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	}
	
	/**
	 * SAX handler for variables.
	 */
	private static class VariableHandler extends DefaultHandler {
		
		private static final int TYPE_DOUBLE 	= 1;
		private static final int TYPE_BOOL 		= 2;
		private static final int TYPE_INT 		= 3;
		private static final int TYPE_STRING 	= 4;
		
		private VariableMap variables = null;
		private Variable currentVar = null;
		private StringBuffer currentText = null;
		private ValidStringValues currentValidStringValues = null;
		private String insideElement = null;
		
		
		public VariableMap getVariableMap() {
			return this.variables;
		}		
		
		/**
		 * Receive notification of the start of an element.
		 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
		 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
		 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
		 * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
		 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
		 */
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		      throws SAXException {

			//Handle accumulated text
			finishText();

			insideElement = localName;

	    	int i;
		    if (VariableXmlNames.ELEMENT_Parameters.equals(localName)) {
		    	variables = new VariableMap();
		    	//Type
				if ((i = atts.getIndex(VariableXmlNames.ATTR_type)) >= 0) {
					variables.setType(atts.getValue(i));
				}
		    	//Name
				if ((i = atts.getIndex(VariableXmlNames.ATTR_name)) >= 0) {
					variables.setName(atts.getValue(i));
				}
		    }
		    else if (VariableXmlNames.ELEMENT_Parameter.equals(localName)) {
		    	createVariable(atts);
		    }
		    else if (VariableXmlNames.ELEMENT_Description.equals(localName)) {
		    	//Handled in finishText
		    }
		    else if (VariableXmlNames.ELEMENT_ValidValues.equals(localName)) {
		    	currentValidStringValues = new ValidStringValues();
		    }
		    else if (VariableXmlNames.ELEMENT_Value.equals(localName)) {
				if (currentValidStringValues != null && (i = atts.getIndex(VariableXmlNames.ATTR_value)) >= 0) {
					currentValidStringValues.addValidValue(atts.getValue(i));
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
			
			//Handle accumulated text
			finishText();

		    if (VariableXmlNames.ELEMENT_ValidValues.equals(localName)) {
		    	if (currentVar != null && currentValidStringValues != null 
		    			&& currentVar instanceof StringVariable)
		    		currentVar.setConstraint(currentValidStringValues);
		    }

			insideElement = null;

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

			String strValue = new String(ch, start, length);
			
			//Text might be parsed bit by bit, so we have to accumulate until a closing tag is found.
			if (currentText == null)
				currentText = new StringBuffer();
			currentText.append(strValue);
		}
		
		/**
		 * Writes accumulated text to the right object. 
		 */
		private void finishText() {
			if (currentText != null) {
				String strValue = currentText.toString();
				
				if (currentVar != null && VariableXmlNames.ELEMENT_Description.equals(insideElement)) {
					currentVar.setDescription(strValue);
				}

				currentText = null;
			}
		}
		
		private void createVariable(Attributes atts) {
			Variable var = null;
			int i;
			
			//Name
			String name = "";
			if ((i = atts.getIndex(VariableXmlNames.ATTR_name)) >= 0) {
				name = atts.getValue(i);
			}

			//Type
			if ((i = atts.getIndex(VariableXmlNames.ATTR_type)) >= 0) {
				int type = Integer.parseInt(atts.getValue(i));
				if (type == TYPE_BOOL)
					var = new BooleanVariable(name);
				else if (type == TYPE_DOUBLE)
					var = new DoubleVariable(name);
				else if (type == TYPE_INT)
					var = new IntegerVariable(name);
				else if (type == TYPE_STRING)
					var = new StringVariable(name);
				else
					return; //Unknown or not supported type
			}
			else
				return; //Type is mandatory
			
			currentVar = var;
			
			//ID
			if ((i = atts.getIndex(VariableXmlNames.ATTR_id)) >= 0) {
				var.setId(Integer.parseInt(atts.getValue(i)));
			}
			
			//Caption
			if ((i = atts.getIndex(VariableXmlNames.ATTR_caption)) >= 0) {
				var.setCaption(atts.getValue(i));
			}
			
			//Version
			if ((i = atts.getIndex(VariableXmlNames.ATTR_version)) >= 0) {
				var.setVersion(Integer.parseInt(atts.getValue(i)));
			}
			
			//Sort index
			if ((i = atts.getIndex(VariableXmlNames.ATTR_sortIndex)) >= 0) {
				var.setSortIndex(Integer.parseInt(atts.getValue(i)));
			}
			
			//Read only
			if ((i = atts.getIndex(VariableXmlNames.ATTR_readOnly)) >= 0) {
				var.setReadOnly(Boolean.parseBoolean(atts.getValue(i)));
			}
			
			//Visible
			if ((i = atts.getIndex(VariableXmlNames.ATTR_visible)) >= 0) {
				var.setVisible(Boolean.parseBoolean(atts.getValue(i)));
			}
			
			//Min, Max, Step for Int variables
			if (var instanceof IntegerVariable) {
				int min = Integer.MIN_VALUE;
				int max = Integer.MAX_VALUE;
				if ((i = atts.getIndex(VariableXmlNames.ATTR_min)) >= 0) {
					min = Integer.parseInt(atts.getValue(i));
				}
				if ((i = atts.getIndex(VariableXmlNames.ATTR_max)) >= 0) {
					max = Integer.parseInt(atts.getValue(i));
				}
				if (min != 0 || max != 0)
					var.setConstraint(new MinMaxConstraint(new IntegerValue(min), new IntegerValue(max)));
				
				//Step
				if ((i = atts.getIndex(VariableXmlNames.ATTR_step)) >= 0) {
					Integer step = Integer.parseInt(atts.getValue(i));
					if (step != null && step > 0) //Only if not 0
						((IntegerVariable)var).setStep(step);
				}
			}
			
			//Min, Max, Step for Double variables
			if (var instanceof DoubleVariable) {
				double min = Double.MIN_VALUE;
				double max = Double.MAX_VALUE;
				if ((i = atts.getIndex(VariableXmlNames.ATTR_min)) >= 0) {
					min = Double.parseDouble(atts.getValue(i));
				}
				if ((i = atts.getIndex(VariableXmlNames.ATTR_max)) >= 0) {
					max = Double.parseDouble(atts.getValue(i));
				}
				if (min != 0.0 || max != 0.0)
					var.setConstraint(new MinMaxConstraint(new DoubleValue(min), new DoubleValue(max)));
				
				//Step
				if ((i = atts.getIndex(VariableXmlNames.ATTR_step)) >= 0) {
					Double step = Double.parseDouble(atts.getValue(i));
					if (step != null && step > 0.0) //Only if not 0
						((DoubleVariable)var).setStep(step);
				}
			}
			
			//Text type
			if (var instanceof StringVariable && (i = atts.getIndex(VariableXmlNames.ATTR_textType)) >= 0) {
				((StringVariable)var).setTextType(Integer.parseInt(atts.getValue(i)));
			}
			
			//Value
			if ((i = atts.getIndex(VariableXmlNames.ATTR_value)) >= 0) {
				var.parseValue(atts.getValue(i));
			}
		
			//Is set
			if ((i = atts.getIndex(VariableXmlNames.ATTR_isSet)) >= 0) {
				boolean isSet = Boolean.parseBoolean(atts.getValue(i));
				if (!isSet) {
					try {
						var.setValue(null);
					} catch (WrongVariableTypeException e) {
						e.printStackTrace();
					}
				}
			}
			
			variables.add(var);

		}
	}
}
