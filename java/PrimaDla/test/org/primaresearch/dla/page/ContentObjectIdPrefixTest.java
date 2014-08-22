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
package org.primaresearch.dla.page;

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;

public class ContentObjectIdPrefixTest {

	@Test
	public void testIdPrefix() {
		Page page = new Page();
		PageLayout layout = page.getLayout();
		
		Region r1 = layout.createRegion(RegionType.TextRegion);
		assertTrue("Region 1", r1.getId().toString().startsWith("r"));
		
		Region r2 = layout.createRegion(RegionType.GraphicRegion);
		assertTrue("Region 2", r2.getId().toString().startsWith("r"));
		
		TextLine t1 = ((TextRegion)r1).createTextLine();
		assertTrue("Text line 1", t1.getId().toString().startsWith("l"));

		Word w1 = t1.createWord();
		assertTrue("Word 1", w1.getId().toString().startsWith("w"));

		Glyph g1 = w1.createGlyph();
		assertTrue("Glyph 1", g1.getId().toString().startsWith("c"));
		
		layout.createReadingOrder();
		assertTrue("Reading order root", layout.getReadingOrder().getRoot().getId().toString().startsWith("g"));
		
		Group group = null;
		try {
			group = layout.getReadingOrder().getRoot().createChildGroup();
			assertTrue("Reading order child group", group.getId().toString().startsWith("g"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		layout.createLayers();
		Layer layer = layout.getLayers().createLayer();
		assertTrue("Layer", layer.getId().toString().startsWith("lay"));
	}

}
