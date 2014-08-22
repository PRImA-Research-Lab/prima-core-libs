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
package org.primaresearch.dla.page.layout.converter;

import java.util.List;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.io.FormatVersion;

/**
 * Interface for converters that convert a page layout to comply with a certain format version.
 * 
 * @author Christian Clausner
 *
 */
public interface LayoutConverter {

	/**
	 * Format version before the conversion
	 */
	public FormatVersion getSourceVersion();
	
	/**
	 * Format version after the conversion
	 */
	public FormatVersion getTargetVersion();

	/**
	 * Converts the given page layout to the specified target format.
	 * @return A list of conversion messages
	 */
	public List<ConversionMessage> convert(PageLayout layout);
	
	/**
	 * Checks if the given page layout is consistent to the target format version
	 * of this converter.
	 * @return A list of inconsistencies
	 */
	public List<ConversionMessage> checkForCompliance(PageLayout layout); 
}
