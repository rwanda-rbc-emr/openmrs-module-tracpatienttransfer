/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.tracpatienttransfer.util.TracPatientTransferConfigurationUtil;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.web.controller.PortletController;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Yves GAKUBA
 * 
 */
public class EPFC_PatientExitHistoryPortletController extends PortletController {

	@Override
	protected void populateModel(HttpServletRequest request,
			Map<String, Object> model) {

		Map<String, Object> pehObjects = new HashMap<String, Object>();
		try {
			int patientId = ServletRequestUtils.getIntParameter(request,
					"patientId", 0);

			PatientTransferService pts = Context
					.getService(PatientTransferService.class);
			Integer obsId = pts.getLastObsWithReasonOfExitForPatient(patientId);

			// log.info(">>>>>>>>>>>>>>>>>>>>> " + patientId + " = " + obsId);

			loadUtils(pehObjects);

			if (null != obsId)
				pehObjects.put("obsReasonExitingCare", Context.getObsService()
						.getObs(obsId));
			// else
			// pehObjects.put("obsReasonExitingCare", null);

			request.setAttribute("pehObjects", pehObjects);

		} catch (Exception e) {
			log.error(">>>>>>>>PATIENT>>EXIT>>HISTORY>> " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadUtils(Map<String, Object> pehObjects) throws Exception {
		pehObjects.put("patientDeceasedConceptId",
				TransferOutInPatientConstant.PATIENT_DEAD);
		pehObjects.put("causeOfDeathConceptId",
				TransferOutInPatientConstant.CAUSE_OF_DEATH);
		pehObjects.put("patientTransferredOutConceptId",
				TransferOutInPatientConstant.PATIENT_TRANSFERED_OUT);
		pehObjects.put("transferredToLocationConceptId",
				TransferOutInPatientConstant.TRANSFER_OUT_TO_A_LOCATION);
	}

}
