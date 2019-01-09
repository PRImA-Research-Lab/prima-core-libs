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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * XML validator for a specific schema.<br>
 * Validators are usually managed by a ValidatorProvider. 
 * 
 * @author Christian Clausner
 *
 */
public class XmlValidator {
	
	URL schemaSource;
	Schema schema = null;
	XmlFormatVersion schemaVersion;
	
	public XmlValidator(URL schemaSource, XmlFormatVersion schemaVersion) {
		this.schemaSource = schemaSource;
		this.schemaVersion = schemaVersion;
	}

	/**
	 * Returns the source file of the schema for this validator
	 * @return Schema location
	 */
	public URL getSchemaSource() {
		return schemaSource; 
	}
	
	/**
	 * Returns the schema object that can be used for validating XML (e.g. DOM or SAX). 
	 */
	public Schema getSchema() {
		if (schema == null) {
    		//SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			try {
				URL schemaSource = getSchemaSource();
				InputStream inputStream = schemaSource.openStream();
				Source src = new StreamSource(inputStream);
				schema = schemaFactory.newSchema(src);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return schema;
	}
	
	/**
	 * Returns the schema version of this validator
	 * @return Version object
	 */
	public XmlFormatVersion getSchemaVersion() {
		return schemaVersion;
	}

}
