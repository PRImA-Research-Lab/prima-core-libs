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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextObject;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.dla.page.metadata.MetaData;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.maths.geometry.Polygon;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Experimental SAX XML handler to read HOCR XHTML files (e.g. output from Tesseract OCR engine).
 * 
 * @author Christian Clausner
 *
 */
public class SaxPageHandler_Hocr extends SaxPageHandler {

	private static final String ELEMENT_html 	= "html";
	//private static final String ELEMENT_head 	= "head";
	//private static final String ELEMENT_body 	= "body";
	private static final String ELEMENT_meta 	= "meta";
	private static final String ELEMENT_div 	= "div";
	private static final String ELEMENT_p 		= "p";
	private static final String ELEMENT_span 	= "span";
	//private static final String ELEMENT_strong 	= "strong";
	//private static final String ELEMENT_em 		= "em";

	private static final String ATTR_name 		= "name";
	private static final String ATTR_content 	= "content";
	private static final String ATTR_class 		= "class";
	private static final String ATTR_id 		= "id";
	private static final String ATTR_title 		= "title";

	private static final String CLASS_page 		= "ocr_page";
	private static final String CLASS_area 		= "ocr_carea";
	private static final String CLASS_paragraph = "ocr_par";
	private static final String CLASS_line 		= "ocr_line";
	private static final String CLASS_word 		= "ocrx_word";

	private Page page;
	private PageLayout layout = null;
	private TextRegion currentTextRegion = null;
	private TextLine currentLine = null;
	private Word currentWord = null;
	private StringBuffer currentText = null;

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
		int i;
		
		//Handle accumulated text
		//finishText();
		
				
	    if (ELEMENT_html.equals(localName)){
	    	page = new Page(PageXmlInputOutput.getLatestSchemaModel());
	    	layout = page.getLayout();
	    }
	    else if (ELEMENT_meta.equals(localName)){
	    	handleMetaElement(atts);
	    }
	    else if (ELEMENT_div.equals(localName)){
	    	//Check class
			if ((i = atts.getIndex(ATTR_class)) >= 0) {
				String elementClass = atts.getValue(i);
				//Page
				if (CLASS_page.equals(elementClass))
					handlePageElement(atts);
				//Area (block)
				else if (CLASS_area.equals(elementClass))
					;
			}
	    }
	    else if (ELEMENT_p.equals(localName)){
	    	//Check class
			if ((i = atts.getIndex(ATTR_class)) >= 0) {
				String elementClass = atts.getValue(i);
				//Paragraph
				if (CLASS_paragraph.equals(elementClass))
					handleParagraphElement(atts);
			}
	    }
	    else if (ELEMENT_span.equals(localName)){
	    	//Check class
			if ((i = atts.getIndex(ATTR_class)) >= 0) {
				String elementClass = atts.getValue(i);
				//Text line
				if (CLASS_line.equals(elementClass))
					handleTextLineElement(atts);
				//Word
				else if (CLASS_word.equals(elementClass))
					handleWordElement(atts);
			}
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
		//finishText();
		
	    if (ELEMENT_html.equals(localName)){
	    }
	    else if (ELEMENT_div.equals(localName)){
	    }
	    else if (ELEMENT_p.equals(localName)) {
    		//Accumulate text from lines
    		List<LowLevelTextObject> lines = currentTextRegion.getTextObjectsSorted();
    		if (lines != null) {
    			String text = "";
    			for (Iterator<LowLevelTextObject> it = lines.iterator(); it.hasNext(); ) {
    				if (!text.isEmpty())
    					text += "\r\n";
    				text += it.next().getText();
    			}
    			currentTextRegion.setText(text);
    		}
	    	currentTextRegion = null;
	    }
	    else if (ELEMENT_span.equals(localName)) {
	    	if (currentWord != null) {
	    		if (currentText != null) {
	    			currentWord.setText(currentText.toString().trim());
	    			currentText = null;
	    		}
	    		currentWord = null;
	    	}
	    	else if (currentLine != null) {
	    		//Accumulate text from words
	    		List<LowLevelTextObject> words = currentLine.getTextObjectsSorted();
	    		if (words != null) {
	    			String text = "";
	    			for (Iterator<LowLevelTextObject> it = words.iterator(); it.hasNext(); ) {
	    				if (!text.isEmpty())
	    					text += " ";
	    				text += it.next().getText();
	    			}
	    			currentLine.setText(text);
	    		}
	    		currentLine = null;
	    	}
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
	
	///**
	// * Writes accumulated text to the right object. 
	// */
	//private void finishText() {
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
	//}
	
	/**
	 * Parses a metadata element from the header. 
	 */
	private void handleMetaElement(Attributes atts) {
		int i;

		//Size
		String name = null;
		String content = null;
		if ((i = atts.getIndex(ATTR_name)) >= 0) {
			name = atts.getValue(i);
		}
		if ((i = atts.getIndex(ATTR_content)) >= 0) {
			content = atts.getValue(i);
		}
		
		if (name != null && content != null)
			addComment(name + ": " + content);
	}
	
	/**
	 * Adds a comment to the comments text (also adds a line break before if not the first entry).
	 */
	private void addComment(String comment) {
		if (page == null || comment == null || comment.isEmpty())
			return;
		
		MetaData metadata = page.getMetaData();
		if (metadata != null) {
			String comments = metadata.getComments();
			if (comments == null)
				comments = "";
			if (!comments.isEmpty())
				comments += "\r\n";
			comments += comment;
			metadata.setComments(comments);
		}
	}
	
	/**
	 * Parses the attributes of the page 'div' node.
	 */
	private void handlePageElement(Attributes atts) {
		int i;

		if (page == null)
			return;
		
		//ID
		if ((i = atts.getIndex(ATTR_id)) >= 0) {
			try {
				page.setGtsId(atts.getValue(i));
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
		}
		
		//Image name and dimensions
		if ((i = atts.getIndex(ATTR_title)) >= 0) {
			String title = atts.getValue(i);
			String parts[] = title.split("; ");
			for (String part : parts) {
				//Image
				if (part.startsWith("image")) {
					String image = null;
					//Filename
					// Path
					if (part.contains(File.separator))
						image = part.substring(part.lastIndexOf(File.separator)+1);
					// No path
					else if (part.contains(" \""))
						image = part.substring(part.indexOf(" \"")+1);

					if (image != null) {
						//Remove quotation mark
						if (image.endsWith("\""))
							image = image.substring(0, image.length()-1);
						page.setImageFilename(image);
					}
				}
				//Bounding box
				else if (part.startsWith("bbox")) {
					String coords[] = part.split(" ");
					if (coords.length == 5) {
						layout.setSize(new Integer(coords[3]), new Integer(coords[4]));	//This should be +1 but they seem to use x2/y2 as width/height
					}
				}
			}
		}	
	}
	
	/**
	 * Parses the given paragraph 'p' node.
	 */
	private void handleParagraphElement(Attributes atts) {
		int i;

		if (page == null)
			return;
		
		//ID
		String id = null;
		if ((i = atts.getIndex(ATTR_id)) >= 0) {
			id = atts.getValue(i);
		}
		
		//Create region
		currentTextRegion = (TextRegion)layout.createRegion(RegionType.TextRegion, id);

		//Coords
		if ((i = atts.getIndex(ATTR_title)) >= 0) {
			Polygon coords = parseCoords(atts.getValue(i));
			if (coords != null)
				currentTextRegion.setCoords(coords);
		}

	}
	
	/**
	 * Parses the given paragraph 'span' node of class 'ocr_line'.
	 */
	private void handleTextLineElement(Attributes atts) {
		int i;

		if (page == null || currentTextRegion == null)
			return;
		
		//ID
		String id = null;
		if ((i = atts.getIndex(ATTR_id)) >= 0) {
			id = atts.getValue(i);
		}
	
		//Create line
		currentLine = currentTextRegion.createTextLine(id);
		
		//Coords
		if ((i = atts.getIndex(ATTR_title)) >= 0) {
			Polygon coords = parseCoords(atts.getValue(i));
			if (coords != null)
				currentLine.setCoords(coords);
		}
	}
	
	/**
	 * Parses the given paragraph 'span' node of class 'ocr_line'.
	 */
	private void handleWordElement(Attributes atts) {
		int i;

		if (page == null || currentLine == null)
			return;
		
		//ID
		String id = null;
		if ((i = atts.getIndex(ATTR_id)) >= 0) {
			id = atts.getValue(i);
		}
	
		//Create word
		currentWord = currentLine.createWord(id);
		
		//Coords
		if ((i = atts.getIndex(ATTR_title)) >= 0) {
			Polygon coords = parseCoords(atts.getValue(i));
			if (coords != null)
				currentWord.setCoords(coords);
		}
	}
	
	/**
	 * Parses a text encoded bounding box and returns a polygon.
	 * @return Box shaped polygon or null 
	 */
	private Polygon parseCoords(String coordsString) {
		Polygon ret = null;
		String toplevelParts[] = coordsString.split(";");
		for (int i=0; i<toplevelParts.length; i++) {
		
			if (toplevelParts[i].startsWith("bbox")) {
				String parts[] = toplevelParts[i].split(" ");
				if (parts.length == 5) {
					ret = new Polygon();
					int x1 = new Integer(parts[1]);
					int y1 = new Integer(parts[2]);
					int x2 = new Integer(parts[3]);
					int y2 = new Integer(parts[4]);
					ret.addPoint(x1,y1);
					ret.addPoint(x2,y1);
					ret.addPoint(x2,y2);
					ret.addPoint(x1,y2);
				}
				break;
			}
		}
		return ret;
	}


}
