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

import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;

/**
 * Class containing hard coded XML element and attribute names for the PAGE format.
 * 
 * @author Christian Clausner
 *
 */
public class DefaultXmlNames implements XmlNameProvider {
	public static final String ELEMENT_PcGts			= "PcGts";
	public static final String ELEMENT_Page				= "Page";
	public static final String ELEMENT_TextRegion		= "TextRegion";
	public static final String ELEMENT_ImageRegion		= "ImageRegion";
	public static final String ELEMENT_LineDrawingRegion	= "LineDrawingRegion";
	public static final String ELEMENT_GraphicRegion		= "GraphicRegion";
	public static final String ELEMENT_TableRegion		= "TableRegion";
	public static final String ELEMENT_ChartRegion		= "ChartRegion";
	public static final String ELEMENT_SeparatorRegion	= "SeparatorRegion";
	public static final String ELEMENT_MathsRegion		= "MathsRegion";
	public static final String ELEMENT_NoiseRegion		= "NoiseRegion";
	public static final String ELEMENT_FrameRegion		= "FrameRegion";
	public static final String ELEMENT_UnknownRegion	= "UnknownRegion";
	public static final String ELEMENT_AdvertRegion		= "AdvertRegion";
	public static final String ELEMENT_ChemRegion		= "ChemRegion";
	public static final String ELEMENT_MusicRegion		= "MusicRegion";

	public static final String ELEMENT_Border			= "Border";
	public static final String ELEMENT_ReadingOrder		= "ReadingOrder";
	public static final String ELEMENT_RegionRef		= "RegionRef";
	public static final String ELEMENT_UnorderedGroup	= "UnorderedGroup";
	public static final String ELEMENT_OrderedGroup				= "OrderedGroup";
	public static final String ELEMENT_RegionRefIndexed			= "RegionRefIndexed";
	public static final String ELEMENT_UnorderedGroupIndexed	= "UnorderedGroupIndexed";
	public static final String ELEMENT_OrderedGroupIndexed		= "OrderedGroupIndexed";
	public static final String ELEMENT_Layers			= "Layers";
	public static final String ELEMENT_Layer			= "Layer";
	public static final String ELEMENT_PrintSpace		= "PrintSpace";

	public static final String ELEMENT_Coords			= "Coords";
	public static final String ELEMENT_Point			= "Point";
	public static final String ELEMENT_TextEquiv		= "TextEquiv";
	public static final String ELEMENT_TextLine			= "TextLine";
	public static final String ELEMENT_Word				= "Word";
	public static final String ELEMENT_Glyph			= "Glyph";
	public static final String ELEMENT_PlainText		= "PlainText";
	public static final String ELEMENT_Unicode			= "Unicode";
	public static final String ELEMENT_Baseline			= "Baseline";

	public static final String ELEMENT_Metadata			= "Metadata";
	public static final String ELEMENT_Creator			= "Creator";
	public static final String ELEMENT_Created			= "Created";
	public static final String ELEMENT_LastChange		= "LastChange";
	public static final String ELEMENT_Comments			= "Comments";

	public static final String ELEMENT_AlternativeImage	= "AlternativeImage";
	public static final String ELEMENT_Relations		= "Relations";
	public static final String ELEMENT_Relation			= "Relation";
	public static final String ELEMENT_TextStyle		= "TextStyle";

	
	public static final String ATTR_pcGtsId				= "pcGtsId";
	public static final String ATTR_imageFilename		= "imageFilename";
	public static final String ATTR_imageWidth			= "imageWidth";
	public static final String ATTR_imageHeight			= "imageHeight";
	public static final String ATTR_id					= "id";
	public static final String ATTR_x					= "x";
	public static final String ATTR_y					= "y";
	public static final String ATTR_orientation			= "orientation";
	public static final String ATTR_readingOrientation	= "readingOrientation";
	public static final String ATTR_readingDirection	= "readingDirection";
	public static final String ATTR_leading				= "leading";
	public static final String ATTR_kerning				= "kerning";
	public static final String ATTR_fontSize			= "fontSize";
	public static final String ATTR_type				= "type";
	public static final String ATTR_textColour			= "textColour";
	public static final String ATTR_bgColour			= "bgColour";
	public static final String ATTR_reverseVideo		= "reverseVideo";
	public static final String ATTR_indented			= "indented";
	public static final String ATTR_primaryLanguage		= "primaryLanguage";
	public static final String ATTR_secondaryLanguage	= "secondaryLanguage";
	public static final String ATTR_language			= "language";
	public static final String ATTR_primaryScript		= "primaryScript";
	public static final String ATTR_secondaryScript	= "secondaryScript";
	public static final String ATTR_colourDepth		= "colourDepth";
	public static final String ATTR_embText			= "embText";
	public static final String ATTR_penColour		= "penColour";
	public static final String ATTR_numColours		= "numColours";
	public static final String ATTR_rows			= "rows";
	public static final String ATTR_columns			= "columns";
	public static final String ATTR_lineColour		= "lineColour";
	public static final String ATTR_lineSeparators	= "lineSeparators";
	public static final String ATTR_colour			= "colour";
	public static final String ATTR_borderPresent	= "borderPresent";
	public static final String ATTR_symbol			= "symbol";
	public static final String ATTR_ligature		= "ligature";
	public static final String ATTR_regionRef		= "regionRef";
	public static final String ATTR_index			= "index";
	public static final String ATTR_zIndex			= "zIndex";
	public static final String ATTR_points			= "points";
	public static final String ATTR_caption			= "caption";
	public static final String ATTR_conf			= "conf";
	public static final String ATTR_custom			= "custom";
	public static final String ATTR_comments		= "comments";
	public static final String ATTR_filename		= "filename";
	public static final String ATTR_bold			= "bold";
	public static final String ATTR_italic			= "italic";
	public static final String ATTR_underlined		= "underlined";
	public static final String ATTR_strikethrough	= "strikethrough";
	public static final String ATTR_subscript		= "subscript";
	public static final String ATTR_superscript		= "superscript";
	public static final String ATTR_smallCaps		= "smallCaps";
	public static final String ATTR_letterSpaced	= "letterSpaced";
	
	@Override
	public String getXmlName(ContentType type) {
		if (type == RegionType.ChartRegion)
			return ELEMENT_ChartRegion;
		if (type == RegionType.GraphicRegion)
			return ELEMENT_GraphicRegion;
		if (type == RegionType.ImageRegion)
			return ELEMENT_ImageRegion;
		if (type == RegionType.LineDrawingRegion)
			return ELEMENT_LineDrawingRegion;
		if (type == RegionType.MathsRegion)
			return ELEMENT_MathsRegion;
		if (type == RegionType.NoiseRegion)
			return ELEMENT_NoiseRegion;
		if (type == RegionType.SeparatorRegion)
			return ELEMENT_SeparatorRegion;
		if (type == RegionType.AdvertRegion)
			return ELEMENT_AdvertRegion;
		if (type == RegionType.ChemRegion)
			return ELEMENT_ChemRegion;
		if (type == RegionType.MusicRegion)
			return ELEMENT_MusicRegion;
		if (type == RegionType.TableRegion)
			return ELEMENT_TableRegion;
		if (type == RegionType.TextRegion)
			return ELEMENT_TextRegion;
		if (type == RegionType.UnknownRegion)
			return ELEMENT_UnknownRegion;
		if (type == LowLevelTextType.TextLine)
			return ELEMENT_TextLine;
		if (type == LowLevelTextType.Word)
			return ELEMENT_Word;
		if (type == LowLevelTextType.Glyph)
			return ELEMENT_Glyph;
		return type.getName();
	}
}
