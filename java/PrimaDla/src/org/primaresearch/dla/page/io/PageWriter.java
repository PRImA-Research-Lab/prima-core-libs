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
package org.primaresearch.dla.page.io;

import org.primaresearch.dla.page.Page;
import org.primaresearch.io.UnsupportedFormatVersionException;

/**
 * Interface for writing PAGE.
 * 
 * @author Christian Clausner
 *
 */
public interface PageWriter {

	/**
	 * Writes the given Page object to an output target.
	 *  
	 * @return Returns true if written successfully, false otherwise.
	 */
	public boolean write(Page page, OutputTarget target) throws UnsupportedFormatVersionException;
	
	/**
	 * Validates the given page object against the format it is set to.
	 *  
	 * @return Returns true if valid, false otherwise.
	 */
	public boolean validate(Page page) throws UnsupportedFormatVersionException;
	
}
