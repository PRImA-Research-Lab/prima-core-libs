/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
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
package org.primaresearch.dla.page;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.primaresearch.shared.variable.VariableMap;

/**
 * Class for document metadata such as creation date, comments, ... 
 * 
 * @author Christian Clausner
 *
 */
public class MetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	public static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //TODO cannot be used in GWT projects!
	
	private String creator = null;
	private long created = 0L;
	private long lastModified = 0L;
	private String comments = null;
	private String externalRef = null;

	private VariableMap userDefinedAttributes = null;

	/**
	 * User-defined attributes (text, int, decimal or boolean)
	 * @return Variable map or <code>null</code>
	 */
	public VariableMap getUserDefinedAttributes() {
		return userDefinedAttributes;
	}
	
	/**
	 *  User-defined attributes (text, int, decimal or boolean)
	 * @param attrs Variable map
	 */
	public void setUserDefinedAttributes(VariableMap attrs) {
		userDefinedAttributes = attrs;
	}

	/**
	 * Returns the creating person, institution, and/or tool
	 * @return Creator description
	 */
	public String getCreator() {
		return creator;
	}
	
	/**
	 * Sets the creating person, institution, and/or tool
	 * @param creator Creator description
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	/**
	 * Returns comments (generic) 
	 * @return Comments text
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets generic comments 
	 * @param comments Comments text
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * Returns the creation date/time
	 * @return Date and time formatted according to the <code>DATE_FORMAT</code> constant
	 */
	public String getFormattedCreationTime() {
		return DATE_FORMAT.format(new Date(created));
	}

	/**
	 * Returns the creation date/time
	 * @return Date object
	 */
	public Date getCreationTime() {
		return new Date(created);
	}

	/**
	 * Returns the modification date/time
	 * @return Date and time formatted according to the <code>DATE_FORMAT</code> constant
	 */
	public String getFormattedLastModificationTime() {
		return DATE_FORMAT.format(new Date(lastModified));
	}

	/**
	 * Returns the modification date/time
	 * @return Date object
	 */
	public Date getLastModificationTime() {
		return new Date(lastModified);
	}
	
	/**
	 * Sets the creation date/time
	 * @param d Date object
	 */
	public void setCreationTime(Date d) {
		created = d.getTime();
	}
	
	/**
	 * Sets the modification date/time
	 * @param d Date object
	 */
	public void setLastModifiedTime(Date d) {
		lastModified = d.getTime();
	}

	/**
	 * External reference of any kind
	 * @return Reference string (e.g. a file or a URL)
	 */
	public String getExternalRef() {
		return externalRef;
	}

	/**
	 * External reference of any kind
	 * @param externalRef Reference string (e.g. a file or a URL)
	 */
	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}
	
}
