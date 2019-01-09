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
package org.primaresearch.dla.page.layout.converter;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.Variable;

/**
 * Converter for 2013-07-15 format to 2016-07-15 format.<br>
 * <br>
 * <ul>
 * <li>Converts script values</li>
 * </ul>
 * @author Christian Clausner
 *
 */
public class Converter_2009_03_16_to_2016_07_15 implements LayoutConverter {

	@Override
	public FormatVersion getSourceVersion() {
		return new XmlFormatVersion("2009-03-16");
	}

	@Override
	public FormatVersion getTargetVersion() {
		return new XmlFormatVersion("2016-07-15");
	}

	@Override
	public List<ConversionMessage> convert(PageLayout layout) {
		return run(layout, false);
	}

	@Override
	public List<ConversionMessage> checkForCompliance(PageLayout layout) {
		return run(layout, true);
	}

	/**
	 * Runs check or conversion
	 * @param checkOnly If true, no conversion is carried out (dry run).
	 */
	public List<ConversionMessage> run(PageLayout layout, boolean checkOnly) {
		List<ConversionMessage> messages = new ArrayList<ConversionMessage>();
		
		//Regions
		for (ContentIterator it = layout.iterator(null); it.hasNext(); ) {
			Region reg = (Region)it.next();
			
			//Primary and secondary Script
			if (reg.getType().equals(RegionType.TextRegion)) { 

				Variable v = reg.getAttributes().get("primaryScript");
				if (v != null && v.getValue() != null)
					convertScript((StringValue)v.getValue(), checkOnly, messages);

				v = reg.getAttributes().get("secondaryScript");
				if (v != null && v.getValue() != null)
					convertScript((StringValue)v.getValue(), checkOnly, messages);
			}
		}
		
		return messages;
	}
	
	/**
	 * 
	 * @param script
	 * @param checkOnly
	 * @param messages
	 */
	private void convertScript(StringValue script, boolean checkOnly, List<ConversionMessage> messages) {
		
		String oldValue = script.val;
		String newValue = convertScript(oldValue);
		
		if (!newValue.equals(oldValue))	{
		
			if (!checkOnly)
				script.val = newValue;
		
			messages.add(new ConversionMessage("Changed content of script attribute from '"+oldValue+"' to '"+newValue+"'", ConversionMessage.CONVERSION_GENERAL));
		}
	}
	
	private String convertScript(String oldString) {
		if ("Arabic".equals(oldString)) return "Arab - Arabic";
		if ("Bengali".equals(oldString)) return "Beng - Bengali";
		if ("Cyrillic".equals(oldString)) return "Cyrl - Cyrillic";
		if ("Devangari".equals(oldString)) return "Deva - Devanagari (Nagari)";
		if ("Ethiopic".equals(oldString)) return "Ethi - Ethiopic";
		if ("Greek".equals(oldString)) return "Grek - Greek";
		if ("Gujarati".equals(oldString)) return "Gujr - Gujarati";
		if ("Gurmukhi".equals(oldString)) return "Guru - Gurmukhi";
		if ("Chinese-simplified".equals(oldString)) return "Hans - Han (Simplified variant)";
		if ("Chinese-traditional".equals(oldString)) return "Hant - Han (Traditional variant)";
		if ("Hebrew".equals(oldString)) return "Hebr - Hebrew";
		if ("Latin".equals(oldString)) return "Latn - Latin";
		if ("Thai".equals(oldString)) return "Thai - Thai";

		return "other";
	}

}
