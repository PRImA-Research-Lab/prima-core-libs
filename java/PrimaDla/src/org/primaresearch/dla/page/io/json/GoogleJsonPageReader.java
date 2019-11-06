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
package org.primaresearch.dla.page.io.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.InputSource;
import org.primaresearch.dla.page.io.PageReader;
import org.primaresearch.dla.page.io.PageReaderBase;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.maths.geometry.Point;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.maths.geometry.Rect;

/**
 * Reads Google Cloud Vision JSON output and returns Page object.
 * (2019 JSON format; only reads the first page)
 * 
 * @author Christian Clausner
 *
 */
public class GoogleJsonPageReader extends PageReaderBase implements PageReader {
	
	private static final String KEY_responses = "responses";
	private static final String KEY_fullTextAnnotation = "fullTextAnnotation";
	private static final String KEY_pages = "pages";
	private static final String KEY_property = "property";
	private static final String KEY_width = "width";
	private static final String KEY_height = "height";
	private static final String KEY_blocks = "blocks";
	private static final String KEY_blockType = "blockType";
	private static final String KEY_boundingBox = "boundingBox";
	private static final String KEY_vertices = "vertices";
	private static final String KEY_x = "x";
	private static final String KEY_y = "y";
	private static final String KEY_paragraphs = "paragraphs";
	private static final String KEY_words = "words";
	private static final String KEY_detectedBreak = "detectedBreak";
	private static final String KEY_type = "type";
	private static final String KEY_symbols = "symbols";
	private static final String KEY_text = "text";

	@Override
	public Page read(InputSource source) throws UnsupportedFormatVersionException {
		
        InputStream is = null;
        JSONObject json = null;
        try
        {
            is = getInputStream(source);
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(new InputStreamReader(is, Charset.forName("UTF-8")));
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        	return null;
        }
        finally
        {
            try { if(null != is) is.close(); } catch (Exception e) { e.printStackTrace(); }
        }
        
        if (!json.containsKey(KEY_responses))
        	throw new UnsupportedFormatVersionException("No 'responses' object found in JSON");
        
        Object obj = json.get(KEY_responses);
        if (obj instanceof JSONObject)
        	return handleRoot((JSONObject)obj);
        else if (obj instanceof JSONArray)
        	return handleRoot((JSONObject)((JSONArray)obj).get(0));
        else
        	throw new UnsupportedFormatVersionException("Unexpected JSON format");
	}
	
	private Page handleRoot(JSONObject json) throws UnsupportedFormatVersionException {
		
		if (json.containsKey(KEY_fullTextAnnotation)) {
			Object fullTextObj = json.get(KEY_fullTextAnnotation);
	        if (fullTextObj instanceof JSONObject) {
	        	if (((JSONObject)fullTextObj).containsKey(KEY_pages)) {
	        		Object obj = ((JSONObject)fullTextObj).get(KEY_pages);
	        		if (obj instanceof JSONArray)
	                	return handlePage((JSONObject)((JSONArray)obj).get(0));
	        		else
	        			throw new UnsupportedFormatVersionException("Expected array in 'pages' object");
	        	} else
	        		throw new UnsupportedFormatVersionException("Expected 'pages' object");
	        }
	        else
	        	throw new UnsupportedFormatVersionException("Expected 'fullTextAnnotation' to be an object");
		}
		throw new UnsupportedFormatVersionException("Expected 'fullTextAnnotation' object under responses");
	}

	private Page handlePage(JSONObject json) {
		Page page = new Page();
		
		//Page attributes
		//Width/height
		page.getLayout().setSize(getIntAttribute(json, KEY_width), getIntAttribute(json, KEY_height));

		//if (json.containsKey(KEY_property)) {
			//Object obj = json.get(KEY_property);
		//}
		
		//Blocks
		if (json.containsKey(KEY_blocks)) {
			Object obj = json.get(KEY_blocks);
			if (obj instanceof JSONArray) {
				JSONArray blocks = (JSONArray)obj;
				for (int i=0; i<blocks.size(); i++) {
					handleBlock(blocks.get(i), page.getLayout());
				}
			}
		}
		
		return page;
	}
	
	private void handleBlock(Object json, PageLayout pageLayout) {
		if (json instanceof JSONObject) {
			JSONObject block = (JSONObject)json;
			
			//Block type
			RegionType regionType = googleBlockTypeToRegionType(getStringAttribute(block, KEY_blockType));
			
			//For text regions, proceed with paragraphs
			if (regionType == RegionType.TextRegion) {
				if (block.containsKey(KEY_paragraphs)) {
					Object obj = block.get(KEY_paragraphs);
					if (obj instanceof JSONArray) {
						JSONArray paragraphs = (JSONArray)obj;
						for (int i=0; i<paragraphs.size(); i++) {
							handleParagraph(paragraphs.get(i), pageLayout);
						}
					}
				}
			}
			//Other regions
			else {
				Polygon coords = handleBoundingBox(block);
				if (coords != null) {
					Region region = pageLayout.createRegion(regionType);
					region.setCoords(coords);
				}				
			}
		}
	}
	
	private void handleParagraph(Object json, PageLayout pageLayout) {
		if (json instanceof JSONObject) {
			JSONObject paragraph = (JSONObject)json;
			Polygon coords = handleBoundingBox(paragraph);
			if (coords != null) {
				TextRegion region = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
				region.setCoords(coords);
				
				//Words
				if (paragraph.containsKey(KEY_words))
					handleWords(paragraph.get(KEY_words), region);
			}
		}
	}
	
	private void handleWords(Object json, TextRegion region) {
		if (json instanceof JSONArray) {
			JSONArray words = (JSONArray)json;
			TextLine currentTextLine = null;
			
			for (int i=0; i<words.size(); i++) {
				Object obj = words.get(i);
				if (obj instanceof JSONObject) {
					JSONObject wordJson = (JSONObject)obj;
					Polygon coords = handleBoundingBox(wordJson);
					
					if (coords != null) {
						if (currentTextLine == null)
							currentTextLine = region.createTextLine();
						
						Word word = currentTextLine.createWord();
						word.setCoords(coords);
						
						String breakType = handleSymbols(wordJson, word);
						
						//Check for whitespace
						if (BreakTypes.LineBreak.equals(breakType)
								|| BreakTypes.EndOfLine.equals(breakType)
								|| BreakTypes.EndOfLineHyphen.equals(breakType)) {
							finishTextLine(currentTextLine);
							currentTextLine = null;
						}
						
						String txt = word.composeText(false, false);
						if (BreakTypes.EndOfLineHyphen.equals(breakType))
							txt += "-";
						word.setText(txt);
					}
				}
			}
			if (currentTextLine != null)
				finishTextLine(currentTextLine);
		}
	}
	
	private String handleSymbols(JSONObject wordJson, Word word) {
		String breakType = "";
		
		if (wordJson.containsKey(KEY_symbols)) {
			Object obj = wordJson.get(KEY_symbols);
			if (obj instanceof JSONArray) {
				JSONArray symbols = (JSONArray)obj;
				
				for (int i=0; i<symbols.size(); i++) {
					obj = symbols.get(i);
					if (obj instanceof JSONObject) {
						JSONObject symbol = (JSONObject)obj;
						
						String symbolBreakType = getBreakType(symbol);
						if (!"".equals(symbolBreakType))
							breakType = symbolBreakType;
						
						Polygon coords = handleBoundingBox(symbol);
						if (coords != null) {
							Glyph glyph = word.createGlyph();
							glyph.setCoords(coords);
							
							//Text
							glyph.setText(getStringAttribute(symbol, KEY_text));
						}
					}
				}
			}
		}
		
		return breakType;
	}
	
	/** Looks for LINE_BREAK in property object */
	private String getBreakType(JSONObject json) {
		if (json.containsKey(KEY_property)) {
			Object obj = json.get(KEY_property);
			if (obj instanceof JSONObject) {
				JSONObject properties = (JSONObject)obj;
				if (properties.containsKey(KEY_detectedBreak)) {
					obj = properties.get(KEY_detectedBreak);
					if (obj instanceof JSONObject) {
						JSONObject detected = (JSONObject)obj;
						return getStringAttribute(detected, KEY_type);
					}
				}
			}
		}
		return "";
	}
	
	private void finishTextLine(TextLine line) {
		//Calculate text line bounding box from words
		int l = Integer.MAX_VALUE;
		int r = 0;
		int t = Integer.MAX_VALUE;
		int b = 0;
		for (int i=0; i<line.getTextObjectCount(); i++) {
			Word word = (Word)line.getTextObject(i);
			Rect box = word.getCoords().getBoundingBox();
			if (box.left < l)
				l = box.left; 
			if (box.right > r)
				r = box.right; 
			if (box.top < t)
				t = box.top; 
			if (box.bottom > b)
				b = box.bottom; 
		}
		Polygon coords = new Polygon();
		coords.addPoint(l, t);
		coords.addPoint(r, t);
		coords.addPoint(r, b);
		coords.addPoint(l, b);
		line.setCoords(coords);
	}
	
	private Polygon handleBoundingBox(JSONObject parentJson) {
		if (parentJson.containsKey(KEY_boundingBox)) {
			Object json = parentJson.get(KEY_boundingBox);
			if (json instanceof JSONObject) {
				JSONObject boundingBox = (JSONObject)json;
				if (boundingBox.containsKey(KEY_vertices)) {
					json = boundingBox.get(KEY_vertices);
					if (json instanceof JSONArray) {
						JSONArray vertices = (JSONArray)json;
						if (vertices.size() == 4) {
							Polygon ret = new Polygon();
							ret.addPoint(getPoint(vertices.get(0)));
							ret.addPoint(getPoint(vertices.get(1)));
							ret.addPoint(getPoint(vertices.get(2)));
							ret.addPoint(getPoint(vertices.get(3)));
							return ret;
						}
					}
				}
			}
		}
		return null;
	}
	
	/** Returns the value of the child object with given key (or zero) */
	private int getIntAttribute(JSONObject parentJson, String key) {
		if (parentJson.containsKey(key))
			return Integer.parseInt(parentJson.get(key).toString());
		return 0;
	}

	/** Returns the value of the child object with given key (or empty string) */
	private String getStringAttribute(JSONObject parentJson, String key) {
		if (parentJson.containsKey(key))
			return parentJson.get(key).toString();
		return "";
	}
	
	private Point getPoint(Object parentJson) {
		if (parentJson instanceof JSONObject)
			return new Point(	Math.max(0, getIntAttribute((JSONObject)parentJson, KEY_x)), 
								Math.max(0, getIntAttribute((JSONObject)parentJson, KEY_y)));
		return new Point();
	}
	
	private RegionType googleBlockTypeToRegionType(String blockType) {
		if ("PICTURE".equals(blockType))
			return RegionType.ImageRegion;
		if ("RULER".equals(blockType))
			return RegionType.SeparatorRegion;
		if ("BARCODE".equals(blockType))
			return RegionType.GraphicRegion;
		if ("TABLE".equals(blockType))
			return RegionType.TableRegion;
		if ("TEXT".equals(blockType))
			return RegionType.TextRegion;
		return RegionType.UnknownRegion;		
	}
	
	private static final class BreakTypes {
		public static final String LineBreak = "LINE_BREAK";
		public static final String EndOfLine = "EOL_SURE_SPACE";
		public static final String EndOfLineHyphen = "HYPHEN";
		//SPACE
		//SURE_SPACE
	}

}
