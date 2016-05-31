/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.extension.html;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.web.extension.LinkExt;

/**
 * @author Yves GAKUBA
 * 
 */
public class TracPatientTransferExt extends LinkExt {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.web.extension.LinkExt#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.web.extension.LinkExt#getLabel()
	 */
	@Override
	public String getLabel() {
		String patientId = this.getParameterMap().get("patientId");

		String msg = "";
		try {
			PatientTransferService pts = Context
					.getService(PatientTransferService.class);
			if (pts.isThePatientExitedFromCare(Integer.valueOf(patientId))){
				if (Context.getAuthenticatedUser().hasPrivilege("Resume care"))
				msg = MohTracUtil.getMessage("tracpatienttransfer.resumeCare",
						null);
			}
			else{
				if (Context.getAuthenticatedUser().hasPrivilege("Exit a patient from care"))
				msg = MohTracUtil.getMessage(
						"tracpatienttransfer.exitPatientFromCare", null);}
		} catch (Exception e) {
			log.info(">>>>PATIENT>>TRANSFER>>EXTENSION>>> " + e.getMessage());
			e.printStackTrace();
		}
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.web.extension.LinkExt#getRequiredPrivilege()
	 */
	@Override
	public String getRequiredPrivilege() {
		return "Patient Dashboard - View Patient exited from care";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.web.extension.LinkExt#getUrl()
	 */
	@Override
	public String getUrl() {
		String patientId = this.getParameterMap().get("patientId");
		String url = "";
		try {
			PatientTransferService pts = Context
					.getService(PatientTransferService.class);
			if (pts.isThePatientExitedFromCare(Integer.valueOf(patientId)))
				url = "module/tracpatienttransfer/resumeCare.form";
			else
				url = "module/tracpatienttransfer/exitPatientFromCare.form";
		} catch (Exception e) {
			log.info(">>>>PATIENT>>TRANSFER>>EXTENSION>>> " + e.getMessage());
			e.printStackTrace();
		}
		return url;
	}
}
