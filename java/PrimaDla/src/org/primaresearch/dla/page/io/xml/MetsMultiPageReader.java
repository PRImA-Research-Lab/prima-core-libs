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
package org.primaresearch.dla.page.io.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.InputSource;
import org.primaresearch.dla.page.io.UrlInput;
import org.primaresearch.io.xml.IOError;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Reader for multiple page file defined in METS XML format.
 * 
 * @author Christian Clausner
 *
 */
public class MetsMultiPageReader {
	
	private SAXParser parser;
	private SaxMetsHandler metsHandler = null;
	private PageErrorHandler lastErrors;

	public MetsMultiPageReader() {
		createParser();
	}

	public List<String> read(InputSource source) {
		
		lastErrors = new PageErrorHandler();
		
		parse(source, lastErrors);
		
		List<String> pageFiles = null;
		
		if (!lastErrors.hasErrors())
			pageFiles = metsHandler.getPageFiles();
		
		return pageFiles;
	}
	
	/**
	 * Parses a METS file
	 */
	private void parse(InputSource input, PageErrorHandler errorHandler) {
	    try{
	    	XMLReader reader = parser.getXMLReader();
	    	reader.setErrorHandler(errorHandler);
	    	reader.setContentHandler(metsHandler);
	    	InputStream inputStream = getInputStream(input);
	    	if (inputStream == null)
	    		return;
	    	org.xml.sax.InputSource saxInput = new org.xml.sax.InputSource(inputStream);
	    	//saxInput.setEncoding("utf-8");
	    	reader.parse(saxInput);
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	
	/**
	 * Creates the SAX parser for METS XML.
	 */
	private void createParser() {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
	    	
	    	this.metsHandler = new SaxMetsHandler();
		    
	    	// Creates a new instance of a SAXParser using the currently configured factory parameters.
	    	parser = factory.newSAXParser();

	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	
	private InputStream getInputStream(InputSource source) {
		if (source instanceof FileInput) {
			File f = ((FileInput)source).getFile();
			try {
				return new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				lastErrors.getErrors().add(new IOError("Could not open stream from file: "+e.getMessage()));
			} 
		} 
		else if (source instanceof UrlInput) {
			try {
				return ((UrlInput)source).getUrl().openStream();
			} catch (IOException e) {
				e.printStackTrace();
				lastErrors.getErrors().add(new IOError("Could not open stream from URL: "+e.getMessage()));
			}
		}
		else 
			throw new IllegalArgumentException("Only FileInput and UrlInput allowed for MetsMultiPageReader");
		return null;
	}
	
	
	/**
	 * SAX handler implementation to parse METS.
	 * 
	 * @author Christian Clausner
	 */
	private static class SaxMetsHandler extends DefaultHandler {
		
		private static final String ELEMENT_FLocat = "FLocat";
		private static final String ATTR_href = "xlink:href";
		
		private List<String> pageFiles = new ArrayList<String>();
		
		public List<String> getPageFiles() {
			return pageFiles;
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
			
		    if (ELEMENT_FLocat.equals(localName)){
		    	int i;
		    	if ((i = atts.getIndex(ATTR_href)) >= 0) {
		    		pageFiles.add(atts.getValue(i));
				}
		    }
		}
	}
}
