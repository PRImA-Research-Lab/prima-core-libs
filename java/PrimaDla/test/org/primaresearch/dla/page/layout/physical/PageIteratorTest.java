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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.impl.LowLevelTextObjectIterator;
import org.primaresearch.dla.page.layout.physical.impl.RegionIterator;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;

public class PageIteratorTest {

	@Test
	public void testRegionIterator() {
		//Create a few regions
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		Region r1 = pageLayout.createRegion(RegionType.TextRegion);
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		Region r3 = pageLayout.createRegion(RegionType.TextRegion);
		Region r4 = pageLayout.createRegion(RegionType.TextRegion);
		Region r5 = pageLayout.createRegion(RegionType.MusicRegion);
		
		//Use iterator factory
		ContentIterator it = pageLayout.iterator(null);
		
		assertTrue("Iterator created", it != null && it instanceof RegionIterator);
		
		assertTrue("Region 1", it.hasNext() && it.next() == r1);
		assertTrue("Region 2", it.hasNext() && it.next() == r2);
		assertTrue("Region 3", it.hasNext() && it.next() == r3);
		assertTrue("Region 4", it.hasNext() && it.next() == r4);
		assertTrue("Region 5", it.hasNext() && it.next() == r5);
		assertFalse("End", it.hasNext());
		
		//Region type
		it = new RegionIterator(pageLayout, RegionType.TextRegion, null);

		assertTrue("Region iterator with type filter created", it != null && it instanceof RegionIterator);
		
		assertTrue("Text region 1", it.hasNext() && it.next() == r1);
		assertTrue("Text region 3", it.hasNext() && it.next() == r3);
		assertTrue("Text region 4", it.hasNext() && it.next() == r4);
		assertFalse("End iterator with type filter", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new RegionIterator(pageLayout, null, layer);
		
		assertTrue("Region iterator with layer filter created", it != null && it instanceof RegionIterator);

		assertTrue("Region 2 (in layer)", it.hasNext() && it.next() == r2);
		assertTrue("Region 4 (in layer)", it.hasNext() && it.next() == r4);
		assertTrue("Region 5 (in layer)", it.hasNext() && it.next() == r5);
		assertFalse("End iterator with layer filter", it.hasNext());
	
		//Layer and region type
		layer.removeRegionRef(r5.getId().toString());
		
		it = new RegionIterator(pageLayout, RegionType.MusicRegion, layer);
	
		assertTrue("Region iterator with layer and type filter created", it != null && it instanceof RegionIterator);

		assertFalse("End iterator with layer and type filter", it.hasNext());
	}

	@Test
	public void testRegionIteratorWithNesting() {
		//Create a few regions
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		Region r1 = pageLayout.createRegion(RegionType.TextRegion);
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		Region r3 = pageLayout.createRegion(RegionType.TextRegion, null, r2);
		Region r4 = pageLayout.createRegion(RegionType.TextRegion, null, r2);
		Region r5 = pageLayout.createRegion(RegionType.MusicRegion);
		Region r6 = pageLayout.createRegion(RegionType.TextRegion, null, r5);

		//Use iterator factory
		ContentIterator it = pageLayout.iterator(null);
		
		assertTrue("Iterator created", it != null && it instanceof RegionIterator);
		
		assertTrue("Region 1", it.hasNext() && it.next() == r1);
		assertTrue("Region 2", it.hasNext() && it.next() == r2);
		assertTrue("Region 3", it.hasNext() && it.next() == r3);
		assertTrue("Region 4", it.hasNext() && it.next() == r4);
		assertTrue("Region 5", it.hasNext() && it.next() == r5);
		assertTrue("Region 6", it.hasNext() && it.next() == r6);
		assertFalse("End", it.hasNext());
		
		//Region type
		it = new RegionIterator(pageLayout, RegionType.TextRegion, null);

		assertTrue("Region iterator with type filter created", it != null && it instanceof RegionIterator);
		
		assertTrue("Text region 1", it.hasNext() && it.next() == r1);
		assertTrue("Text region 3", it.hasNext() && it.next() == r3);
		assertTrue("Text region 4", it.hasNext() && it.next() == r4);
		assertTrue("Text region 6", it.hasNext() && it.next() == r6);
		assertFalse("End iterator with type filter", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new RegionIterator(pageLayout, null, layer);
		
		assertTrue("Region iterator with layer filter created", it != null && it instanceof RegionIterator);

		assertTrue("Region 2 (in layer)", it.hasNext() && it.next() == r2);
		assertTrue("Region 4 (in layer)", it.hasNext() && it.next() == r4);
		assertTrue("Region 5 (in layer)", it.hasNext() && it.next() == r5);
		assertFalse("End iterator with layer filter", it.hasNext());

		//Layer and region type
		layer.removeRegionRef(r5.getId().toString());
		
		it = new RegionIterator(pageLayout, RegionType.MusicRegion, layer);
	
		assertTrue("Region iterator with layer and type filter created", it != null && it instanceof RegionIterator);

		assertFalse("End iterator with layer and type filter", it.hasNext());
	}

	@SuppressWarnings("unused")
	@Test
	public void testTextLineIterator() {
		//Create a few regions with text lines
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		TextRegion r1 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t11 = r1.createTextLine();
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		TextRegion r3 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextRegion r4 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t41 = r4.createTextLine();
		TextLine t42 = r4.createTextLine();
		TextRegion r5 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t51 = r5.createTextLine();
		
		//Use iterator factory
		ContentIterator it = pageLayout.iterator(LowLevelTextType.TextLine);
		
		assertTrue("Iterator created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Line 11", it.hasNext() && it.next() == t11);
		assertTrue("Line 41", it.hasNext() && it.next() == t41);
		assertTrue("Line 42", it.hasNext() && it.next() == t42);
		assertTrue("Line 51", it.hasNext() && it.next() == t51);
		assertFalse("End", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.TextLine, layer);
		
		assertTrue("Text line iterator with layer filter created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Line 41 (in layer)", it.hasNext() && it.next() == t41);
		assertTrue("Line 42 (in layer)", it.hasNext() && it.next() == t42);
		assertTrue("Line 51 (in layer)", it.hasNext() && it.next() == t51);
		assertFalse("End it with layer filter", it.hasNext());

		//Layer (no matching region)
		layer.removeRegionRef(r4.getId().toString());
		layer.removeRegionRef(r5.getId().toString());
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.TextLine, layer);
		assertTrue("Text line iterator with layer filter created (no match)", it != null && it instanceof LowLevelTextObjectIterator);
		assertFalse("End it with layer filter (no match)", it.hasNext());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testTextLineIteratorWithNesting() {
		//Create a few regions with text lines: tr1, cr2 (tr3,tr4), tr5 (tr6)
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		TextRegion r1 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t11 = r1.createTextLine();
		
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		TextRegion r3 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion, null, r2);
		TextRegion r4 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion, null, r2);
		TextLine t41 = r4.createTextLine();
		TextLine t42 = r4.createTextLine();
		
		TextRegion r5 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextRegion r6 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion, null, r5);
		TextLine t61 = r6.createTextLine();
		
		//Use iterator factory
		ContentIterator it = pageLayout.iterator(LowLevelTextType.TextLine);
		
		assertTrue("Iterator created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Line 11", it.hasNext() && it.next() == t11);
		assertTrue("Line 41", it.hasNext() && it.next() == t41);
		assertTrue("Line 42", it.hasNext() && it.next() == t42);
		assertTrue("Line 51", it.hasNext() && it.next() == t61);
		assertFalse("End", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.TextLine, layer);
		
		assertTrue("Text line iterator with layer filter created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Line 41 (in layer)", it.hasNext() && it.next() == t41);
		assertTrue("Line 42 (in layer)", it.hasNext() && it.next() == t42);
		assertFalse("End it with layer filter", it.hasNext());

		//Layer (no matching region)
		layer.removeRegionRef(r4.getId().toString());
		layer.removeRegionRef(r5.getId().toString());
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.TextLine, layer);
		assertTrue("Text line iterator with layer filter created (no match)", it != null && it instanceof LowLevelTextObjectIterator);
		assertFalse("End it with layer filter (no match)", it.hasNext());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testWordIterator() {
		//Create a few regions with text lines and words
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		TextRegion r1 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t11 = r1.createTextLine();
		Word w111 = t11.createWord();
		
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		
		TextRegion r3 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		
		TextRegion r4 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t41 = r4.createTextLine();
		Word w411 = t41.createWord();
		Word w412 = t41.createWord();
		TextLine t42 = r4.createTextLine();
		
		TextRegion r5 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		
		TextRegion r6 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t61 = r6.createTextLine();
		Word w611 = t61.createWord();
		
		//Use iterator factory
		ContentIterator it = pageLayout.iterator(LowLevelTextType.Word);
		
		assertTrue("Iterator created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Word 111", it.hasNext() && it.next() == w111);
		assertTrue("Word 411", it.hasNext() && it.next() == w411);
		assertTrue("Word 412", it.hasNext() && it.next() == w412);
		assertTrue("Word 611", it.hasNext() && it.next() == w611);
		assertFalse("End", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Word, layer);
		
		assertTrue("Word iterator with layer filter created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Word 411 (in layer)", it.hasNext() && it.next() == w411);
		assertTrue("Word 412 (in layer)", it.hasNext() && it.next() == w412);
		assertFalse("End it with layer filter", it.hasNext());
		
		//Layer (no matching region)
		layer.removeRegionRef(r4.getId().toString());
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Word, layer);
		assertTrue("Word iterator with layer filter created (no match)", it != null && it instanceof LowLevelTextObjectIterator);
		assertFalse("End it with layer filter (no match)", it.hasNext());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testWordIteratorWithNesting() {
		//Create a few regions with text lines and words: tr1, cr2 (tr3,tr4), tr5 (tr6)
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		TextRegion r1 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t11 = r1.createTextLine();
		Word w111 = t11.createWord();
		
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		
		TextRegion r3 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion, null, r2);
		
		TextRegion r4 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion, null, r2);
		TextLine t41 = r4.createTextLine();
		Word w411 = t41.createWord();
		Word w412 = t41.createWord();
		TextLine t42 = r4.createTextLine();
		
		TextRegion r5 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		
		TextRegion r6 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion, null, r5);
		TextLine t61 = r6.createTextLine();
		Word w611 = t61.createWord();
		
		//Use iterator factory
		ContentIterator it = pageLayout.iterator(LowLevelTextType.Word);
		
		assertTrue("Iterator created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Word 111", it.hasNext() && it.next() == w111);
		assertTrue("Word 411", it.hasNext() && it.next() == w411);
		assertTrue("Word 412", it.hasNext() && it.next() == w412);
		assertTrue("Word 611", it.hasNext() && it.next() == w611);
		assertFalse("End", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Word, layer);
		
		assertTrue("Word iterator with layer filter created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Word 411 (in layer)", it.hasNext() && it.next() == w411);
		assertTrue("Word 412 (in layer)", it.hasNext() && it.next() == w412);
		assertFalse("End it with layer filter", it.hasNext());
		
		//Layer (no matching region)
		layer.removeRegionRef(r4.getId().toString());
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Word, layer);
		assertTrue("Word iterator with layer filter created (no match)", it != null && it instanceof LowLevelTextObjectIterator);
		assertFalse("End it with layer filter (no match)", it.hasNext());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testGlyphIterator() {
		//Create a few regions with text lines, words and glyphs
		Page page = new Page();
		PageLayout pageLayout = page.getLayout();
		TextRegion r1 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t11 = r1.createTextLine();
		Word w111 = t11.createWord();
		Glyph g1111 = w111.createGlyph();
		
		Region r2 = pageLayout.createRegion(RegionType.ChartRegion);
		
		TextRegion r3 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		
		TextRegion r4 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t41 = r4.createTextLine();
		Word w411 = t41.createWord();
		Glyph g4111 = w411.createGlyph(); 
		Glyph g4112 = w411.createGlyph(); 
		Word w412 = t41.createWord();
		TextLine t42 = r4.createTextLine();
		
		TextRegion r5 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		
		TextRegion r6 = (TextRegion)pageLayout.createRegion(RegionType.TextRegion);
		TextLine t61 = r6.createTextLine();
		Word w611 = t61.createWord();
		Glyph g6111 = w611.createGlyph(); 
		
		//Use iterator factory
		ContentIterator it = pageLayout.iterator(LowLevelTextType.Glyph);
		
		assertTrue("Iterator created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Glyph 1111", it.hasNext() && it.next() == g1111);
		assertTrue("Glyph 4111", it.hasNext() && it.next() == g4111);
		assertTrue("Glyph 4112", it.hasNext() && it.next() == g4112);
		assertTrue("Glyph 6111", it.hasNext() && it.next() == g6111);
		assertFalse("End", it.hasNext());
		
		//Layer
		pageLayout.createLayers();
		Layer layer = pageLayout.getLayers().createLayer();
		layer.addRegionRef(r2.getId().toString());
		layer.addRegionRef(r4.getId().toString());
		layer.addRegionRef(r5.getId().toString());
		
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Glyph, layer);
		
		assertTrue("Glyph iterator with layer filter created", it != null && it instanceof LowLevelTextObjectIterator);
		
		assertTrue("Glyph 4111 (in layer)", it.hasNext() && it.next() == g4111);
		assertTrue("Glyph 4112 (in layer)", it.hasNext() && it.next() == g4112);
		assertFalse("End it with layer filter", it.hasNext());
		
		//Layer (no matching region)
		layer.removeRegionRef(r4.getId().toString());
		it = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Glyph, layer);
		assertTrue("Glyph iterator with layer filter created (no match)", it != null && it instanceof LowLevelTextObjectIterator);
		assertFalse("End it with layer filter (no match)", it.hasNext());
	}
}
