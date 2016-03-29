/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.tracpatienttransfer.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * This class defines the links that will appear on the administration page
 * under the "basicmodule.title" heading. This extension is enabled by defining
 * (uncommenting) it in the /metadata/config.xml file.
 */
public class AdminList extends AdministrationSectionExt {

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
	 */
	public String getTitle() {
		return "tracpatienttransfer.managementOfPatientExitFromCare";
	}

	@Override
	public String getRequiredPrivilege() {
		return "View Patient exited from care";
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 */
	public Map<String, String> getLinks() {

		Map<String, String> map = new HashMap<String, String>();

		map.put("module/tracpatienttransfer/overview.form", "tracpatienttransfer.overviewOnExit");
		if (Context.getAuthenticatedUser().hasPrivilege("Manage search/listing on Patient Exited from care")) {
			map.put("module/tracpatienttransfer/patientExitedFromCare.list?page=1", "tracpatienttransfer.manage.list");
			map.put("module/tracpatienttransfer/advancedSearch.form", "tracpatienttransfer.search.advanced");
		}
		map.put("module/tracpatienttransfer/patientsResumedCare.list?page=1", "tracpatienttransfer.patientsResumedCare");

		return map;
	}

}
