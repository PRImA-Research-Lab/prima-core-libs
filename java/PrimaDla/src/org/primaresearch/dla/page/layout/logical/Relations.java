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
package org.primaresearch.dla.page.layout.logical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.primaresearch.ident.Id;

/**
 * Container class for relations between content objects.
 * 
 * @author Christian Clausner
 *
 */
public class Relations {

	private Map<Id,Map<Id,ContentObjectRelation>> relations = new HashMap<Id,Map<Id,ContentObjectRelation>>();

	/**
	 * Checks if there are relations in this container
	 * @return <code>true</code> if empty, <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return relations.isEmpty();
	}

	/**
	 * Adds a relation to this container.
	 * @param relation Relation object to add
	 */
	public void addRelation(ContentObjectRelation relation) {
		Map<Id,ContentObjectRelation> targetMap = relations.get(relation.getObject1().getId());
		if (targetMap == null) {
			targetMap = new HashMap<Id,ContentObjectRelation>();
			relations.put(relation.getObject1().getId(), targetMap);
		}
		targetMap.put(relation.getObject2().getId(), relation);
	}
	
	/**
	 * Returns the relation for the objects with id1 and id2 or 'null', if no such relation exists.
	 */
	public ContentObjectRelation getRelation(Id id1, Id id2) {
		Map<Id,ContentObjectRelation> targetMap = relations.get(id1);
		if (targetMap != null) {
			return targetMap.get(id2);
		} else {
			return getRelation(id2, id1);
		}
	}
	
	/**
	 * Exports a set of all relations. 
	 */
	public Set<ContentObjectRelation> exportRelations() {
		Set<ContentObjectRelation> rels = new HashSet<ContentObjectRelation>();
		for (Iterator<Map<Id,ContentObjectRelation>> it = relations.values().iterator(); it.hasNext(); ) {
			Map<Id,ContentObjectRelation> targetMap = it.next();
			for (Iterator<ContentObjectRelation> it2 = targetMap.values().iterator(); it2.hasNext(); ) {
				rels.add(it2.next());
			}
		}
		return rels;
	}
}
