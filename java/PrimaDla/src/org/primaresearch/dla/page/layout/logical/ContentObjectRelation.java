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
package org.primaresearch.dla.page.layout.logical;

import org.primaresearch.dla.page.layout.physical.ContentObject;

/**
 * Represents a relation between two page content objects (e.g. parent-child relation).
 * 
 * @author Christian Clausner
 *
 */
public class ContentObjectRelation {

	private ContentObject object1;
	private ContentObject object2;
	private RelationType relationType;
	private String customField;
	private String comments;

	/**
	 * Constructor
	 * 
	 * @param object1 Page content object one
	 * @param object2 Page content object two
	 * @param relation Relation between object one and object two
	 */
	public ContentObjectRelation(ContentObject object1, ContentObject object2, RelationType relation) {
		this.object1 = object1;
		this.object2 = object2;
		this.relationType = relation;
	}
	
	public ContentObject getObject1() {
		return object1;
	}

	public ContentObject getObject2() {
		return object2;
	}

	public RelationType getRelationType() {
		return relationType;
	}
	
	/**
	 * Returns custom content
	 */
	public String getCustomField() {
		return customField;
	}

	/**
	 * Sets custom content
	 */
	public void setCustomField(String customField) {
		this.customField = customField;
	}

	/**
	 * Returns comments
	 * @return Comments text
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets comments
	 * @param comments Comments text
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}




	/**
	 * Relation type for page content objects.
	 * 
	 * @author Christian Clausner
	 *
	 */
	public static class RelationType {
		
		/**
		 * Parent-child relation (e.g. word-glyph)
		 */
		public static final RelationType ParentChildRelation = new RelationType("ParentChildRelation");
		
		/**
		 * Weak relation (e.g. image-caption)
		 */
		public static final RelationType Link = new RelationType("link");
		
		/**
		 * Strong relation (e.g. drop capital - following text region or two parts of a word that was been wrapped)
		 */
		public static final RelationType Join = new RelationType("join");
		
		private String id;
		private RelationType(String id) {
			this.id = id;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof RelationType)
				return id.equals(((RelationType)other).id);
			return false;
		}
		
		public String toString() {
			return id;
		}
	}
}
