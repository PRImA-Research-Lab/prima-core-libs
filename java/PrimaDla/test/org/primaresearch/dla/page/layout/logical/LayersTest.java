package org.primaresearch.dla.page.layout.logical;

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.PageLayout;

public class LayersTest {

	@Test
	public void test() {
		Page page = new Page();
		PageLayout layout = page.getLayout();
		
		//initialise layers
		Layers layers = layout.createLayers();
		assertNotNull(layers);

		//Add layers to front
		Layer layerA = layers.createLayer();
		Layer layerB = layers.createLayer();
		Layer layerC = layers.createLayer();
		
		assertTrue("Three new layers", layers.getSize() == 3);
		
		//Add to back
		Layer layerD = layers.createLayer(false);
		assertTrue("Fourth layer at back", layers.getLayer(0) == layerD);
		
		//Check order
		assertTrue("backmost layer", layers.getLayer(0) == layerD);
		assertTrue("getBackLayer", layers.getBackLayer() == layerD);
		assertTrue("layer 2 from back", layers.getLayer(1) == layerA);
		assertTrue("layer 3 from back", layers.getLayer(2) == layerB);
		assertTrue("frontmost layer", layers.getLayer(3) == layerC);
		assertTrue("getFrontLayer", layers.getFrontLayer() == layerC);
		
		
	}

}
