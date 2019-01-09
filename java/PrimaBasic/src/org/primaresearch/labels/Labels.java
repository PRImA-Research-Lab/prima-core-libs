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
package org.primaresearch.labels;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Semantic labels
 * 
 * @author Christian Clausner
 *
 */
public class Labels implements Serializable {

	private static final long serialVersionUID = 1L;
	private	Map<String, LabelGroup> labelGroups;

	public Labels() {
	}
	
	public void addLabel(Label  label) {
		if (label == null)
			return;

		//Get group
		LabelGroup group = null;
		if (labelGroups.containsKey(label.getExternalModel()))
			group = labelGroups.get(label.getExternalModel());
		else
		{
			group = new LabelGroup(label.getExternalModel());
			labelGroups.put(label.getExternalModel(), group);
		}

		//Add label to group
		group.addLabel(label);
	}

	public void addGroup(LabelGroup group) {
		if (group == null)
			return;
		
		if (labelGroups == null)
			labelGroups = new HashMap<String, LabelGroup>();
		
		labelGroups.put(group.getExternalModel(), group);
	}

	public int getGroupCount() {
		return labelGroups != null ? labelGroups.size() : 0;
	}

	public Map<String, LabelGroup> getGroups() {
		return labelGroups;
	}


}
