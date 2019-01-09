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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primaresearch.io.UnsupportedFormatVersionException;

/**
 * Provides access to models and validators for different schema versions.
 *  
 * @author Christian Clausner
 *
 */
public abstract class XmlModelAndValidatorProvider {

	/** Map [schemaVersion, validator] */
	private Map<XmlFormatVersion, XmlValidator> validators = new HashMap<XmlFormatVersion, XmlValidator>();
	
	/** Map [schemaVersion, schemaFilePath] */
	private Map<XmlFormatVersion, URL> schemaSources = new HashMap<XmlFormatVersion, URL>();

	/** Map [schemaVersion, schemaFilePath] */
	private List<XmlFormatVersion> defaultSchemas = new ArrayList<XmlFormatVersion>();
	
	/** Map [schemaVersion, schemaParser] */
	private Map<XmlFormatVersion, SchemaModelParser> schemaParsers = new HashMap<XmlFormatVersion, SchemaModelParser>();
	
	
	private XmlFormatVersion latestSchemaVersion;

	/**
	 * Constructor for default schemas only.
	 * 
	 * @throws NoSchemasException Schema resources not found
	 */
	public XmlModelAndValidatorProvider() throws NoSchemasException {
		addDefaultSchemas();
		if (schemaSources.isEmpty())
			throw new NoSchemasException("No default schemas found.");
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
	public XmlModelAndValidatorProvider(String schemaRootFolder, String schemaFilename) throws NoSchemasException {
		addDefaultSchemas();
		searchForAdditionalSchemas(schemaRootFolder, schemaFilename, 3);
		if (schemaSources.isEmpty())
			throw new NoSchemasException("No default schemas or schemas with filename " + schemaFilename + " found in " + schemaRootFolder);
	}
	
	/**
	 * Adds the internal default schemas to the list of schema sources.
	 */
	protected abstract void addDefaultSchemas();

	/**
	 * Adds a schema source file
	 * @param version Schema version
	 * @param url Schema location
	 */
	protected void addSchemaSource(XmlFormatVersion version, URL url) {
		addSchemaSource(version, url, false);
	}

	/**
	 * Adds a schema source file
	 * @param version Schema version
	 * @param url Schema location
	 * @param addToDefaultList Set to <code>true</code> to add the schema source to the list of default sources 
	 */
	protected void addSchemaSource(XmlFormatVersion version, URL url, boolean addToDefaultList) {
		schemaSources.put(version, url);
		//Keep a separate list as well
		if (addToDefaultList && !defaultSchemas.contains(version))
			defaultSchemas.add(version);
	}
	
	/**
	 * Returns true if the given schema version is part of the list of default schemas.
	 */
	public boolean isDefaultSchema(XmlFormatVersion schemaVersion) {
		if (defaultSchemas == null)
			return false;
		for (int i=0; i<defaultSchemas.size(); i++)
			if (defaultSchemas.get(i).equals(schemaVersion))
				return true;
		return false;
	}
	
	
	/**
	 * Searches the given folder for schema files having the specified name and
	 * adds them to the internal schema version map.
	 * Note: The name of the folder containing a schema file is assumed to be the schema version.
	 * 
	 * @param schemaRootFolder The folder where to start the search 
	 * @param schemaFilename Usually a file name with extension .xsd 
	 * @param maxSearchDepth Specifies how deep to go into the folder hierarchy
	 */
	private void searchForAdditionalSchemas(String folderPath, String schemaFilename, int maxSearchDepth) {
		File folder = new File(folderPath);
		if(folder.isDirectory()) { // check to make sure it is a directory
			if (!folderPath.endsWith("/"))
				folderPath += "/";
			String filenames[] = folder.list(); //make array of filenames.
			if (filenames != null) {
				for (int i=0; i<filenames.length; i++) {
					if (filenames[i].endsWith("."))
						continue;
					File curr = new File(folderPath+filenames[i]);
					if (curr.exists()) {
						if (curr.isDirectory() && maxSearchDepth > 0)
							searchForAdditionalSchemas(curr.getPath(), schemaFilename, maxSearchDepth-1); //recursion
						else if (curr.getName().equals(schemaFilename)) {
							try {
								schemaSources.put(new XmlFormatVersion(folder.getName()), new File(curr.getPath()).toURI().toURL());
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns the latest schema version of this provider (based on version format 'yyyy-mm-dd').
	 */
	public XmlFormatVersion getLatestSchemaVersion() {
		if (latestSchemaVersion == null)
			latestSchemaVersion = findLatestSchemaVersion();
		return latestSchemaVersion;
	}

	/**
	 * Finds the latest schema version from the list of schema versions (based on version format 'yyyy-mm-dd').
	 * @return New format version object
	 */
	private XmlFormatVersion findLatestSchemaVersion() {
	
		List<String> versions = new ArrayList<String>();
		
		//Put all versions into a list
		for (Iterator<XmlFormatVersion> it = defaultSchemas.iterator(); it.hasNext(); ) {
			versions.add(it.next().toString()); 
		}
		
		//Sort
		Collections.sort(versions);
		
		return new XmlFormatVersion(versions.get(versions.size()-1));
	}
	
	/**
	 * Returns the validator for the latest default schema version. Note that 
	 * additional schemas are not taken into account. 
	 */
	public XmlValidator getValidatorForLatestVersion() {
		try {
			return getValidator(getLatestSchemaVersion());
		} catch (UnsupportedSchemaVersionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the validator for the specified schema version.
	 */
	public XmlValidator getValidator(XmlFormatVersion schemaVersion) throws UnsupportedSchemaVersionException {
		if (schemaVersion == null)
			return null;
		if (!schemaSources.containsKey(schemaVersion))
			throw new UnsupportedSchemaVersionException(schemaVersion.toString());
		
		XmlValidator validator = validators.get(schemaVersion);
		if (validator == null) { //Not yet created
			if (schemaSources.get(schemaVersion) != null) {
				//Create validator
				validator = new XmlValidator(schemaSources.get(schemaVersion), schemaVersion);
				validators.put(schemaVersion, validator);
			}
		}
		return validator;
	}
	
	/**
	 * Returns a schema parser for the given version
	 * @param schemaVersion Schema version of the requested parser
	 * @return The parser
	 * @throws UnsupportedSchemaVersionException
	 */
	public SchemaModelParser getSchemaParser(XmlFormatVersion schemaVersion) throws UnsupportedSchemaVersionException {
		if (schemaVersion == null)
			return null;
		if (!schemaSources.containsKey(schemaVersion))
			throw new UnsupportedSchemaVersionException(schemaVersion.toString());
		
		SchemaModelParser schemaParser = schemaParsers.get(schemaVersion);
		if (schemaParser == null) { //Not yet created
			if (schemaSources.get(schemaVersion) != null) {
				//Create schema parser
				//TODO Different parsers?
				schemaParser = new DefaultSchemaParser(schemaVersion);
				schemaParser.parse(schemaSources.get(schemaVersion));
				schemaParsers.put(schemaVersion, schemaParser);
			}
		}
		return schemaParser;
	}

	
	
	@SuppressWarnings("serial")
	public static class UnsupportedSchemaVersionException extends UnsupportedFormatVersionException {
		public UnsupportedSchemaVersionException(String msg) {
			super(msg);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NoSchemasException extends Exception {
		public NoSchemasException(String msg) {
			super(msg);
		}
	}
}
