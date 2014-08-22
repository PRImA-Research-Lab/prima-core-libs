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

import java.util.List;

import org.primaresearch.dla.page.io.PageWriter;
import org.primaresearch.dla.page.layout.converter.ConversionMessage;

/**
 * Interface for page writers producing XML.
 * 
 * @author Christian Clausner
 */
public interface XmlPageWriter extends PageWriter {

	/**
	 * Returns the XML schema version the writer supports (in format yyyy-mm-dd). 
	 */
	public String getSchemaVersion();
	
	/**
	 * Returns the location of the schema (e.g. http://schema.primaresearch.org/PAGE/gts/pagecontent/2010-03-19).
	 */
	public String getSchemaLocation();
	
	/**
	 * Returns the URL of the schema file (e.g. http://schema.primaresearch.org/PAGE/gts/pagecontent/2010-03-19/pagecontent.xsd).
	 */
	public String getSchemaUrl();
	
	/**
	 * Returns the name space. This is usually the same as the schema location.
	 */
	public String getNamespace();
	
	/**
	 * Returns format conversion related messages
	 */
	public List<ConversionMessage> getConversionInformation();
}
