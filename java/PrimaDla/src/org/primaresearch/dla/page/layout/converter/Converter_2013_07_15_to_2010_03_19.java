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
import java.util.Iterator;
import java.util.List;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.impl.GraphicRegion;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Converter for 2013-07-15 format to 2010-03-19 format.<br>
 * <br>
 * <ul>
 * <li>Removes unsupported graphic types (frame, barcode, decoration)</li>
 * <li>Removes unsupported text types (endnote, other)</li>
 * <li>Removes unsupported colours (other)</li>
 * <li>Removes unsupported languages</li>
 * <li>Converts unsupported regions to 'Unknown' (Music, Chem, Advert)</li>
 * <li>Removes unsupported attributes from regions, lines, words, and glyphs</li>
 * </ul>
 * @author Christian Clausner
 *
 */
public class Converter_2013_07_15_to_2010_03_19 implements LayoutConverter {

	@Override
	public FormatVersion getSourceVersion() {
		return new XmlFormatVersion("2013-07-15");
	}

	@Override
	public FormatVersion getTargetVersion() {
		return new XmlFormatVersion("2010-03-19");
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
		List<Region> unsupportedRegions = new ArrayList<Region>();
		for (ContentIterator it = layout.iterator(null); it.hasNext(); ) {
			Region reg = (Region)it.next();
			
			//Graphic types frame, barcode, decoration
			if (reg.getType().equals(RegionType.GraphicRegion) 
					&& ("frame".equals(((GraphicRegion)reg).getGraphicType())
						|| "barcode".equals(((GraphicRegion)reg).getGraphicType())
						|| "decoration".equals(((GraphicRegion)reg).getGraphicType()))) {
				
				if (!checkOnly)
					((GraphicRegion)reg).setGraphicType(null);
				
				messages.add(new ConversionMessage("Reset unsupported graphic type for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
			}

			//Text region types endnote, other
			if (reg.getType().equals(RegionType.TextRegion) 
					&& ("endnote".equals(((TextRegion)reg).getTextType())
						|| "other".equals(((TextRegion)reg).getTextType())
						)) {
				
				if (!checkOnly)
					((TextRegion)reg).setTextType(null);
				
				messages.add(new ConversionMessage("Reset unsupported text type for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
			}
			
			//Colours
			try {
				Variable v = reg.getAttributes().get("penColour");
				if (v != null && v.getValue().equals(new StringValue("other"))) {
					if (!checkOnly)
						v.setValue(null);
					messages.add(new ConversionMessage("Reset unsupported colour for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
				}

				v = reg.getAttributes().get("bgColour");
				if (v != null && v.getValue() != null && v.getValue().equals(new StringValue("other"))) {
					if (!checkOnly)
						v.setValue(null);
					messages.add(new ConversionMessage("Reset unsupported colour for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
				}
				
				v = reg.getAttributes().get("lineColour");
				if (v != null && v.getValue() != null && v.getValue().equals(new StringValue("other"))) {
					if (!checkOnly)
						v.setValue(null);
					messages.add(new ConversionMessage("Reset unsupported colour for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
				}
				
				v = reg.getAttributes().get("colour");
				if (v != null && v.getValue() != null && v.getValue().equals(new StringValue("other"))) {
					if (!checkOnly)
						v.setValue(null);
					messages.add(new ConversionMessage("Reset unsupported colour for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
				}
				
				v = reg.getAttributes().get("textColour");
				if (v != null && v.getValue() != null && v.getValue().equals(new StringValue("other"))) {
					if (!checkOnly)
						v.setValue(null);
					messages.add(new ConversionMessage("Reset unsupported colour for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Colour depth
			try {
				Variable v = reg.getAttributes().get("colourDepth");
				if (v != null && v.getValue() != null && v.getValue().equals(new StringValue("other"))) {
					if (!checkOnly)
						v.setValue(null);
					messages.add(new ConversionMessage("Reset unsupported colour depth for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Language
			try {
				Variable v = reg.getAttributes().get("primaryLanguage");
				if (v != null && v.getValue() != null) {
					if (!v.getValue().equals(new StringValue("other"))
						&& !v.getValue().equals(new StringValue("other"))
			    		&& !v.getValue().equals(new StringValue("Afrikaans"))
			    		&& !v.getValue().equals(new StringValue("Albanian"))
			    		&& !v.getValue().equals(new StringValue("Amharic"))
			    		&& !v.getValue().equals(new StringValue("Arabic"))
			    		&& !v.getValue().equals(new StringValue("Basque"))
			    		&& !v.getValue().equals(new StringValue("Bengali"))
			    		&& !v.getValue().equals(new StringValue("Bulgarian"))
			    		&& !v.getValue().equals(new StringValue("Cambodian"))
			    		&& !v.getValue().equals(new StringValue("Cantonese"))
			    		&& !v.getValue().equals(new StringValue("Chinese"))
			    		&& !v.getValue().equals(new StringValue("Czech"))
			    		&& !v.getValue().equals(new StringValue("Danish"))
			    		&& !v.getValue().equals(new StringValue("Dutch"))
			    		&& !v.getValue().equals(new StringValue("English"))
			    		&& !v.getValue().equals(new StringValue("Estonian"))
			    		&& !v.getValue().equals(new StringValue("Finnish"))
			    		&& !v.getValue().equals(new StringValue("French"))
			    		&& !v.getValue().equals(new StringValue("German"))
			    		&& !v.getValue().equals(new StringValue("Greek"))
			    		&& !v.getValue().equals(new StringValue("Gujarati"))
			    		&& !v.getValue().equals(new StringValue("Hebrew"))
			    		&& !v.getValue().equals(new StringValue("Hindi"))
			    		&& !v.getValue().equals(new StringValue("Hungarian"))
			    		&& !v.getValue().equals(new StringValue("Icelandic"))
			    		&& !v.getValue().equals(new StringValue("Gaelic"))
			    		&& !v.getValue().equals(new StringValue("Italian"))
			    		&& !v.getValue().equals(new StringValue("Japanese"))
			    		&& !v.getValue().equals(new StringValue("Korean"))
						&& !v.getValue().equals(new StringValue("Latin"))
			    		&& !v.getValue().equals(new StringValue("Latvian"))
			    		&& !v.getValue().equals(new StringValue("Malay"))
			    		&& !v.getValue().equals(new StringValue("Norwegian"))
			    		&& !v.getValue().equals(new StringValue("Polish"))
			    		&& !v.getValue().equals(new StringValue("Portuguese"))
			    		&& !v.getValue().equals(new StringValue("Punjabi"))
			    		&& !v.getValue().equals(new StringValue("Russian"))
			    		&& !v.getValue().equals(new StringValue("Spanish"))
			    		&& !v.getValue().equals(new StringValue("Swedish"))
			    		&& !v.getValue().equals(new StringValue("Thai"))
			    		&& !v.getValue().equals(new StringValue("Turkish"))
			    		&& !v.getValue().equals(new StringValue("Urdu"))
			    		&& !v.getValue().equals(new StringValue("Welsh"))
			    		&& !v.getValue().equals(new StringValue("other"))
			    		) {
						
						if (!checkOnly)
							v.setValue(null);
						messages.add(new ConversionMessage("Reset unsupported language for region '"+reg.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			

			//Advert, Chem and Music
			if (reg.getType().equals(RegionType.AdvertRegion)
				|| reg.getType().equals(RegionType.ChemRegion)
				|| reg.getType().equals(RegionType.MusicRegion)) {
				unsupportedRegions.add(reg);
			}

		}

		//Handle unsupported regions
		for (Iterator<Region> it = unsupportedRegions.iterator(); it.hasNext(); ) {
			Region unsupported = it.next();
			
			if (!checkOnly) {
				layout.removeRegion(unsupported.getId(), true);
				Region unknownRegion = layout.createRegion(RegionType.UnknownRegion, unsupported.getId().toString());
				unknownRegion.setCoords(unsupported.getCoords());
			}
			messages.add(new ConversionMessage("Changed region type to 'unknown' for region '"+unsupported.getId()+"'", ConversionMessage.CONVERSION_RESET_INVALID_ATTRIBUTE));
		}

		//Unsupported attributes
		// Regions
		for (ContentIterator it = layout.iterator(null); it.hasNext(); ) {
			ContentObject obj = it.next();
			if (obj == null)
				continue;
			VariableMap atts = obj.getAttributes();
			if (atts != null) {
				atts.remove("custom");
				atts.remove("comments");
				atts.remove("production");
				atts.remove("fontFamily");
				atts.remove("bold");
				atts.remove("italic");
				atts.remove("underlined");
				atts.remove("subscript");
				atts.remove("superscript");
				atts.remove("strikethrough");
				atts.remove("smallCaps");
				atts.remove("letterSpaced");
			}
		}
		// Lines
		for (ContentIterator it = layout.iterator(LowLevelTextType.TextLine); it.hasNext(); ) {
			ContentObject obj = it.next();
			if (obj == null)
				continue;
			VariableMap atts = obj.getAttributes();
			if (atts != null) {
				atts.remove("custom");
				atts.remove("comments");
				atts.remove("primaryLanguage");
				atts.remove("production");
				atts.remove("fontFamily");
				atts.remove("bold");
				atts.remove("italic");
				atts.remove("underlined");
				atts.remove("subscript");
				atts.remove("superscript");
				atts.remove("strikethrough");
				atts.remove("smallCaps");
				atts.remove("letterSpaced");
				atts.remove("serif");
				atts.remove("monospace");
				atts.remove("fontSize");
				atts.remove("kerning");
				atts.remove("textColour");
				atts.remove("bgColour");
				atts.remove("reverseVideo");
			}
		}
		// Words
		for (ContentIterator it = layout.iterator(LowLevelTextType.Word); it.hasNext(); ) {
			ContentObject obj = it.next();
			if (obj == null)
				continue;
			VariableMap atts = obj.getAttributes();
			if (atts != null) {
				atts.remove("custom");
				atts.remove("comments");
				atts.remove("language");
				atts.remove("production");
				atts.remove("fontFamily");
				atts.remove("bold");
				atts.remove("italic");
				atts.remove("underlined");
				atts.remove("subscript");
				atts.remove("superscript");
				atts.remove("strikethrough");
				atts.remove("smallCaps");
				atts.remove("letterSpaced");
				atts.remove("serif");
				atts.remove("monospace");
				atts.remove("fontSize");
				atts.remove("kerning");
				atts.remove("textColour");
				atts.remove("bgColour");
				atts.remove("reverseVideo");
			}
		}
		// Glyphs
		for (ContentIterator it = layout.iterator(LowLevelTextType.Glyph); it.hasNext(); ) {
			ContentObject obj = it.next();
			if (obj == null)
				continue;
			VariableMap atts = obj.getAttributes();
			if (atts != null) {
				atts.remove("custom");
				atts.remove("comments");
				atts.remove("production");
				atts.remove("fontFamily");
				atts.remove("bold");
				atts.remove("italic");
				atts.remove("underlined");
				atts.remove("subscript");
				atts.remove("superscript");
				atts.remove("strikethrough");
				atts.remove("smallCaps");
				atts.remove("letterSpaced");
				atts.remove("serif");
				atts.remove("monospace");
				atts.remove("fontSize");
				atts.remove("kerning");
				atts.remove("textColour");
				atts.remove("bgColour");
				atts.remove("reverseVideo");
			}
		}
		
		
		return messages;
	}

}
