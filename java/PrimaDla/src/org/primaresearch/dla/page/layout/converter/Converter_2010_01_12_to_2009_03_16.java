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
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.xml.XmlFormatVersion;

/**
 * Converter for 2010-01-12 format to 2009-03-16 format.<br>
 * <br>
 * <ul>
 * <li>Adds temp region if there is no region (at least one region required)</li>
 * </ul>
 * @author Christian Clausner
 *
 */
public class Converter_2010_01_12_to_2009_03_16 implements LayoutConverter {

	@Override
	public FormatVersion getSourceVersion() {
		return new XmlFormatVersion("2010-01-12");
	}

	@Override
	public FormatVersion getTargetVersion() {
		return new XmlFormatVersion("2009-03-16");
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
		
		//Add a temporary region if there is no region at all
		if (layout.getRegionCount() == 0) {
			Region reg = layout.createRegion(RegionType.TextRegion);
			try {
				if (!checkOnly)
					reg.setId("r"+ContentObject.TEMP_ID_SUFFIX);
				messages.add(new ConversionMessage("Added temporary text region", ConversionMessage.CONVERSION_ADD_REQUIRED_REGION));
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
		}
		
		return messages;
	}

}
