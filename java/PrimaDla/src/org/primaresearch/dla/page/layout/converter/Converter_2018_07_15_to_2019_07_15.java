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
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.xml.XmlFormatVersion;

/**
 * Converter for 2018-07-15 format to 2019-07-15 format.<br>
 * <br>
 * <ul>
 * <li></li>
 * </ul>
 * @author Christian Clausner
 *
 */
public class Converter_2018_07_15_to_2019_07_15 implements LayoutConverter {

	@Override
	public FormatVersion getSourceVersion() {
		return new XmlFormatVersion("2018-07-15");
	}

	@Override
	public FormatVersion getTargetVersion() {
		return new XmlFormatVersion("2019-07-15");
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
		
		//TODO ?
		
		//Regions
		/*for (ContentIterator it = layout.iterator(null); it.hasNext(); ) {
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
		}*/
		
		return messages;
	}
	
	


}
