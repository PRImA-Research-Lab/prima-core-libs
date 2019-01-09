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
package org.primaresearch.dla.page.layout.physical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LogicalType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.dla.page.layout.physical.text.TextContent;
import org.primaresearch.io.FormatModel;
import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Attribute factory for the default layout content types of PAGE (static/dynamic).<br>
 * <br>
 * In static mode types and attributes are hard coded. In dynamic use only the types are hard coded, the attributes are generated dynamically from a schema.
 * 
 * @author Christian Clausner
 *
 */
public class DefaultAttributeFactory implements
		AttributeFactory {
	
	private static Map<ContentType, List<Variable>> contentTypeAttrMap = null;
	private static List<Variable> textStyleAttrs = null;
	private static List<Variable> textEquivAttrs = null;
	
	private FormatModel schemaParser = null;

	/**
	 * Constructor for static use
	 */
	public DefaultAttributeFactory() {
	}
	
	/**
	 * Constructor for dynamic use.
	 * @param schemaParser Parser for XML schema
	 */
	public DefaultAttributeFactory(FormatModel schemaParser) {
		this.schemaParser = schemaParser;
	}
	
	/**
	 * Creates map with hard-coded types and attributes.
	 */
	private static Map<ContentType, List<Variable>> getMap() {
		if (contentTypeAttrMap == null) {
			contentTypeAttrMap = new HashMap<ContentType, List<Variable>>();
			
			List<Variable> vars;
			
			//Page
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(ContentType.Page, vars);
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("type", null));
			vars.add(new StringVariable("readingDirection", null));
			vars.add(new StringVariable("textLineOrder", null));
			vars.add(new StringVariable("primaryLanguage", null));
			vars.add(new StringVariable("secondaryLanguage", null));
			vars.add(new StringVariable("primaryScript", null));
			vars.add(new StringVariable("secondaryScript", null));
			
			//Text Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.TextRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("type", new StringValue("paragraph")));
			vars.add(new IntegerVariable("leading", null));
			vars.add(new StringVariable("readingDirection", null));
			vars.add(new DoubleVariable("readingOrientation", null));
			vars.add(new BooleanVariable("indented", null));
			vars.add(new StringVariable("primaryLanguage", null));
			vars.add(new StringVariable("secondaryLanguage", null));
			vars.add(new StringVariable("primaryScript", null));
			vars.add(new StringVariable("secondaryScript", null));
			vars.add(new StringVariable("production", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Image Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.ImageRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("colourDepth", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new BooleanVariable("embText", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Line Drawing Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.LineDrawingRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("penColour", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new BooleanVariable("embText", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Graphic Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.GraphicRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("type", null));
			vars.add(new IntegerVariable("numColours", null));
			vars.add(new BooleanVariable("embText", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Table Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.TableRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new IntegerVariable("rows", null));
			vars.add(new IntegerVariable("columns", null));
			vars.add(new StringVariable("lineColour", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new BooleanVariable("lineSeparators", null));
			vars.add(new BooleanVariable("embText", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Chart Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.ChartRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("type", null));
			vars.add(new IntegerVariable("numColours", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new BooleanVariable("embText", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Separator Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.SeparatorRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("colour", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Maths Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.MathsRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Advert Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.AdvertRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Chem Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.ChemRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Music Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.MusicRegion, vars);
			vars.add(new DoubleVariable("orientation", null));
			vars.add(new StringVariable("bgColour", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Noise Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.NoiseRegion, vars);
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Frame Region
			//vars = new ArrayList<Variable>();
			//contentTypeAttrMap.put(RegionType.FrameRegion, vars);
			//vars.add(new StringVariable("bgColour", null));
			//vars.add(new BooleanVariable("borderPresent", null));
		
			//Unknown Region
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RegionType.UnknownRegion, vars);
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Text line
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LowLevelTextType.TextLine, vars);
			vars.add(new StringVariable("primaryScript", null));
			vars.add(new StringVariable("secondaryScript", null));
			vars.add(new StringVariable("primaryLanguage", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Word
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LowLevelTextType.Word, vars);
			vars.add(new StringVariable("language", null));
			vars.add(new StringVariable("primaryScript", null));
			vars.add(new StringVariable("secondaryScript", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Glyph
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LowLevelTextType.Glyph, vars);
			vars.add(new BooleanVariable("ligature", null));
			vars.add(new BooleanVariable("symbol", null));
			vars.add(new StringVariable("script", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Grapheme
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LowLevelTextType.Grapheme, vars);
			vars.add(new BooleanVariable("ligature", null));
			vars.add(new BooleanVariable("charType", null));
			vars.add(new StringVariable("index", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Grapheme Group
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LowLevelTextType.GraphemeGroup, vars);
			vars.add(new BooleanVariable("ligature", null));
			vars.add(new BooleanVariable("charType", null));
			vars.add(new StringVariable("index", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Non-printing character
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LowLevelTextType.NonPrintingCharacter, vars);
			vars.add(new BooleanVariable("ligature", null));
			vars.add(new BooleanVariable("charType", null));
			vars.add(new StringVariable("index", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));
			
			//Reading order group
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(LogicalType.ReadingOrderGroup, vars);
			vars.add(new StringVariable("caption", null));
			vars.add(new StringVariable("type", null));
			vars.add(new StringVariable("continuation", null));
			vars.add(new StringVariable("custom", null));
			vars.add(new StringVariable("comments", null));

			//Table cell role
			vars = new ArrayList<Variable>();
			contentTypeAttrMap.put(RoleType.TableCellRole, vars);
			vars.add(new IntegerVariable("rowIndex", null));
			vars.add(new IntegerVariable("columnIndex", null));
			vars.add(new IntegerVariable("rowSpan", null));
			vars.add(new IntegerVariable("colSpan", null));
		}
		return contentTypeAttrMap;
	}
	
	private List<Variable> getTextStyleList() {
		if (textStyleAttrs == null) {
			textStyleAttrs = new ArrayList<Variable>();
			textStyleAttrs.add(new StringVariable("textColour", null));
			textStyleAttrs.add(new StringVariable("bgColour", null));
			textStyleAttrs.add(new StringVariable("textColourRgb", null));
			textStyleAttrs.add(new StringVariable("bgColourRgb", null));
			textStyleAttrs.add(new BooleanVariable("reverseVideo", null));
			textStyleAttrs.add(new DoubleVariable("fontSize", null));
			textStyleAttrs.add(new DoubleVariable("xHeight", null));
			textStyleAttrs.add(new IntegerVariable("kerning", null));
			textStyleAttrs.add(new StringVariable("fontFamily", null));
			textStyleAttrs.add(new BooleanVariable("serif", null));
			textStyleAttrs.add(new BooleanVariable("monospace", null));
			textStyleAttrs.add(new BooleanVariable("bold", null));
			textStyleAttrs.add(new BooleanVariable("italic", null));
			textStyleAttrs.add(new BooleanVariable("underlined", null));
			textStyleAttrs.add(new BooleanVariable("subscript", null));
			textStyleAttrs.add(new BooleanVariable("superscript", null));
			textStyleAttrs.add(new BooleanVariable("strikethrough", null));
			textStyleAttrs.add(new BooleanVariable("smallCaps", null));
			textStyleAttrs.add(new BooleanVariable("letterSpaced", null));
		}
		return textStyleAttrs;
	}
	
	private List<Variable> getTextEquivList() {
		if (textEquivAttrs == null) {
			textEquivAttrs = new ArrayList<Variable>();
			textEquivAttrs.add(new IntegerVariable(DefaultXmlNames.ATTR_index, null));
			textEquivAttrs.add(new StringVariable(DefaultXmlNames.ATTR_comments, null));
			textEquivAttrs.add(new DoubleVariable(DefaultXmlNames.ATTR_conf, null));
			textEquivAttrs.add(new StringVariable(DefaultXmlNames.ATTR_dataType, null));
			textEquivAttrs.add(new StringVariable(DefaultXmlNames.ATTR_dataTypeDetails, null));
		}
		return textEquivAttrs;
	}

	@Override
	public VariableMap createAttributes(ContentType type) {
		
		//Dynamic schema
		VariableMap varMap = new VariableMap();
		if (schemaParser != null) {
			Map<String, VariableMap> typeAttrTemplates = schemaParser.getTypeAttributeTemplates();
			if (typeAttrTemplates != null) {
				String schemaTypeName = getSchemaTypeName(type);
				varMap.setType(schemaTypeName);
				if (schemaTypeName != null) {
					VariableMap template = typeAttrTemplates.get(schemaTypeName);
					if (template != null) {
						for (int i=0; i<template.getSize(); i++)
							varMap.add(template.get(i).clone());
					}
				}
			}
		} 
		//Hard coded schema
		else {
			Map<ContentType, List<Variable>> map = getMap(); 
			List<Variable> vars = map.get(type);
			if (vars != null) {
				for (int i=0; i<vars.size(); i++)
					varMap.add(vars.get(i).clone());
			}
		}
		
		//Text style 
		if (LowLevelTextType.TextLine.equals(type) || LowLevelTextType.Word.equals(type) || LowLevelTextType.Glyph.equals(type) 
				|| RegionType.TextRegion.equals(type)) {
			createTextStyleAttributes(varMap);
		}
		return varMap;
	}
	
	@Override
	public VariableMap createAttributes(TextContent textContent) {
		VariableMap varMap = new VariableMap();
		createTextEquivAttributes(varMap);
		return varMap;
	}
	
	private void createTextStyleAttributes(VariableMap varMap) {
		//Dynamic 
		if (schemaParser != null) {
			Map<String, VariableMap> typeAttrTemplates = schemaParser.getTypeAttributeTemplates();
			if (typeAttrTemplates != null) {
				String schemaTypeName = "TextStyleType";
				//varMap.setType(schemaTypeName);
				VariableMap template = typeAttrTemplates.get(schemaTypeName);
				if (template != null) {
					for (int i=0; i<template.getSize(); i++)
						varMap.add(template.get(i).clone());
				}
			}
		} 
		//Hard coded
		else {
			List<Variable> list = getTextStyleList(); 
			if (list != null) {
				for (int i=0; i<list.size(); i++)
					varMap.add(list.get(i).clone());
			}
		}
	}
	
	private void createTextEquivAttributes(VariableMap varMap) {
		//Dynamic 
		if (schemaParser != null) {
			Map<String, VariableMap> typeAttrTemplates = schemaParser.getTypeAttributeTemplates();
			if (typeAttrTemplates != null) {
				String schemaTypeName = "TextEquivType";
				//varMap.setType(schemaTypeName);
				VariableMap template = typeAttrTemplates.get(schemaTypeName);
				if (template != null) {
					for (int i=0; i<template.getSize(); i++)
						varMap.add(template.get(i).clone());
				}
			}
		} 
		//Hard coded
		else {
			List<Variable> list = getTextEquivList(); 
			if (list != null) {
				for (int i=0; i<list.size(); i++)
					varMap.add(list.get(i).clone());
			}
		}
	}
	
	/**
	 * Returns the name of the complex type in the XML schema that corresponds to the given page content type.
	 * @return The type name or null if the type is not known. 
	 */
	private String getSchemaTypeName(ContentType type) {
		if (type == RegionType.ChartRegion)
			return "ChartRegionType";
		//else if (type == RegionType.FrameRegion)
		//	return "FrameRegionType";
		else if (RegionType.GraphicRegion.equals(type))
			return "GraphicRegionType";
		else if (RegionType.ImageRegion.equals(type))
			return "ImageRegionType";
		else if (RegionType.LineDrawingRegion.equals(type))
			return "LineDrawingRegionType";
		else if (RegionType.MathsRegion.equals(type))
			return "MathsRegionType";
		else if (RegionType.AdvertRegion.equals(type))
			return "AdvertRegionType";
		else if (RegionType.ChemRegion.equals(type))
			return "ChemRegionType";
		else if (RegionType.MusicRegion.equals(type))
			return "MusicRegionType";
		else if (RegionType.NoiseRegion.equals(type))
			return "NoiseRegionType";
		else if (RegionType.SeparatorRegion.equals(type))
			return "SeparatorRegionType";
		else if (RegionType.TableRegion.equals(type))
			return "TableeRegionType";
		else if (RegionType.TextRegion.equals(type))
			return "TextRegionType";
		else if (RegionType.UnknownRegion.equals(type))
			return "UnknownRegionType";
		else if (LowLevelTextType.TextLine.equals(type))
			return "TextLineType";
		else if (LowLevelTextType.Word.equals(type))
			return "WordType";
		else if (LowLevelTextType.Glyph.equals(type))
			return "GlyphType";
		else if (LowLevelTextType.Grapheme.equals(type))
			return "GraphemeType";
		else if (LowLevelTextType.GraphemeGroup.equals(type))
			return "GraphemeGroupType";
		else if (LowLevelTextType.NonPrintingCharacter.equals(type))
			return "NonPrintingCharType";
		else if (ContentType.Page.equals(type))
			return "PageType";
		else if (LogicalType.ReadingOrderGroup.equals(type))
			return "OrderedGroupType";
		else if (RoleType.TableCellRole.equals(type))
			return "TableCellRoleType";
		return null;
	}

}
