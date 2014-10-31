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
package org.primaresearch.dla.page.io.xml;

import java.net.URL;

import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider;

/**
 * Provides access to models and validators for different PAGE XML schema versions.
 *  
 * @author Christian Clausner
 *
 */
public class PageXmlModelAndValidatorProvider extends XmlModelAndValidatorProvider{


	/**
	 * Constructor for default schemas only
	 * @throws NoSchemasException Schema resources not found
	 */
	public PageXmlModelAndValidatorProvider() throws NoSchemasException {
		super();
	}

	/**
	 * Constructor for additional schema retrieval from a folder structure.<br>
	 * 
	 * Example:<br>
	 * &nbsp;&nbsp;-schema<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;-2010-01-12<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-pagecontent.xsd<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;-2010-03-19<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-pagecontent.xsd<br>
	 *      
	 * @param schemaRootFolder
	 * @param schemaFilename
	 * @throws NoSchemasException No schema files found at the given location.
	 */
	public PageXmlModelAndValidatorProvider(String schemaRootFolder, String schemaFilename) throws NoSchemasException {
		super(schemaRootFolder, schemaFilename);
	}

	/**
	 * Adds the internal default schemas to the list of schema sources.
	 */
	protected void addDefaultSchemas() {
		try {
			//2009-03-16
			addSchemaSource(new XmlFormatVersion("2009-03-16"), 
					getClass().getResource("/org/primaresearch/dla/page/io/xml/schema/2009-03-16_pagecontent.xsd"),
					true);
			
			//2010-01-12
			addSchemaSource(	new XmlFormatVersion("2010-01-12"), 
					getClass().getResource("/org/primaresearch/dla/page/io/xml/schema/2010-01-12_pagecontent.xsd"),
					true);
			
			//2010-03-19
			addSchemaSource(	new XmlFormatVersion("2010-03-19"), 
					getClass().getResource("/org/primaresearch/dla/page/io/xml/schema/2010-03-19_pagecontent.xsd"),
					true);

			//2013-07-15
			addSchemaSource(	new XmlFormatVersion("2013-07-15"), 
					getClass().getResource("/org/primaresearch/dla/page/io/xml/schema/2013-07-15_pagecontent.xsd"),
					true);

			//Abbyy FineReader 10
			addSchemaSource(	new XmlFormatVersion("http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml"), 
								new URL("http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml"),
								false);

			//ALTO 1.1 //Didn't work (Cannot resolve the name 'xlink:simpleLink' )
			addSchemaSource(	new XmlFormatVersion("http://schema.ccs-gmbh.com/ALTO"), 
								new URL("http://www.loc.gov/ndnp/alto_1-1-041.xsd"),
								false);

			//ALTO 2.1
			addSchemaSource(	new XmlFormatVersion("http://www.loc.gov/standards/alto/ns-v2#"), 
								new URL("http://www.loc.gov/standards/alto/alto.xsd"),
								false);

			//ALTO 3.0
			addSchemaSource(	new XmlFormatVersion("http://www.loc.gov/standards/alto/ns-v3#"), 
								new URL("http://www.loc.gov/standards/alto/v3/alto.xsd"),
								false);

			//HOCR
			addSchemaSource(	new XmlFormatVersion("HOCR"), 
								null,
								false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
