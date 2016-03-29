/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.extension.html;

import org.openmrs.module.web.extension.BoxExt;

/**
 * @author Administrator
 *
 */
public class PatientExitOverviewTabExt extends BoxExt {

	/* (non-Javadoc)
	 * @see org.openmrs.module.web.extension.BoxExt#getContent()
	 */
	@Override
	public String getContent() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.web.extension.BoxExt#getPortletUrl()
	 */
	@Override
	public String getPortletUrl() {
		return "patientExitPortlet";
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.web.extension.BoxExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Patient Care Exit";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return super.getRequiredPrivilege();
	}

}
