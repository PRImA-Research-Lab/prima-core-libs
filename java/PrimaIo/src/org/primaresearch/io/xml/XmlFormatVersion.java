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

import org.primaresearch.io.FormatVersion;

/**
 * Implementation of FormatVersion intended for use with XML.
 * @author Christian Clausner
 *
 */
public class XmlFormatVersion implements FormatVersion {

	private String schemaVersion;
	
	
	public XmlFormatVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof XmlFormatVersion)
			return schemaVersion.equals(((XmlFormatVersion) other).schemaVersion);
		return false;
	}
	
	@Override
	public int hashCode() {
		return schemaVersion.hashCode();
	}
	
	@Override
	public String toString() {
		return schemaVersion;
	}

	@Override
	public boolean isNewerThan(FormatVersion otherVersion) {
		if (otherVersion == null || !(otherVersion instanceof XmlFormatVersion))
			return false;
		return this.schemaVersion.compareTo(((XmlFormatVersion)otherVersion).schemaVersion) > 0;
	}

	@Override
	public boolean isOlderThan(FormatVersion otherVersion) {
		if (otherVersion == null || !(otherVersion instanceof XmlFormatVersion))
			return false;
		return this.schemaVersion.compareTo(((XmlFormatVersion)otherVersion).schemaVersion) < 0;
	}

}
