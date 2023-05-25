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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.InputSource;
import org.primaresearch.dla.page.io.PageReader;
import org.primaresearch.dla.page.io.PageReaderBase;
import org.primaresearch.dla.page.io.xml.sax.SaxPageHandler;
import org.primaresearch.dla.page.io.xml.sax.SaxPageHandlerFactory;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.IOError;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider;
import org.primaresearch.io.xml.XmlValidator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Page reader implementation for XML files (supports validation against schema).
 * 
 * @author Christian Clausner
 */
public class XmlPageReader extends PageReaderBase implements PageReader {
	
	/** Constant for recognising a shortcut out of parsing. */
	private static final String PARSING_COMPLETE = "PARSING_COMPLETE";
	
	private SaxPageHandler pageHandler = null;
	private SAXParser mainParser;
	private SchemaVersionHandler schemaVersionHandler;
	private SAXParser schemaVersionParser;
	private XmlModelAndValidatorProvider validatorProvider;
	private XmlFormatVersion schemaVersion = null;

	/**
	 * Constructor
	 * @param validatorProvider Schema validator provider. (optional, set to null if no validation required).
	 */
	public XmlPageReader(XmlModelAndValidatorProvider validatorProvider) {
	    this.validatorProvider = validatorProvider;
	    if (validatorProvider != null) {
	    	createSchemaVersionParser();
	    	schemaVersionHandler = new SchemaVersionHandler();
	    }
	    try {
			createMainParser();
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace(); //Cannot happen here, as we don't have the schema version yet...
		}
	}
	
	/**
	 * Creates the SAX parser for PAGE XML.
	 * @throws UnsupportedFormatVersionException 
	 */
	private void createMainParser() throws UnsupportedFormatVersionException {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
    		//Fix for delay when reading HOCR (disables loading the external DTD that is defined in the HOCR file)
    		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    	
	    	//Validation
	    	if (validatorProvider != null && schemaVersion != null) {
	    		// Specifies that the parser produced by this code will validate documents as they are parsed.
	    		XmlValidator validator = validatorProvider.getValidator(schemaVersion);
	    		if (validator != null)
	    			factory.setSchema(validator.getSchema());
	    	}
	    	
		    //this.pageHandler = new PageHandler(validatorProvider, schemaVersion);
	    	this.pageHandler = SaxPageHandlerFactory.createHandler(validatorProvider, schemaVersion);
		    
	    	// Creates a new instance of a SAXParser using the currently configured factory parameters.
	    	mainParser = factory.newSAXParser();

	    } catch (UnsupportedFormatVersionException exc) {
	    	throw exc;
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	
	/**
	 * Creates the parser that finds the schema version only.
	 */
	private void createSchemaVersionParser() {
	    try {
	    	// Obtain a new instance of a SAXParserFactory.
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	    	// Specifies that the parser produced by this code will provide support for XML namespaces.
	    	factory.setNamespaceAware(true);
    		factory.setValidating(false);
    		//Fix for delay when reading HOCR (disables loading the external DTD that is defined in the HOCR file)
    		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    	
    		schemaVersionParser = factory.newSAXParser();
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	}
	  
	/**
	 * Reads a PAGE XML file and returns a Page object.
	 * 
	 * @param source FileInput representing an XML file
	 * @return Page object or null in case of errors (see getErrors()).
	 * @throws IllegalArgumentException Wrong input source type
	 */
	@Override
	public Page read(InputSource source) throws UnsupportedFormatVersionException {

		
		lastErrors = new PageErrorHandler();
		
		parse(source, lastErrors);
		
		Page page = null;
		
		if (lastErrors.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			for (IOError error : getErrors())
				sb.append(error.getMessage());
			throw new UnsupportedFormatVersionException(sb.toString());
		}

		page = pageHandler.getPageObject();
		
		//if (!MeasurementUnit.PIXEL.equals(pageHandler.getMeasurementUnit()))
			
		
		return page;
	}

	
	/**
	 * Returns a list of errors that occurred on the last call of read(). 
	 */
	public List<IOError> getErrors() {
		return lastErrors != null ? lastErrors.getErrors() : null;
	}

	/**
	 * Returns a list of warnings that occurred on the last call of read(). 
	 */
	public List<IOError> getWarnings() {
		return lastErrors != null ? lastErrors.getWarnings() : null;
	}

	/**
	 * Parses a PAGE file
	 */
	private void parse(InputSource input, PageErrorHandler errorHandler) throws UnsupportedFormatVersionException {
		//Validation?
		if (validatorProvider != null) {
			InputStream inputStream = null;
			try {
		    	inputStream = getInputStream(input);
		    	if (inputStream == null)
		    		return;
				schemaVersionParser.parse(inputStream, schemaVersionHandler);
				//We shortcut the parsing with an exception (see below)
			} catch (SAXException e) {
				if (PARSING_COMPLETE.equals(e.getMessage())) { //Shortcut when no more parsing is required.
					XmlFormatVersion version = schemaVersionHandler.getSchemaVersion();
					if (version == null || !version.equals(schemaVersion)) {
						schemaVersion = version;
						createMainParser(); //If the schema version has changed, we have to create a new parser.
					}
				}
				else
					e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}			
		}
		
		InputStream inputStream = null;
	    try{
	    	XMLReader reader = mainParser.getXMLReader();
	    	reader.setErrorHandler(errorHandler);
	    	reader.setContentHandler(pageHandler);
	    	inputStream = getInputStream(input);
	    	if (inputStream == null)
	    		return;
	    	org.xml.sax.InputSource saxInput = new org.xml.sax.InputSource(inputStream);
	    	//saxInput.setEncoding("utf-8");
	    	reader.parse(saxInput);
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    } finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * SAX handler implementation to parse the schema version only.
	 * 
	 * @author Christian Clausner
	 */
	private static class SchemaVersionHandler extends DefaultHandler {
		private XmlFormatVersion schemaVersion = null;
		
		public XmlFormatVersion getSchemaVersion() {
			return schemaVersion;
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
			
		    if (DefaultXmlNames.ELEMENT_PcGts.equals(localName)){
		    	
				String str = namespaceURI; //Example: http://schema.primaresearch.org/PAGE/gts/pagecontent/2010-03-19
				int pos = str.lastIndexOf("/");
				schemaVersion = new XmlFormatVersion(str.substring(pos+1));
					throw new SAXException(PARSING_COMPLETE);
		    }
		    //Abbyy
		    else if ("document".equals(localName)) {
				//String str = namespaceURI; //Example: http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml
				if (namespaceURI.contains("abbyy")) {
					schemaVersion = new XmlFormatVersion(namespaceURI);
					throw new SAXException(PARSING_COMPLETE);
				}
		    }
		    //ALTO
		    else if ("alto".equals(localName)) {
				//String str = namespaceURI; //Examples: http://www.loc.gov/standards/alto/ns-v2#
		    	//                                       http://www.loc.gov/standards/alto/ns-v3#
				if (namespaceURI.toLowerCase().contains("alto")) {
					schemaVersion = new XmlFormatVersion(namespaceURI);
					throw new SAXException(PARSING_COMPLETE);
				}
		    }
		    //HOCR
		    else if ("html".equals(localName)) {
				schemaVersion = new XmlFormatVersion("HOCR");
				throw new SAXException(PARSING_COMPLETE);
		    }
		}
	}
	
	

	
	

}
