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

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.io.xml.IOError;
import org.primaresearch.io.xml.XmlValidationError;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Error handler implementation that collects errors and warnings.
 * 
 * @author Christian Clausner
 *
 */
public class PageErrorHandler implements ErrorHandler {
	
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
	
	/**
	 * Checks if there were errors
	 * @return <code>true</code> if errors were registered
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * Checks if there were warnings
	 * @return <code>true</code> if warnings were registered
	 */
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	
	/**
	 * Returns all registered errors
	 * @return List of error objects
	 */
	public List<IOError> getErrors() {
		return errors;
	}

	/**
	 * Returns all registered warnings
	 * @return List of warning objects
	 */
	public List<IOError> getWarnings() {
		return warnings;
	}
	
}