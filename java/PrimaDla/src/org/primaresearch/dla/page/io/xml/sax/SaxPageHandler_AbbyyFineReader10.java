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
package org.primaresearch.dla.page.io.xml.sax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.dla.page.metadata.MetaData;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.maths.geometry.Rect;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Experimental SAX XML handler to read Abbyy FineReader 10 XML files.
 * 
 * @author Christian Clausner
 *
 */
public class SaxPageHandler_AbbyyFineReader10 extends SaxPageHandler {
	
	private static final String ELEMENT_document = "document";
	private static final String ELEMENT_page = "page";
	private static final String ELEMENT_region = "region";
	private static final String ELEMENT_rect = "rect";
	private static final String ELEMENT_block = "block";
	
	private static final String ATTR_producer = "producer";
	private static final String ATTR_width = "width";
	private static final String ATTR_height = "height";
	private static final String ATTR_originalCoords = "originalCoords";
	private static final String ATTR_l = "l";
	private static final String ATTR_t = "t";
	private static final String ATTR_r = "r";
	private static final String ATTR_b = "b";
	private static final String ATTR_blockType = "blockType";
	
	private static Comparator<Rect> rectComparator = new SortRectsVertically();
	
	private Page page;
	private PageLayout layout = null;
	private MetaData metaData = null;

	@SuppressWarnings("unused")
	private String insideElement = null;
	private List<Rect> currentRects = null;
	private GeometricObject currentGeometricObject = null;
	private Region currentRegion = null;

	private XmlModelAndValidatorProvider validatorProvider;
	private XmlFormatVersion schemaVersion;
	
	public SaxPageHandler_AbbyyFineReader10(XmlModelAndValidatorProvider validatorProvider, XmlFormatVersion schemaVersion) {
		this.validatorProvider = validatorProvider;
		this.schemaVersion = schemaVersion;
	}

	@Override
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
				
	    if (ELEMENT_document.equals(localName)){
	    	createPageObject();
			//Producer
			int i;
			if ((i = atts.getIndex(ATTR_producer)) >= 0) {
				if (metaData != null)
					metaData.setCreator(atts.getValue(i));
			}
	    }
	    else if (ELEMENT_page.equals(localName)){
	    	handlePageElement(atts);
	    } 
	    else if (ELEMENT_region.equals(localName)) {
	    	if (currentGeometricObject != null)
	    		currentGeometricObject.setCoords(new Polygon());
	    	currentRects = new ArrayList<Rect>();
	    }
	    else if (ELEMENT_rect.equals(localName)) {
	    	handleRegionRect(atts);
	    }
	    else if (ELEMENT_block.equals(localName)) {
	    	currentRegion = createRegion(atts);
	    	currentGeometricObject = currentRegion;
	    }
	    /*else if (ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	if (currentRegion != null && currentRegion.getType() == RegionType.TextRegion)
	    		currentTextLine = ((TextRegion)currentRegion).createTextLine(readId(atts));
	    	currentGeometricObject = currentTextLine;
	    	currentTextObject = currentTextLine;
	    	handleContentObject(currentTextLine, atts);
	    }
	    else if (ELEMENT_Word.equals(localName)) {
	    	currentWord = null;
	    	if (currentTextLine != null)
	    		currentWord = currentTextLine.createWord(readId(atts));
	    	currentGeometricObject = currentWord;
	    	currentTextObject = currentWord;
	    	handleContentObject(currentWord, atts);
	    }
	    else if (ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
	    	if (currentWord != null)
	    		currentGlyph = currentWord.createGlyph(readId(atts));
	    	currentGeometricObject = currentGlyph;
	    	currentTextObject = currentGlyph;
	    	handleContentObject(currentGlyph, atts);
	    }*/
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
		
	    if (	ELEMENT_block.equals(localName)) {
	    	if (currentRects != null && !currentRects.isEmpty() && currentGeometricObject != null) {	    		
	    		Polygon polygon = convertToPolygon(currentRects);
	    		currentGeometricObject.setCoords(polygon);
	    	}
	    	currentRegion = null;
	    	currentGeometricObject = null;
	    }
	    /*else if (	ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	currentGeometricObject = currentRegion;	//Set to parent
	    	currentTextObject = (TextObject)currentRegion;
	    }
	    else if (	ELEMENT_Word.equals(localName)) {
	    	currentWord = null;
	    	currentGeometricObject = currentTextLine;	//Set to parent
	    	currentTextObject = currentTextLine;
	    }
	    else if (	ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
	    	currentGeometricObject = currentWord;	//Set to parent
	    	currentTextObject = currentWord;
	    }*/
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

		//String strValue = new String(ch, start, length);
		
		//Text might be parsed bit by bit, so we have to accumulate until a closing tag is found.
		//if (currentText == null)
		//	currentText = new StringBuffer();
		//currentText.append(strValue);
	}
	
	/**
	 * Writes accumulated text to the right object. 
	 */
	private void finishText() {
		/*if (currentText != null) {
			String strValue = currentText.toString();
			
			if (currentTextObject != null) {
				if (ELEMENT_Unicode.equals(insideElement)) {
					currentTextObject.setText(strValue);
				} 
				else if (ELEMENT_PlainText.equals(insideElement)) {
					currentTextObject.setPlainText(strValue);
				} 
			}
			if (metaData != null) {
				if (ELEMENT_Creator.equals(insideElement)) {
					metaData.setCreator(strValue);
				}
				else if (ELEMENT_Comments.equals(insideElement)) {
					metaData.setComments(strValue);
				}
			    else if (ELEMENT_Created.equals(insideElement)) {
			    	metaData.setCreationTime(parseDate(strValue));
			    }
			    else if (ELEMENT_LastChange.equals(insideElement)) {
			    	metaData.setLastModifiedTime(parseDate(strValue));
			    }
			}

			currentText = null;
		}*/
	}
	
	private void createPageObject() {
    	if (validatorProvider != null && schemaVersion != null) {
    		try {
				page = new Page(validatorProvider.getSchemaParser(validatorProvider.getLatestSchemaVersion()));
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
		if ((i = atts.getIndex(ATTR_width)) >= 0) {
			width = new Integer(atts.getValue(i));
		}
		if ((i = atts.getIndex(ATTR_height)) >= 0) {
			height = new Integer(atts.getValue(i));
		}
		page.getLayout().setSize(width, height);

		//Original coords (1==coords relative to original image, otherwise coords relative to deskewed image)
		if ((i = atts.getIndex(ATTR_originalCoords)) >= 0) {
			if (metaData != null) {
				String comments = metaData.getComments();
				if (comments == null)
					comments = "";
				else
					comments += "\n";
				comments += "Original coords: "+(new Integer(atts.getValue(i)).equals(1) ? "true" : "false");
			}
		}
	}
	
	/**
	 * Reads the coordinates of a single rectangle. 
	 */
	private void handleRegionRect(Attributes atts) {
		if (currentRects == null)
			return;
		
	
		int i, l=0, t=0, r=0, b=0;
		if ((i = atts.getIndex(ATTR_l)) >= 0) {
			l = new Integer(atts.getValue(i));
		}
		if ((i = atts.getIndex(ATTR_t)) >= 0) {
			t = new Integer(atts.getValue(i));
		}
		if ((i = atts.getIndex(ATTR_r)) >= 0) {
			r = new Integer(atts.getValue(i));
		}
		if ((i = atts.getIndex(ATTR_b)) >= 0) {
			b = new Integer(atts.getValue(i));
		}
		currentRects.add(new Rect(l, t, r, b));
	}
	
	
	//private String getXmlAttributeName(String name) {
	//	return name; //TODO Should there be a mechanism to translate attribute names to XML names?
	//}
	
	private Region createRegion(Attributes atts) {
		String abbyyType = null;
		int i;
		if ((i = atts.getIndex(ATTR_blockType)) >= 0) {
			abbyyType = atts.getValue(i);
		}
		
		RegionType primaType = RegionType.UnknownRegion;
		if ("Text".equals(abbyyType))
			primaType = RegionType.TextRegion;
		else if ("Table".equals(abbyyType))
			primaType = RegionType.TableRegion;
		else if ("Picture".equals(abbyyType))
			primaType = RegionType.ImageRegion;
		else if ("Barcode".equals(abbyyType))
			primaType = RegionType.GraphicRegion;
		else if ("Separator".equals(abbyyType))
			primaType = RegionType.SeparatorRegion;
		else if ("SeparatorsBox".equals(abbyyType))
			primaType = RegionType.SeparatorRegion;
		
		return layout.createRegion(primaType);
	}
	
	/**
	 * Converts a stack of rectangles to a polygon. 
	 */
	private Polygon convertToPolygon(List<Rect> rects) {
		
		if (rects.isEmpty())
			return null;
		
		Polygon polygon = new Polygon();
		
		//One rectangle
		if (rects.size() == 1) {
			Rect rect = rects.get(0);
			polygon.addPoint(rect.left, rect.top);
			polygon.addPoint(rect.right, rect.top);
			polygon.addPoint(rect.right, rect.bottom);
			polygon.addPoint(rect.left, rect.bottom);
		}
		//Multiple rectangles
		else {
			//Sort rects vertically
			Collections.sort(rects, rectComparator);
			
			//Create polygon
			// Right sides
			Rect rect;
			for (int i=0; i<rects.size(); i++) {
				rect = rects.get(i);
				polygon.addPoint(rect.right, rect.top);
				polygon.addPoint(rect.right, rect.bottom);
			}
			// Left sides
			for (int i=rects.size()-1; i>=0; i--) {
				rect = rects.get(i);
				polygon.addPoint(rect.left, rect.bottom);
				polygon.addPoint(rect.left, rect.top);
			}
		}
		
		return polygon; 
	}
	
	private static class SortRectsVertically implements Comparator<Rect> {
		@Override
		public int compare(Rect rect1, Rect rect2) {
			int center1 = (rect1.top + rect1.bottom) / 2; 
			int center2 = (rect2.top + rect2.bottom) / 2;
			if (center1 < center2)
				return -1;
			if (center1 > center2)
				return 1;
			return 0;
		}
	}
	

}
