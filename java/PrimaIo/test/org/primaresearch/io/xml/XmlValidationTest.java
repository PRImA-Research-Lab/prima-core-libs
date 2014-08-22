/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidationTest {

	@Test
	public void testDomBuilderXmlValidation() {
		
		//Build up DOM tree
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        dbfac.setValidating(false);
        dbfac.setNamespaceAware(true);

        DocumentBuilder docBuilder;
		try {
			docBuilder = dbfac.newDocumentBuilder();
			DOMImplementation domImpl = docBuilder.getDOMImplementation();

			String namespace = "org.primaresearch.io.xml.XmlValidationTest";
			String schemaLocation = "c:\\junit\\testSchema.xsd";

			
			//Create document
			Document doc = domImpl.createDocument(namespace, "PcGts", null);
	
			Element root = doc.getDocumentElement();
			root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", schemaLocation);

			//root.setAttribute("pcGtsId", "page123");
			root.setAttributeNS(null, "pcGtsId", "page123");
			//root.setAttributeNS(namespace, "pcGtsId", "page123");
			
			
			//Load schema
			Schema schema = null;
    		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				File schemaSource = new File("c:\\junit\\testSchema.xsd");
				Source src = new StreamSource(schemaSource);
				schema = schemaFactory.newSchema(src);
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
			//Validate XML file directly
	       	Validator domVal = schema.newValidator();
         	MyErrorHandler errorHandler = new MyErrorHandler();
			Source xmlFile = new StreamSource(new File("c:\\junit\\validationTestContent.xml"));
        	try {
				domVal.validate(xmlFile);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
	        if (errorHandler.hasErrors()) {
	        	for (int i=0; i<errorHandler.errors.size(); i++) {
	        		System.out.println(errorHandler.errors.get(i).getMessage());
	        	}
	        	fail("XML file validation errors");
	        }
	        
	        //Load XML file into DOM
	        Document loadedDoc = docBuilder.parse(new File("c:\\junit\\validationTestContent.xml"));
         	errorHandler = new MyErrorHandler();
        	domVal.setErrorHandler(errorHandler);

        	try {
				domVal.validate(new DOMSource(loadedDoc));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
	        if (errorHandler.hasErrors()) {
	        	for (int i=0; i<errorHandler.errors.size(); i++) {
	        		System.out.println(errorHandler.errors.get(i).getMessage());
	        	}
	        	fail("Loaded DOM validation erros");
	        }

			
			//Validate DOM
         	errorHandler = new MyErrorHandler();
        	domVal.setErrorHandler(errorHandler);

        	try {
				domVal.validate(new DOMSource(doc));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
	        if (errorHandler.hasErrors()) {
	        	for (int i=0; i<errorHandler.errors.size(); i++) {
	        		System.out.println(errorHandler.errors.get(i).getMessage());
	        	}
	        	fail("Created DOM validation erros");
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class MyErrorHandler implements ErrorHandler {
		
		List<IOError> errors = new ArrayList<IOError>();
		List<IOError> warnings = new ArrayList<IOError>();

		@Override
		public void error(SAXParseException exc) throws SAXException {
			errors.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
		}

		@Override
		public void fatalError(SAXParseException exc) throws SAXException {
			errors.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
		}

		@Override
		public void warning(SAXParseException exc) throws SAXException {
			warnings.add(new XmlValidationError(exc.getMessage(), "Line "+exc.getLineNumber()+", Column: "+exc.getColumnNumber()));
		}
		
		public boolean hasErrors() {
			return !errors.isEmpty();
		}


		
	}
}
