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
import java.util.Date;

import org.primaresearch.dla.page.MetaData;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.GeometricObjectImpl;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.TextObject;
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
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handler for PAGE schema version 2010-01-12 and older.
 * @author Christian Clausner
 *
 */
public class SaxPageHandlerLegacy extends SaxPageHandler {

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	private Page page = null;
	private PageLayout layout = null;
	private MetaData metaData = null;

	private GeometricObject currentGeometricObject = null;
	private Region currentRegion = null;
	private TextLine currentTextLine = null;
	private Word currentWord = null;
	private Glyph currentGlyph = null;
	private TextObject currentTextObject = null;
	private String insideElement = null;
	private ReadingOrder readingOrder = null;
	private Group currentLogicalGroup;
	private StringBuffer currentText = null;
	XmlModelAndValidatorProvider validatorProvider;
	XmlFormatVersion schemaVersion;
	
	public SaxPageHandlerLegacy(XmlModelAndValidatorProvider validatorProvider, XmlFormatVersion schemaVersion) {
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
		
		//Handle accumulated text
		finishText();
		
		insideElement = localName;
				
	    if (DefaultXmlNames.ELEMENT_PcGts.equals(localName)){
	    	createPageObject();
			//GtsID
			int i;
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
	    else if (DefaultXmlNames.ELEMENT_Coords.equals(localName)) {
	    	if (currentGeometricObject != null)
	    		currentGeometricObject.setCoords(new Polygon());
	    }
	    else if (DefaultXmlNames.ELEMENT_Point.equals(localName)) {
	    	handlePolygonPoint(atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TextRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.TextRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	currentTextObject = (TextObject)currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_ImageRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.ImageRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_GraphicRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.GraphicRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_LineDrawingRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.LineDrawingRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_ChartRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.ChartRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_SeparatorRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.SeparatorRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_MathsRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.MathsRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TableRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.TableRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_FrameRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.GraphicRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_NoiseRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.NoiseRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_UnknownRegion.equals(localName)) {
	    	currentRegion = layout.createRegion(RegionType.UnknownRegion, readId(atts));
	    	currentGeometricObject = currentRegion;
	    	handleContentObject(currentRegion, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	if (currentRegion != null && currentRegion.getType() == RegionType.TextRegion)
	    		currentTextLine = ((TextRegion)currentRegion).createTextLine(readId(atts));
	    	currentGeometricObject = currentTextLine;
	    	currentTextObject = currentTextLine;
	    	handleContentObject(currentTextLine, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Word.equals(localName)) {
	    	currentWord = null;
	    	if (currentTextLine != null)
	    		currentWord = currentTextLine.createWord(readId(atts));
	    	currentGeometricObject = currentWord;
	    	currentTextObject = currentWord;
	    	handleContentObject(currentWord, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
	    	if (currentWord != null)
	    		currentGlyph = currentWord.createGlyph(readId(atts));
	    	currentGeometricObject = currentGlyph;
	    	currentTextObject = currentGlyph;
	    	handleContentObject(currentGlyph, atts);
	    }
	    else if (DefaultXmlNames.ELEMENT_ReadingOrder.equals(localName)) {
	    	readingOrder = layout.createReadingOrder();
	    	currentLogicalGroup = readingOrder.getRoot();
	    }
	    else if (	DefaultXmlNames.ELEMENT_OrderedGroup.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_OrderedGroupIndexed.equals(localName)) {
	    	if (currentLogicalGroup == readingOrder.getRoot())
	    		currentLogicalGroup.setOrdered(DefaultXmlNames.ELEMENT_OrderedGroupIndexed.equals(localName));
	    	
	    	Group group;
    		try {
				group = currentLogicalGroup.createChildGroup();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	    	group.setOrdered(true);
	    	parseId(group, atts);
	    	
	    	currentLogicalGroup = group;
	    }
	    else if (	DefaultXmlNames.ELEMENT_UnorderedGroup.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_UnorderedGroupIndexed.equals(localName)) {
	    	if (currentLogicalGroup == readingOrder.getRoot())
	    		currentLogicalGroup.setOrdered(DefaultXmlNames.ELEMENT_UnorderedGroupIndexed.equals(localName));
	    	
	    	Group group;
    		try {
				group = currentLogicalGroup.createChildGroup();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	    	group.setOrdered(false);
	    	parseId(group, atts);
	    	
	    	currentLogicalGroup = group;
	    }
	    else if (	DefaultXmlNames.ELEMENT_RegionRef.equals(localName)
	    		||	DefaultXmlNames.ELEMENT_RegionRefIndexed.equals(localName)) {
	    	if (readingOrder != null) {
	    		if (currentLogicalGroup == readingOrder.getRoot())
	    			currentLogicalGroup.setOrdered(DefaultXmlNames.ELEMENT_RegionRefIndexed.equals(localName));
	    	}
	    	
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_regionRef)) >= 0) {
		    	currentLogicalGroup.addRegionRef(atts.getValue(i));
			}
	    }
	    else if (DefaultXmlNames.ELEMENT_Layers.equals(localName)) {
	    	layout.createLayers();
	    	currentLogicalGroup = null;
	    }
	    else if (DefaultXmlNames.ELEMENT_Layer.equals(localName)) {
	    	Layer layer = layout.getLayers().createLayer();
	    	currentLogicalGroup = layer;
			int i;
			if ((i = atts.getIndex(DefaultXmlNames.ATTR_zIndex)) >= 0) {
				layer.setZIndex(new Integer(atts.getValue(i)));
			}
			parseId(layer, atts);
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
		
	    if (DefaultXmlNames.ELEMENT_Border.equals(localName)) {
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
	    		|| 	DefaultXmlNames.ELEMENT_FrameRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_NoiseRegion.equals(localName)
	    		|| 	DefaultXmlNames.ELEMENT_UnknownRegion.equals(localName)
	    	   ) {
	    	currentRegion = null;
	    	currentGeometricObject = null;
	    	currentTextObject = null;
	    }
	    else if (	DefaultXmlNames.ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	currentGeometricObject = currentRegion;	//Set to parent
	    	currentTextObject = (TextObject)currentRegion;
	    }
	    else if (	DefaultXmlNames.ELEMENT_Word.equals(localName)) {
	    	currentWord = null;
	    	currentGeometricObject = currentTextLine;	//Set to parent
	    	currentTextObject = currentTextLine;
	    }
	    else if (	DefaultXmlNames.ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
	    	currentGeometricObject = currentWord;	//Set to parent
	    	currentTextObject = currentWord;
	    }
	    else if (DefaultXmlNames.ELEMENT_ReadingOrder.equals(localName)) {
	    	
	    	//If the root group only contains one group as member, we make that member the root
	    	Group root = readingOrder.getRoot();
	    	if (root.getSize() == 1 && root.getMember(0) instanceof Group) {
	    		Group child = (Group)root.getMember(0);
	    		root.setOrdered(child.isOrdered());
	    		//Copy all children of the child group to the root group
	    		while (child.getSize()>0)
	    			child.getMember(0).moveTo(root);
	    		//Remove the child group from the root
	    		root.delete(child);
	    	}
	    	
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
			
			if (currentTextObject != null) {
				if (DefaultXmlNames.ELEMENT_Unicode.equals(insideElement)) {
					currentTextObject.setText(strValue);
				} 
				else if (DefaultXmlNames.ELEMENT_PlainText.equals(insideElement)) {
					currentTextObject.setPlainText(strValue);
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
	
	}
	
	/**
	 * Reads the coordinates of a single polygon point. 
	 */
	private void handlePolygonPoint(Attributes atts) {
		if (currentGeometricObject == null || currentGeometricObject.getCoords() == null)
			return;
		
		int x=0;
		int y=0;
		int i;
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_x)) >= 0) {
			x = new Integer(atts.getValue(i));
		}
		if ((i = atts.getIndex(DefaultXmlNames.ATTR_y)) >= 0) {
			y = new Integer(atts.getValue(i));
		}
		currentGeometricObject.getCoords().addPoint(x, y);
	}
	
	/**
	 * Reads the attributes of a content object.  
	 */
	private void handleContentObject(ContentObject obj, Attributes atts) {
    	
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


}
