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
package org.primaresearch.dla.page.layout.logical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.AttributeContainer;
import org.primaresearch.dla.page.layout.physical.ContentFactory;
import org.primaresearch.dla.page.layout.physical.shared.LogicalType;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.ident.Identifiable;
import org.primaresearch.labels.HasLabels;
import org.primaresearch.labels.Labels;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * A logical group within a page layout (e.g. a reading order group). Groups can also be GroupMemebers.
 * 
 * @author Christian Clausner
 */
public class Group implements GroupMember, Identifiable, AttributeContainer, HasLabels {

	private PageLayout layout;
	private IdRegister idRegister;
	private ContentFactory contentFactory;
	private boolean canHaveGroupsAsChildren;
	private Group parentGroup;
	private Id id;
	private Id regionRef = null;
	private boolean ordered;
	private List<GroupMember> members = new ArrayList<GroupMember>();
	
	private VariableMap attributes;
	private VariableMap userDefinedAttributes = null;
	
	transient private Labels labels;

	/**
	 * Constructor
	 * @param layout Page layout the group belongs to
	 * @param idRegister ID register (needed when creating child groups)
	 * @param contentFactory Factory needed when creating child groups
	 * @param id Group ID
	 * @param parentGroup Parent group (<code>null</code> for root)
	 * @param canHaveGroupsAsChildren Set to <code>true</code> to allow child groups
	 */
	Group(PageLayout layout, IdRegister idRegister, ContentFactory contentFactory, Id id, Group parentGroup, boolean canHaveGroupsAsChildren) {
		this.layout = layout;
		this.idRegister = idRegister;
		this.contentFactory = contentFactory;
		this.id = id;
		this.parentGroup = parentGroup;
		this.canHaveGroupsAsChildren = canHaveGroupsAsChildren;
		try {
			this.attributes = contentFactory.getAttributeFactory().createAttributes(LogicalType.ReadingOrderGroup);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Returns the parent of this group or <code>null</code> if it is a root group 
	 */
	public Group getParent() {
		return parentGroup;
	}

	@Override
	public Id getId() {
		return id;
	}
	
	/**
	 * Returns the ID of an optional referenced region. Intended to model parent regions containing nested regions.
	 * The linked parent region doubles as reading order group. Only nested regions should be allowed as group members.
	 */
	public Id getRegionRef() {
		return regionRef;
	}

	/**
	 * Sets the ID of an optional referenced region. Intended to model parent regions containing nested regions.
	 * The linked parent region doubles as reading order group. Only nested regions should be allowed as group members.
	 */
	public void setRegionRef(Id regionRef) {
		this.regionRef = regionRef;
	}

	/**
	 * Sets the ID of an optional referenced region. Intended to model parent regions containing nested regions.
	 * The linked parent region doubles as reading order group. Only nested regions should be allowed as group members.
	 */
	public void setRegionRef(String regionRef) {
		try {
			this.regionRef = contentFactory.getIdRegister().getId(regionRef);
		} catch (InvalidIdException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the caption (display name)
	 */
	public String getCaption() {
		if (getAttributes().get(DefaultXmlNames.ATTR_caption) != null && getAttributes().get(DefaultXmlNames.ATTR_caption).getValue() != null)
			return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_caption).getValue()).val;
		return null;
	}

	/**
	 * Sets the caption (display name)
	 */
	public void setCaption(String caption) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_caption).setValue(VariableValue.createValueObject(caption));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the 'ordered' state of this group
	 * @return <code>true</code> if an ordered group; <code>false</code> if an unordered group
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * Sets the 'ordered' state of this group
	 * @param ordered Set to <code>true</code> for an ordered group or <code>false</code> for an unordered group
	 */
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}
	
	/**
	 * Returns the size of this group
	 * @return Number of members
	 */
	public int getSize() {
		return members.size();
	}
	
	/**
	 * Returns the group member at the given position
	 * @param index Position
	 * @return Group member object
	 */
	public GroupMember getMember(int index) {
		return members.get(index);
	}
	
	/**
	 * Creates a group and adds it as child to this group
	 * @return The new group
	 * @throws Exception The group is not allowed to have children
	 */
	public Group createChildGroup() throws Exception {
		if (!canHaveGroupsAsChildren)
			throw new Exception("");
		Group group = new Group(layout, idRegister, contentFactory, idRegister.generateId("g"), this, canHaveGroupsAsChildren); 
		members.add(group);
		return group;
	}
	
	/**
	 * Adds a reference to a region as group member
	 * @param id Region ID
	 */
	public void addRegionRef(String id) {
		try {
			members.add(new RegionRef(this, contentFactory.getIdRegister().getId(id)));
		} catch (InvalidIdException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes a reference to a region
	 * @param id Region ID
	 */
	public void removeRegionRef(String id) {
		if (members != null) {
			GroupMember toRemove = null;
			for (Iterator<GroupMember> it = members.iterator(); it.hasNext(); ) {
				GroupMember member = it.next();
				if (member instanceof RegionRef) {
					if (((RegionRef)member).getRegionId().equals(id)) {
						toRemove = member;
						break;
					}
				} 
			}
			
			if (toRemove != null)
				members.remove(toRemove);
		}
	}
	
	/**
	 * Recursively checks if this group or a child group contains a region reference with the given ID.
	 *  
	 * @param regionId ID of referenced region
	 * @return True, if a reference has been found; false otherwise. 
	 */
	public boolean containsRegionRef(Id regionId) {
		if (members != null) {
			for (Iterator<GroupMember> it = members.iterator(); it.hasNext(); ) {
				GroupMember member = it.next();
				if (member instanceof RegionRef) {
					if (((RegionRef)member).getRegionId().equals(regionId))
						return true;
				} else { //if (member instanceof Group) 
					if (((Group)member).containsRegionRef(regionId)) //Recursion
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Adds the given group member
	 */
	public void add(GroupMember member) {
		members.add(member);
	}

	@Override
	public IdRegister getIdRegister() {
		return idRegister;
	}

	@Override
	public void setId(String id) throws InvalidIdException {
		this.id = idRegister.registerId(id, this.id);
	}
	
	@Override
	public void setId(Id id) throws InvalidIdException {
		idRegister.registerId(id, this.id);
		this.id = id;
	}

	@Override
	public void moveTo(Group newParent) {
		parentGroup.remove(this);
		newParent.add(this);
	}
	
	/**
	 * Removes the specified member from this group.
	 * This method does not unregister the ID of a group.
	 * Intended for internal use (e.g. moveTo() of GroupMember).
	 * 
	 * @return true, if the member has been found and removed, false otherwise
	 */
	boolean remove(GroupMember member) {
		for (int i=0; i<members.size(); i++) {
			if (members.get(i) == member) {
				members.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Deletes a group member.
	 * @return true, if the member has been found and removed, false otherwise
	 */
	public boolean delete(GroupMember member) {
		boolean removed = remove(member);
		
		//If it is a group, we have to unregister it's ID
		if (removed && member instanceof Group)
			idRegister.unregisterId(((Group)member).getId());
		return removed;
	}

	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

	/**
	 * User-defined attributes (text, int, decimal or boolean)
	 * @return Variable map or <code>null</code>
	 */
	public VariableMap getUserDefinedAttributes() {
		return userDefinedAttributes;
	}
	
	/**
	 *  User-defined attributes (text, int, decimal or boolean)
	 * @param attrs Variable map
	 */
	public void setUserDefinedAttributes(VariableMap attrs) {
		userDefinedAttributes = attrs;
	}


	@Override
	public Labels getLabels() {
		return labels;
	}

	@Override
	public void setLabels(Labels labels) {
		this.labels = labels;		
	}
}
