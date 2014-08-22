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

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.io.FormatVersion;

/**
 * Meta converter representing a chain of converters.
 * 
 * @author Christian Clausner
 *
 */
public class ChainConverter implements LayoutConverter {

	private List<LayoutConverter> converters = new ArrayList<LayoutConverter>();
	
	/**
	 * Adds a converter to the chain
	 * @param converter Converter object
	 */
	public void addConverter(LayoutConverter converter) {
		if (!converters.isEmpty() && !converters.get(converters.size()-1).getTargetVersion().equals(converter.getSourceVersion()))
			throw new IllegalArgumentException("Source format version of given converter doesn't match the target format version of the last converter in the chain.");
		converters.add(converter);
	}
	
	@Override
	public FormatVersion getSourceVersion() {
		return converters.get(0).getSourceVersion();
	}

	@Override
	public FormatVersion getTargetVersion() {
		return converters.get(converters.size()-1).getTargetVersion();
	}

	@Override
	public List<ConversionMessage> convert(PageLayout layout) {
		List<ConversionMessage> messages = new ArrayList<ConversionMessage>();
		
		for (int i=0; i<converters.size(); i++) {
			List<ConversionMessage> localMsg = converters.get(i).convert(layout);
			if (localMsg != null)
				messages.addAll(localMsg);
		}
			
		return messages;
	}

	@Override
	public List<ConversionMessage> checkForCompliance(PageLayout layout) {
		List<ConversionMessage> messages = new ArrayList<ConversionMessage>();
		
		for (int i=0; i<converters.size(); i++) {
			List<ConversionMessage> localMsg = converters.get(i).checkForCompliance(layout);
			if (localMsg != null)
				messages.addAll(localMsg);
		}
			
		return messages;
	}

}
