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
package org.primaresearch.dla.page.io.xml.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.primaresearch.dla.page.MetaData;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.Page.AlternativeImage;
import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.GeometricObjectImpl;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.ContentObjectRelation;
import org.primaresearch.dla.page.layout.logical.ContentObjectRelation.RelationType;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.logical.Relations;
import org.primaresearch.dla.page.layout.physical.AttributeContainer;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.RegionContainer;
import org.primaresearch.dla.page.layout.physical.role.RegionRole;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.dla.page.layout.physical.text.TextContent;
import org.primaresearch.dla.page.layout.physical.text.TextContentVariants;
import org.primaresearch.dla.page.layout.physical.text.TextObject;
import org.primaresearch.dla.page.layout.physical.text.graphemes.GraphemeElement;
import org.primaresearch.dla.page.layout.physical.text.graphemes.GraphemeGroup;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.ident.Identifiable;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerValue;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * XML handler for 2016 PAGE format.
 * 
 * @author Christian Clausner
 *
 */
public class SaxPageHandler_2017_07_15 extends SaxPageHandler {

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	private Page page = null;
	private PageLayout layout = null;
	private MetaData metaData = null;
	private boolean handlingMetadata = false;

	private GeometricObject currentGeometricObject = null;
	private Region currentRegion = null;
	private Stack<Region> regionStack = new Stack<Region>(); //for nesting of regions
	private TextLine currentTextLine = null;
	private Word currentWord = null;
	private Glyph currentGlyph = null;
	private TextContentVariants currentTextObject = null;
	private TextContent currentTextContent = null;
	private String insideElement = null;
	private ReadingOrder readingOrder = null;
	private Group currentLogicalGroup;
	private StringBuffer currentText = null;
	private XmlModelAndValidatorProvider validatorProvider;
	private XmlFormatVersion schemaVersion;
	private List<List<String>> tempRelations;
	private List<String> currentRelation;		//[type, custom, comments, id1, id2]
	private Map<String, ContentObject> contentObjects = new HashMap<String, ContentObject>();
	private GraphemeGroup currentGraphemeGroup = null;
	private GraphemeElement currentGraphemeElement = null;
	private VariableMap currentUserDefinedAttributes = null;
	
	private int parsedTextEquivElements = 0;
	
	public SaxPageHandler_2017_07_15(XmlModelAndValidatorProvider validatorProvider, XmlFormatVersion schemaVersion) {
		this.validatorProvider = validatorProvider;
		this.schemaVersion = schemaVersion;
	}
	
	public Page getPageObject() {
		return page;
	}
	
	/**
	 * Receive notification of the start of an element.
	 * 
	 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
	 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
	 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
	 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	      throws SAXException {
    	int i;
		
		//Handle accumulated text
		finishText();
		
		insideElement = localName;
				
	    if (DefaultXmlNames.ELEMENT_PcGts.equals(localName)){
	    	createPageObject();
			//GtsID
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_pcGtsId)) >= 0) {
		    	try {
					page.setGtsId(atts.getValue(i));
				} catch (InvalidIdException e) {
					e.printStackTrace();
				}
			}
	    }
	    if (DefaultXmlNames.ELEMENT_Page.equals(localName)){
	    	handlePageElement(atts);
	    } 
	    else if (	DefaultXmlNames.ELEMENT_Border.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_PrintSpace.equals(localName)) {
	    	currentGeometricObject = new GeometricObjectImpl(new Polygon());
	    }
	    else if (DefaultXmlNames.ELEMENT_Metadata.equals(localName)) {
			//External ref
			if (metaData != null && (i = atts.getIndex(DefaultXmlNames.ATTR_externalRef)) >= 0) {
				metaData.setExternalRef(atts.getValue(i));
			}
			handlingMetadata = true;
	    }
	    else if (DefaultXmlNames.ELEMENT_Coords.equals(localName)) {
	    	handleCoords(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Baseline.equals(localName)) {
	    	handleBaseline(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TextRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.TextRegion);
	    	currentTextObject = (TextObject)currentRegion;
	    	
	    }
	    else if (DefaultXmlNames.ELEMENT_ImageRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.ImageRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_GraphicRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.GraphicRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_LineDrawingRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.LineDrawingRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_ChartRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.ChartRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_SeparatorRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.SeparatorRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_MathsRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.MathsRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_TableRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.TableRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_AdvertRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.AdvertRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_ChemRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.ChemRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_MusicRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.MusicRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_FrameRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.GraphicRegion);
	    	Variable v = currentRegion.getAttributes().get("type");
	    	if (v != null)
	    	{
				try {
					v.setValue(new StringValue("frame"));
				} catch (Exception e) {
				}
	    	}
	    }
	    else if (DefaultXmlNames.ELEMENT_NoiseRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.NoiseRegion);
	    }
	    else if (DefaultXmlNames.ELEMENT_UnknownRegion.equals(localName)) {
	    	handleRegion(atts, RegionType.UnknownRegion);
	    }
	    else if (localName.endsWith("Region")) {
	    	//Generic region
	    	handleRegion(atts, RegionType.getGenericType(localName));
	    }
	    else if (DefaultXmlNames.ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	if (currentRegion != null && currentRegion.getType() == RegionType.TextRegion)
	    		currentTextLine = ((TextRegion)currentRegion).createTextLine(readId(atts));
	    	currentGeometricObject = currentTextLine;
	    	currentTextObject = currentTextLine;
	    	contentObjects.put(currentTextLine.getId().toString(), currentTextLine);
	    	handleAttributeContainer(currentTextLine, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Word.equals(localName)) {
	    	currentWord = null;
	    	if (currentTextLine != null)
	    		currentWord = currentTextLine.createWord(readId(atts));
	    	currentGeometricObject = currentWord;
	    	currentTextObject = currentWord;
	    	contentObjects.put(currentWord.getId().toString(), currentWord);
	    	handleAttributeContainer(currentWord, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
	    	if (currentWord != null)
	    		currentGlyph = currentWord.createGlyph(readId(atts));
	    	currentGeometricObject = currentGlyph;
	    	currentTextObject = currentGlyph;
	    	contentObjects.put(currentGlyph.getId().toString(), currentGlyph);
	    	handleAttributeContainer(currentGlyph, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TextEquiv.equals(localName)) {
	    	handleTextEquiv(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_ReadingOrder.equals(localName)) {
	    	readingOrder = layout.createReadingOrder();
	    	currentLogicalGroup = null;
	    }
	    else if (	DefaultXmlNames.ELEMENT_OrderedGroup.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_OrderedGroupIndexed.equals(localName)) {
	    	Group group;
	    	if (currentLogicalGroup == null)  //Root group
	    		group = readingOrder.getRoot();
	    	else //Child group
	    	{
	    		try {
					group = currentLogicalGroup.createChildGroup();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
	    	}
	    	group.setOrdered(true);
	    	currentLogicalGroup = group;
	    	handleAttributeContainer(currentLogicalGroup, atts);
	    	parseId(group, atts);
	    	parseGroupRegionRef(group, atts);
	    }
	    else if (	DefaultXmlNames.ELEMENT_UnorderedGroup.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_UnorderedGroupIndexed.equals(localName)) {
	    	Group group;
	    	if (currentLogicalGroup == null)  //Root group
	    		group = readingOrder.getRoot();
	    	else //Child group
	    	{
	    		try {
					group = currentLogicalGroup.createChildGroup();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
	    	}
	    	group.setOrdered(false);
	    	currentLogicalGroup = group;
	    	handleAttributeContainer(currentLogicalGroup, atts);
	    	parseId(group, atts);
	    	parseGroupRegionRef(group, atts);
	    }
	    else if (	DefaultXmlNames.ELEMENT_RegionRef.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_RegionRefIndexed.equals(localName)) {
	    	
	    	if (currentRelation != null) {
				if ((i = atts.getIndex(DefaultXmlNames.ATTR_regionRef)) >= 0) {
					currentRelation.add(atts.getValue(i));
				}
	    	}
	    	else if (currentLogicalGroup != null) {
				if ((i = atts.getIndex(DefaultXmlNames.ATTR_regionRef)) >= 0) {
			    	currentLogicalGroup.addRegionRef(atts.getValue(i));
				}
	    	}
	    }
	    else if (DefaultXmlNames.ELEMENT_Layers.equals(localName)) {
	    	layout.createLayers();
	    	currentLogicalGroup = null;
	    }
	    else if (DefaultXmlNames.ELEMENT_Layer.equals(localName)) {
	    	Layer layer = layout.getLayers().createLayer();
	    	currentLogicalGroup = layer;
			//Z-Index
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_zIndex)) >= 0) {
				layer.setZIndex(new Integer(atts.getValue(i)));
			}
			//Caption
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_caption)) >= 0) {
				layer.setCaption(atts.getValue(i));
			}
			parseId(layer, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_AlternativeImage.equals(localName)) {
	    	handleAlternativeImage(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Relation.equals(localName)) {
	    	handleRelationStart(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TextStyle.equals(localName)) {
	    	parseTextStyle((ContentObject)currentTextObject, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Graphemes.equals(localName)) {
	    	currentGraphemeGroup = null;
	    }
	    else if (DefaultXmlNames.ELEMENT_Grapheme.equals(localName)) {
	    	handleGrapheme(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_GraphemeGroup.equals(localName)) {
	    	handleGraphemeGroup(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_NonPrintingChar.equals(localName)) {
	    	handleNonPrintingCharacter(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_UserDefined.equals(localName)) {
	    	if (currentGlyph != null)
	    		currentUserDefinedAttributes = currentGlyph.getUserDefinedAttributes();
	    	else if (currentWord != null)
	    		currentUserDefinedAttributes = currentWord.getUserDefinedAttributes();
	    	else if (currentTextLine != null)
	    		currentUserDefinedAttributes = currentTextLine.getUserDefinedAttributes();
	    	else if (currentLogicalGroup != null)
	    		currentUserDefinedAttributes = currentLogicalGroup.getUserDefinedAttributes();
	    	else if (currentRegion != null)
	    		currentUserDefinedAttributes = currentRegion.getUserDefinedAttributes();
	    	else {
	    		if (handlingMetadata && metaData != null)
	    			currentUserDefinedAttributes = metaData.getUserDefinedAttributes();
	    		else
	    			currentUserDefinedAttributes = page.getUserDefinedAttributes();
	    	}
	    }
	    else if (DefaultXmlNames.ELEMENT_Metadata.equals(localName)) {
			handlingMetadata = false;
	    }
	    else if (DefaultXmlNames.ELEMENT_UserAttribute.equals(localName)) {
	    	handleUserAttribute(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TableCellRole.equals(localName)) {
	    	handleTableCellRole(atts);
	    }
	}
	
	/**
	 * Receive notification of the end of an element.
	 * 
	 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
	 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
	 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
	 */
	public void endElement(String namespaceURI, String localName, String qName)
	      throws SAXException {
		
		//Handle accumulated text
		finishText();
		
		insideElement = null;
		
	    if (DefaultXmlNames.ELEMENT_Page.equals(localName)) {
	    	finaliseRelations();
	    }
	    else if (DefaultXmlNames.ELEMENT_Border.equals(localName)) {
	    	layout.setBorder(currentGeometricObject);
	    	currentGeometricObject = null;
	    }
	    else if (DefaultXmlNames.ELEMENT_PrintSpace.equals(localName)) {
	    	layout.setPrintSpace(currentGeometricObject);
	    	currentGeometricObject = null;
	    }
	    else if (	DefaultXmlNames.ELEMENT_TextRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_ImageRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_GraphicRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_LineDrawingRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_ChartRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_SeparatorRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_MathsRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_TableRegion.equals(localName)
	    		//|| 	DefaultXmlNames.ELEMENT_FrameRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_NoiseRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_UnknownRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_AdvertRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_ChemRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_MusicRegion.equals(localName)
	    	   ) {
	    	handleRegionEnd();
	    }
	    else if (	DefaultXmlNames.ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	currentGeometricObject = currentRegion;	//Set to parent
	    	currentTextObject = (TextObject)currentRegion;
			parsedTextEquivElements = 0;
	    }
	    else if (	DefaultXmlNames.ELEMENT_Word.equals(localName)) {
	    	currentWord = null;
	    	currentGeometricObject = currentTextLine;	//Set to parent
	    	currentTextObject = currentTextLine;
			parsedTextEquivElements = 0;
	    }
	    else if (	DefaultXmlNames.ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
	    	currentGeometricObject = currentWord;	//Set to parent
	    	currentTextObject = currentWord;
			parsedTextEquivElements = 0;
	    }
	    else if (DefaultXmlNames.ELEMENT_ReadingOrder.equals(localName)) {
	    	currentLogicalGroup = null;
	    	readingOrder = null;
	    }
	    else if (	DefaultXmlNames.ELEMENT_OrderedGroup.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_OrderedGroupIndexed.equals(localName)) {
	    	currentLogicalGroup = currentLogicalGroup.getParent();
	    }
	    else if (	DefaultXmlNames.ELEMENT_UnorderedGroup.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_UnorderedGroupIndexed.equals(localName)) {
	    	currentLogicalGroup = currentLogicalGroup.getParent();
	    }
	    else if (DefaultXmlNames.ELEMENT_Layer.equals(localName)) {
	    	currentLogicalGroup = null;
	    }
	    else if (DefaultXmlNames.ELEMENT_Relation.equals(localName)) {
	    	currentRelation = null;
	    }
	    else if (DefaultXmlNames.ELEMENT_Grapheme.equals(localName)) {
	    	if (currentGraphemeGroup == null) {
	    		currentGeometricObject = currentGlyph;
	    		currentTextObject = currentGlyph;
	    	}
	    	else
	    		currentTextObject = currentGraphemeGroup;
	    }
	    else if (DefaultXmlNames.ELEMENT_GraphemeGroup.equals(localName)) {
	    	currentGraphemeGroup = null;
	    	currentTextObject = currentGlyph;
	    }
	    else if (DefaultXmlNames.ELEMENT_NonPrintingChar.equals(localName)) {
	    	if (currentGraphemeGroup == null) 
	    		currentTextObject = currentGlyph;
	    	else
	    		currentTextObject = currentGraphemeGroup;
	    }
	    else if (DefaultXmlNames.ELEMENT_UserDefined.equals(localName)) {
	    	currentUserDefinedAttributes = null;
	    }
	}
	
	/**
	 * Receive notification of character data inside an element.
	 * @param ch - The characters.
	 * @param start - The start position in the character array.
	 * @param length - The number of characters to use from the character array.
	 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
	 */
	public void characters(char[] ch, int start, int length)
	      throws SAXException {

		String strValue = new String(ch, start, length);
		
		//Text might be parsed bit by bit, so we have to accumulate until a closing tag is found.
		if (currentText == null)
			currentText = new StringBuffer();
		currentText.append(strValue);
	}
	
	/**
	 * Writes accumulated text to the right object. 
	 */
	private void finishText() {
		if (currentText != null) {
			String strValue = currentText.toString();
			
			if (currentTextContent != null) {
				if (DefaultXmlNames.ELEMENT_Unicode.equals(insideElement)) {
					currentTextContent.setText(strValue);
				} 
				else if (DefaultXmlNames.ELEMENT_PlainText.equals(insideElement)) {
					currentTextContent.setPlainText(strValue);
				} 
			}
			if (metaData != null) {
				if (DefaultXmlNames.ELEMENT_Creator.equals(insideElement)) {
					metaData.setCreator(strValue);
				}
				else if (DefaultXmlNames.ELEMENT_Comments.equals(insideElement)) {
					metaData.setComments(strValue);
				}
			    else if (DefaultXmlNames.ELEMENT_Created.equals(insideElement)) {
			    	metaData.setCreationTime(parseDate(strValue));
			    }
			    else if (DefaultXmlNames.ELEMENT_LastChange.equals(insideElement)) {
			    	metaData.setLastModifiedTime(parseDate(strValue));
			    }
			}

			currentText = null;
		}
	}
	
	private void createPageObject() {
    	if (validatorProvider != null && schemaVersion != null) {
    		try {
				page = new Page(validatorProvider.getSchemaParser(schemaVersion));
				//page.setFormatVersion(schemaVersion);
			} catch (UnsupportedSchemaVersionException e) {
				e.printStackTrace();
				page = new Page();
			}
    	}
    	else
    		page = new Page();
    	
		layout = page.getLayout();
		metaData = page.getMetaData();
	}
	
	/**
	 * Reads the attributes of the Page element. 
	 */
	private void handlePageElement(Attributes atts) {
		int i;

		//Size
		int width = 0;
		int height = 0;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_imageWidth)) >= 0) {
			width = new Integer(atts.getValue(i));
		}
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_imageHeight)) >= 0) {
			height = new Integer(atts.getValue(i));
		}
		page.getLayout().setSize(width, height);
		
		//Image filename
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_imageFilename)) >= 0) {
			page.setImageFilename(atts.getValue(i));
		}
		
		//Other attributes (page type, ...)
		handleAttributeContainer(page, atts);
	}
	
	/**
	 * Adds an alternative image to the list of images of the page object. 
	 */
	private void handleAlternativeImage(Attributes atts) {
		if (page.getAlternativeImages() == null)
			return;
		
		AlternativeImage img = null;
		int i;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_filename)) >= 0) {
			img = new AlternativeImage(atts.getValue(i));
			page.getAlternativeImages().add(img);
		}
		else 
			return;

		//Comments
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_comments)) >= 0) {
			img.setComments(atts.getValue(i));
		}
	}
	
	/**
	 * Handles attributes of the TextEquiv element.
	 */
	private void handleTextEquiv(Attributes atts) {
		if (currentTextObject == null)
			return;

		currentTextContent = null;
		if (currentTextObject.getTextContentVariantCount() > parsedTextEquivElements)
			currentTextContent = currentTextObject.getTextContentVariant(parsedTextEquivElements);
		else
			currentTextContent = currentTextObject.addTextContentVariant();
		
		//Text content attributes
		handleAttributeContainer(currentTextContent, atts);
		
		parsedTextEquivElements++;

		//OCR confidence
		//int i;
		//if ((i = atts.getIndex(DefaultXmlNames.ATTR_conf)) >= 0) {
		//	Double confidence = new Double(atts.getValue(i));
		//	currentTextObject.setConfidence(confidence);
		//}
	}
	
	/**
	 * Reads the attributes of a content object.  
	 */
	private void handleAttributeContainer(AttributeContainer obj, Attributes atts) {
    	
		//Id
		//parseId(obj, atts);
		
		//Attributes
		VariableMap map = obj.getAttributes();
		int p;
		for (int i=0; i<map.getSize(); i++) {
			Variable var = map.get(i);
			String xmlName = getXmlAttributeName(var.getName());

			if ((p = atts.getIndex(xmlName)) >= 0) {
				var.parseValue(atts.getValue(p));
			}
		}
	}
	
	/**
	 * Reads text style specific attributes 
	 */
	private void parseTextStyle(ContentObject obj, Attributes atts) {
		if (obj == null)
			return;
		VariableMap objectAttrs = obj.getAttributes();
		if (objectAttrs != null) {
			int p;
			for (int i=0; i<objectAttrs.getSize(); i++) {
				Variable var = objectAttrs.get(i);
				String xmlName = getXmlAttributeName(var.getName());

				if ((p = atts.getIndex(xmlName)) >= 0) {
					var.parseValue(atts.getValue(p));
				}
			}
		}
	}

	private void handleCoords(Attributes atts) {
		if (currentGeometricObject != null)	{
			Polygon polygon = new Polygon();
			currentGeometricObject.setCoords(polygon);
			handlePointsAttribute(polygon, atts);
		}
	}
	
	private void handleBaseline(Attributes atts) {
		if (currentTextLine != null)	{
			Polygon baseline = new Polygon();
			currentTextLine.setBaseline(baseline);
			handlePointsAttribute(baseline, atts);
		}
	}

	private void handleRelationStart(Attributes atts) {
		if (tempRelations == null)
			tempRelations = new ArrayList<List<String>>();
		
		int i;
		//Type
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_type)) >= 0) {
			List<String> rel = new ArrayList<String>();
			tempRelations.add(rel);
			currentRelation = rel;

			rel.add(atts.getValue(i));

			//Custom
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_custom)) >= 0) {
				currentRelation.add(atts.getValue(i));
			} else
				currentRelation.add("");
			//Comments
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_comments)) >= 0) {
				currentRelation.add(atts.getValue(i));
			} else
				currentRelation.add("");
		}
	}
	
	/**
	 * Translates the temporary relations data structure to the proper one. 
	 */
	private void finaliseRelations() {
		if (tempRelations == null)
			return;
		
		PageLayout layout = page.getLayout();
		Relations relations = layout.getRelations();
		if (relations == null)
			return;
		
		for (int i=0; i<tempRelations.size(); i++) {
			List<String> rel = tempRelations.get(i);
			if (rel != null && rel.size() == 5) {
				RelationType type = "link".equals(rel.get(0)) ? RelationType.Link : RelationType.Join;
				String custom = rel.get(1);
				String comments = rel.get(2);
				String id1 = rel.get(3);
				String id2 = rel.get(4);
				
				ContentObject obj1 = contentObjects.get(id1);
				ContentObject obj2 = contentObjects.get(id2);
				
				if (obj1 != null && obj2 != null) {
					ContentObjectRelation relation = new ContentObjectRelation(obj1, obj2, type);
					relation.setCustomField(custom);
					relation.setComments(comments);
					relations.addRelation(relation);
				}
			}
		}
	}

	private void handlePointsAttribute(Polygon polygon, Attributes atts) {
		//Points
		int i;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_points)) >= 0) {
			String pointList = atts.getValue(i);
			
			//Split using space
			String[] pointStrings = pointList.split(" ");
			
			for (i = 0; i<pointStrings.length; i++) {
				//Split using comma
				String[] coords = pointStrings[i].split(",");
				if (coords.length == 2) {
					polygon.addPoint(new Integer(coords[0]), new Integer(coords[1]));
				}
			}
		}
	}
	
	private String getXmlAttributeName(String name) {
		return name; //TODO Should there be a mechanism to translate attribute names to XML names?
	}
	
	/**
	 * Parses a date given as string using the default date format. 
	 */
	private Date parseDate(String str) {
		try {
			return DATE_FORMAT.parse(str);
		} catch (ParseException e) {
			return new Date();
		}
	}
	
	private String readId(Attributes atts) {
		int i;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_id)) >= 0)
			return atts.getValue(i);
		return "";
	}

	/**
	 * Reads the ID attribute and sets it in the Identifiable object. 
	 */
	private void parseId(Identifiable ident, Attributes atts) {
		int i;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_id)) >= 0) {
			try {
				ident.setId(atts.getValue(i));
			} catch (InvalidIdException e) {
				//TODO Manage ID conflicts
				e.printStackTrace();
			}
		}
	}
	
	private void parseGroupRegionRef(Group group, Attributes atts) {
		int i;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_regionRef)) >= 0) {
			group.setRegionRef(atts.getValue(i));
		}
	}
	
	private void handleRegion(Attributes atts, RegionType type) {
		currentRegion = layout.createRegion(type, readId(atts), (RegionContainer)currentRegion); //Either adds it to the page (if currentRegion is null) or to the current region (as nested region)
    	regionStack.push(currentRegion);
    	currentGeometricObject = currentRegion;
    	contentObjects.put(currentRegion.getId().toString(), currentRegion);
    	handleAttributeContainer(currentRegion, atts);
	}
	
	private void handleRegionEnd() {
		regionStack.pop();
		if (!regionStack.isEmpty()) {
			currentRegion = regionStack.lastElement();
			if (currentRegion instanceof TextObject)
				currentTextObject = (TextObject)currentRegion;
	    	currentGeometricObject = currentRegion;
		}
		else { 
			currentRegion = null;
			currentGeometricObject = null;
			currentTextObject = null;
		}
		parsedTextEquivElements = 0;
	}

	private void handleGrapheme(Attributes atts) {
		if (currentGlyph != null) {
			currentGraphemeElement = currentGlyph.createGraphemeElement(readId(atts), LowLevelTextType.Grapheme, currentGraphemeGroup);
			currentGeometricObject = (GeometricObject)currentGraphemeElement;
			currentTextObject = currentGraphemeElement;
			handleAttributeContainer(currentGraphemeElement, atts);
		}
	}

	private void handleGraphemeGroup(Attributes atts) {
		if (currentGlyph != null) {
			currentGraphemeElement = currentGlyph.createGraphemeElement(readId(atts), LowLevelTextType.GraphemeGroup, null);
			currentTextObject = currentGraphemeElement;
			handleAttributeContainer(currentGraphemeElement, atts);
		}
	}
	
	private void handleNonPrintingCharacter(Attributes atts) {
		if (currentGlyph != null) {
			currentGraphemeElement = currentGlyph.createGraphemeElement(readId(atts), LowLevelTextType.NonPrintingCharacter, currentGraphemeGroup);
			currentTextObject = currentGraphemeElement;
			handleAttributeContainer(currentGraphemeElement, atts);
		}
	}
	
	private void handleUserAttribute(Attributes atts) {
		if (currentUserDefinedAttributes == null)
			return;
		
		String name = "unknown";
		String description = null;
		String datatype = "xsd:string";
		String value = "";
		int i;
		//Name
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_name)) >= 0)
			name = atts.getValue(i);
		//Description
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_description)) >= 0)
			description = atts.getValue(i);
		//Type
		else if ((i = atts.getIndex(DefaultXmlNames.ATTR_type)) >= 0)
			datatype = atts.getValue(i);
		//Value
		else if ((i = atts.getIndex(DefaultXmlNames.ATTR_value)) >= 0)
			datatype = atts.getValue(i);
		
		//Add attribute
		try {
			Variable attr = null;
			if ("xsd:string".equals(datatype))
				attr = new StringVariable(name, new StringValue(value));
			else if ("xsd:integer".equals(datatype))
				attr = new IntegerVariable(name, new IntegerValue(Integer.parseInt(value)));
			else if ("xsd:float".equals(datatype))
				attr = new DoubleVariable(name, new DoubleValue(Double.parseDouble(value)));
			else if ("xsd:boolean".equals(datatype))
				attr = new BooleanVariable(name, new BooleanValue(Boolean.parseBoolean(value)));
			
			if (attr != null) {
				if (description != null)
					attr.setDescription(description);
				currentUserDefinedAttributes.add(attr);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private void handleTableCellRole(Attributes atts) {
		if (currentRegion == null)
			return;
		
		RegionRole role = currentRegion.addRole(RoleType.TableCellRole);
		if (role != null)
			handleAttributeContainer(role, atts);
	}
}
