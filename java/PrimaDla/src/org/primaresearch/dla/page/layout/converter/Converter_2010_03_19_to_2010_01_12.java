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
package org.primaresearch.dla.page.layout.converter;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * Converter for 2010-03-19 format to 2010-01-12 format.<br>
 * <br>
 * <ul>
 * <li>Removes unsupported text types (signature-mark, catch-word, marginalia, footnote, footnote-continued, TOC-entry)</li>
 * </ul>
 * @author Christian Clausner
 *
 */
public class Converter_2010_03_19_to_2010_01_12 implements LayoutConverter {

	@Override
	public FormatVersion getSourceVersion() {
		return new XmlFormatVersion("2010-03-19");
	}

	@Override
	public FormatVersion getTargetVersion() {
		return new XmlFormatVersion("2010-01-12");
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
		
		//Remove text type values:
		// signature-mark
		// catch-word
		// marginalia
		// footnote
		// footnote-continued
		// TOC-entry
		for (int i=0; i<layout.getRegionCount(); i++) {
			Region reg = layout.getRegion(i);
			if (reg instanceof TextRegion) {
				Variable textType = reg.getAttributes().get("type");
				if (textType != null && textType.getValue() != null) {
					String val = textType.getValue().toString();
					if (	val.equals("signature-mark")
						|| 	val.equals("catch-word")
						|| 	val.equals("marginalia")
						|| 	val.equals("footnote")
						|| 	val.equals("footnote-continued")
						|| 	val.equals("TOC-entry"))
					{
						try {
							if (!checkOnly)
								textType.setValue(null);
							messages.add(new ConversionMessage("Reset unsupported text type '"+val+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
						} catch (WrongVariableTypeException e) {
						}
					}
				}
			}
		}
		
		return messages;
	}

}
