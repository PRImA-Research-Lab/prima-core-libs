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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.constraints.MinMaxConstraint;
import org.primaresearch.shared.variable.constraints.ValidStringValues;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * XML Writer for variables. 
 * 
 * @author Christian Clausner
 *
 */
public class XmlVariableFileWriter {

	private Document doc;
	private VariableMap vars; 
	

	/**
	 * Writes the given variables to the specified file.
	 * @param vars Variables to write
	 * @param target File location
	 */
	public void write(VariableMap vars, File target) {
		this.vars = vars;
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		dbfac.setValidating(false);
		dbfac.setNamespaceAware(true);

		DocumentBuilder docBuilder;
		try {
			docBuilder = dbfac.newDocumentBuilder();
			//docBuilder.setErrorHandler(lastErrors);

			doc = docBuilder.newDocument();

			writeRoot();

			//Write XML
			File f = target;
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			DOMSource source = new DOMSource(doc);
			OutputStream fos = new FileOutputStream(f);
			StreamResult result = new StreamResult(fos);
			trans.transform(source, result);
            fos.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeRoot() {
		
		Element root = doc.createElement(VariableXmlNames.ELEMENT_Parameters);
		doc.appendChild(root);
		
		//Type
		if (vars.getType() != null) 
			addAttribute(root, VariableXmlNames.ATTR_type, vars.getType());

		//Type
		if (vars.getType() != null) 
			addAttribute(root, VariableXmlNames.ATTR_name, vars.getName());

		for (int i=0; i<vars.getSize(); i++) {
			Variable v = vars.get(i);
			addVariable(v,root);
		}
	}
	
	private void addAttribute(Element node, String name, String value) {
		node.setAttribute(name, value);
	}
	
	private void addVariable(Variable v, Element parent) {
		Element node = doc.createElement(VariableXmlNames.ELEMENT_Parameter);
		parent.appendChild(node);

		//Type
		int type = 0;
		if (v instanceof DoubleVariable)
			type = 1;
		else if (v instanceof BooleanVariable)
			type = 2;
		else if (v instanceof IntegerVariable)
			type = 3;
		else if (v instanceof StringVariable)
			type = 4;
		node.setAttribute(VariableXmlNames.ATTR_type, ""+type);
		
		//ID
		node.setAttribute(VariableXmlNames.ATTR_id, ""+v.getId());
		
		//Name
		node.setAttribute(VariableXmlNames.ATTR_name, v.getName());
		
		//Caption
		if (v.getCaption() != null && !v.getCaption().isEmpty())
			node.setAttribute(VariableXmlNames.ATTR_caption, v.getCaption());
		
		//Sort index
		node.setAttribute(VariableXmlNames.ATTR_sortIndex, ""+v.getSortIndex());
		
		//Visible
		node.setAttribute(VariableXmlNames.ATTR_visible, v.isVisible() ? "true" : "false");
		
		//Read only
		node.setAttribute(VariableXmlNames.ATTR_readOnly, v.isReadOnly() ? "true" : "false");
		
		//Version
		node.setAttribute(VariableXmlNames.ATTR_version, ""+v.getVersion());
		
		//Description
		if (v.getDescription() != null)
			addTextElement(node, VariableXmlNames.ELEMENT_Description, v.getDescription());

		
		//Min/max
		if (v.getConstraint() != null && v.getConstraint() instanceof MinMaxConstraint) {
			MinMaxConstraint constraint = (MinMaxConstraint)v.getConstraint();
			node.setAttribute(VariableXmlNames.ATTR_min, ""+constraint.getMin());
			node.setAttribute(VariableXmlNames.ATTR_max, ""+constraint.getMax());
		}
		
		//Step
		if (v instanceof IntegerVariable) {
			Integer step = ((IntegerVariable)v).getStep();
			if (step != null)
				node.setAttribute(VariableXmlNames.ATTR_step, ""+step);
		}
		else if (v instanceof DoubleVariable) {
			Double step = ((DoubleVariable)v).getStep();
			if (step != null)
				node.setAttribute(VariableXmlNames.ATTR_step, ""+step);
		}
		
		//Text type
		if (v instanceof StringVariable) {
			StringVariable sv = (StringVariable)v;
			if (sv.getTextType() != 0)
				node.setAttribute(VariableXmlNames.ATTR_textType, ""+sv.getTextType());
			
			//Valid values
			if (v.getConstraint() != null && v.getConstraint() instanceof ValidStringValues) {
				ValidStringValues vals = (ValidStringValues)sv.getConstraint();
				if (!vals.getValidValues().isEmpty()) {
					Element valuesNode = doc.createElement(VariableXmlNames.ELEMENT_ValidValues);
					node.appendChild(valuesNode);
					for (Iterator<String> it = vals.getValidValues().iterator(); it.hasNext(); ) {
						Element valueNode = doc.createElement(VariableXmlNames.ELEMENT_Value);
						valuesNode.appendChild(valueNode);
						valueNode.setAttribute(VariableXmlNames.ATTR_value, it.next());
					}
				}
			}
		}
		
		//Is set
		node.setAttribute(VariableXmlNames.ATTR_isSet, v.getValue() != null ? "true" : "false");
		
		//Value
		if (v.getValue() != null) {
			node.setAttribute(VariableXmlNames.ATTR_value, ""+v.getValue());
		}
		
	}
	
	/**
	 * Writes a single element with text content.
	 */
	private void addTextElement(Element parent, String elementName, String text) /*throws XMLStreamException*/ {
		Element node = doc.createElement(elementName);
		parent.appendChild(node);

		Text textNode = doc.createTextNode(text != null ? text : "");
		node.appendChild(textNode);
	}

}
