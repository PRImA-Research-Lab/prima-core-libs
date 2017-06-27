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
package org.primaresearch.dla.page.layout.physical;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Layers;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.physical.impl.AdvertRegion;
import org.primaresearch.dla.page.layout.physical.impl.ChartRegion;
import org.primaresearch.dla.page.layout.physical.impl.ChemRegion;
import org.primaresearch.dla.page.layout.physical.impl.GraphicRegion;
import org.primaresearch.dla.page.layout.physical.impl.ImageRegion;
import org.primaresearch.dla.page.layout.physical.impl.LineDrawingRegion;
import org.primaresearch.dla.page.layout.physical.impl.MathsRegion;
import org.primaresearch.dla.page.layout.physical.impl.MusicRegion;
import org.primaresearch.dla.page.layout.physical.impl.NoiseRegion;
import org.primaresearch.dla.page.layout.physical.impl.RegionImpl;
import org.primaresearch.dla.page.layout.physical.impl.SeparatorRegion;
import org.primaresearch.dla.page.layout.physical.impl.TableRegion;
import org.primaresearch.dla.page.layout.physical.role.RegionRole;
import org.primaresearch.dla.page.layout.physical.role.TableCellRole;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.graphemes.Grapheme;
import org.primaresearch.dla.page.layout.physical.text.graphemes.GraphemeElement;
import org.primaresearch.dla.page.layout.physical.text.graphemes.GraphemeGroup;
import org.primaresearch.dla.page.layout.physical.text.graphemes.NonPrintingCharacter;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Factory for creating page content objects.
 * 
 * @author Christian Clausner
 *
 */
public class ContentFactory {
	
	private IdRegister idRegister;
	private AttributeFactory attributeFactory;

	/**
	 * Constructor
	 * @param idRegister ID register to guarantee unique content object IDs
	 * @param attributeFactory Factory for object attributes
	 */
	public ContentFactory(IdRegister idRegister, AttributeFactory attributeFactory) {
		this.idRegister = idRegister;
		this.attributeFactory = attributeFactory;
	}

	/**
	 * Returns the attribute factory used for this content factory.
	 * @return Attribute factory implementation
	 */
	public AttributeFactory getAttributeFactory() {
		return attributeFactory;
	}

	private TextRegion createTextRegion() {
		Id id;
		try {
			id = idRegister.generateId("r");
			TextRegion ret = new TextRegionItem(this, id, new Polygon(), 
					createAttributes(RegionType.TextRegion),
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}

	private TextLine createTextLine() {
		Id id;
		try {
			id = idRegister.generateId("l");
			TextLine ret = new TextLineItem(this, id, new Polygon(), createAttributes(LowLevelTextType.TextLine), 
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}

	private Word createWord() {
		Id id;
		try {
			id = idRegister.generateId("w");
			Word ret = new WordItem(this, id, new Polygon(), createAttributes(LowLevelTextType.Word), 
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}
	
	private Glyph createGlyph() {
		Id id;
		try {
			id = idRegister.generateId("c");
			Glyph ret = new GlyphItem(this, idRegister, id, new Polygon(), createAttributes(LowLevelTextType.Glyph), 
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}

	private Grapheme createGrapheme() {
		Id id;
		try {
			id = idRegister.generateId("a");
			Grapheme ret = new GraphemeItem(this, idRegister, id, new Polygon(), createAttributes(LowLevelTextType.Grapheme), 
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}

	private GraphemeGroup createGraphemeGroup() {
		Id id;
		try {
			id = idRegister.generateId("b");
			GraphemeGroup ret = new GraphemeGroupItem(this, idRegister, id, createAttributes(LowLevelTextType.GraphemeGroup), 
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}

	private NonPrintingCharacter createNonPrintingCharacter() {
		Id id;
		try {
			id = idRegister.generateId("i");
			NonPrintingCharacter ret = new NonPrintingCharacterItem(this, idRegister, id, createAttributes(LowLevelTextType.NonPrintingCharacter), 
					null);
			return ret;
		} catch (InvalidIdException e) {
		} 
		return null;
	}

	/**
	 * Creates a new content object of the given type
	 * @param type Content type (e.g. text region)
	 * @return The new object or <code>null</code> if the type is not supported
	 */
	public ContentObject createContent(ContentType type) {
		
		//Text regions are special
		if (RegionType.TextRegion.equals(type))
			return createTextRegion();
		
		if (LowLevelTextType.TextLine.equals(type))
			return createTextLine();

		if (LowLevelTextType.Word.equals(type))
			return createWord();

		if (LowLevelTextType.Glyph.equals(type))
			return createGlyph();

		//All other regions
		if (type instanceof RegionType) {
			Id id = null;
			try {
				id = idRegister.generateId("r");
			} catch (InvalidIdException e) {
			} 
			Region ret = null;
			if (RegionType.ImageRegion.equals(type))
				ret = new ImageRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.GraphicRegion.equals(type))
				ret = new GraphicRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.LineDrawingRegion.equals(type))
				ret = new LineDrawingRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.ChartRegion.equals(type))
				ret = new ChartRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.SeparatorRegion.equals(type))
				ret = new SeparatorRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.MathsRegion.equals(type))
				ret = new MathsRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.AdvertRegion.equals(type))
				ret = new AdvertRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.ChemRegion.equals(type))
				ret = new ChemRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.MusicRegion.equals(type))
				ret = new MusicRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.TableRegion.equals(type))
				ret = new TableRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			//else if (type == RegionType.FrameRegion)
			//	ret = new FrameRegion(idRegister, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else if (RegionType.NoiseRegion.equals(type))
				ret = new NoiseRegion(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			else //Generic
				ret = new RegionItem(idRegister, this, (RegionType)type, id, new Polygon(), createAttributes(type), null);
			return ret;
		}
		return null;
	}
	
	/**
	 * Creates a new grapheme element object of the given type
	 * @param type Content type (e.g. LowLeveltextType.Grapheme)
	 * @return The new object or <code>null</code> if the type is not supported
	 */
	public GraphemeElement createGraphemeElement(ContentType type) {
		if (LowLevelTextType.Grapheme.equals(type))
			return createGrapheme();
		if (LowLevelTextType.GraphemeGroup.equals(type))
			return createGraphemeGroup();
		if (LowLevelTextType.NonPrintingCharacter.equals(type))
			return createNonPrintingCharacter();
		return null;
	}
	
	/**
	 * Creates a region role (e.g. table cell)
	 * @param type Type of role (e.g. table cell)
	 * @return The role object or <code>null</code> if the type is not supported
	 */
	public RegionRole createRegionRole(RoleType type) {
		if (RoleType.TableCellRole.equals(type))
			return new TableCellRole(attributeFactory);
		return null;
	}
	
	private VariableMap createAttributes(ContentType contentType) {
		return attributeFactory.createAttributes(contentType);
	}
	
	/**
	 * Creates a reading order object 
	 * @param layout Page layout the reading order is intended for
	 * @return The reading order object
	 */
	public ReadingOrder createReadingOrder(PageLayout layout) {
		return new ReadingOrder(layout, idRegister, this);
	}

	/**
	 * Creates a layer container
	 * @param layout Page layout the layer container is intended for
	 * @return Layers object
	 */
	public Layers createLayers(PageLayout layout) {
		return new Layers(layout, idRegister, this);
	}
	
	/**
	 * Registers the specified ID
	 * @param id ID content
	 * @return ID object
	 * @throws InvalidIdException ID invalid
	 */
	public Id registerNewId(String id) throws InvalidIdException {
		return idRegister.registerId(id);
	}
	
	
	//Extend class to get access to protected constructor.
	private static class TextRegionItem extends TextRegion {
		protected TextRegionItem(ContentFactory contentFactory, Id id, Polygon coords, 
								VariableMap attributes, 
								RegionContainer parentRegion) {
			super(contentFactory, contentFactory.idRegister, id, coords, attributes, parentRegion);
		}
	}

	//Extend class to get access to protected constructor.
	private static class TextLineItem extends TextLine {
		protected TextLineItem(ContentFactory contentFactory, Id id, Polygon coords, VariableMap attributes,
								LowLevelTextContainer parentRegion) {
			super(contentFactory, contentFactory.idRegister, id, coords, attributes, parentRegion);
		}
	}
	
	//Extend class to get access to protected constructor.
	private static class WordItem extends Word {
		protected WordItem(	ContentFactory contentFactory, Id id, Polygon coords, 
							VariableMap attributes, 
							LowLevelTextContainer parentRegion) {
			super(contentFactory, contentFactory.idRegister, id, coords, attributes, parentRegion);
		}
	}
	
	//Extend class to get access to protected constructor.
	private static class GlyphItem extends Glyph {
		protected GlyphItem(ContentFactory contentFactory, IdRegister idRegister, Id id, Polygon coords, 
							VariableMap attributes, 
							LowLevelTextContainer parentRegion) {
			super(contentFactory, idRegister, id, coords, attributes, parentRegion);
		}
	}

	//Extend class to get access to protected constructor.
	private static class GraphemeItem extends Grapheme {
		protected GraphemeItem(ContentFactory contentFactory, IdRegister idRegister, Id id, Polygon coords, 
							VariableMap attributes, 
							Glyph parent) {
			super(idRegister, id, coords, attributes, parent, contentFactory.attributeFactory);
		}
	}

	//Extend class to get access to protected constructor.
	private static class GraphemeGroupItem extends GraphemeGroup {
		protected GraphemeGroupItem(ContentFactory contentFactory, IdRegister idRegister, Id id, 
							VariableMap attributes, 
							Glyph parent) {
			super(idRegister, id, attributes, parent, contentFactory.attributeFactory);
		}
	}

	//Extend class to get access to protected constructor.
	private static class NonPrintingCharacterItem extends NonPrintingCharacter {
		protected NonPrintingCharacterItem(ContentFactory contentFactory, IdRegister idRegister, Id id, 
							VariableMap attributes, 
							Glyph parent) {
			super(idRegister, id, attributes, parent, contentFactory.attributeFactory);
		}
	}

	//Extend class to get access to protected constructor.
	private static class RegionItem extends RegionImpl {
		protected RegionItem(IdRegister idRegister, ContentFactory contentFactory, RegionType type, Id id, Polygon coords, VariableMap attributes,
				RegionContainer parentRegion) {
			super(idRegister, contentFactory, type, id, coords, attributes, parentRegion);
		}
	}
	
	/**
	 * Sets the attribute factory that is used for creating attributes for new objects
	 */
	public void setAttributeFactory(AttributeFactory factory) {
		this.attributeFactory = factory;
	}

	/**
	 * Sets the ID register that is used for creating new objects
	 */
	public IdRegister getIdRegister() {
		return idRegister;
	}
	

	
}
