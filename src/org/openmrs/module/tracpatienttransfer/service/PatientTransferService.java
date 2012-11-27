/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.service;

import java.util.Date;
import java.util.List;

/**
 * @author Yves GAKUBA
 * 
 */
public interface PatientTransferService {

	public List<Integer> getObsWithConceptReasonOfExit(Integer conceptId,
			Integer locationId);

	public List<Integer> getObsWithConceptReasonOfExitAndWithAnswer(
			Integer conceptId, String option);

	public Integer getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
			Integer conceptId, Date startDate, Date endDate);

	public boolean isThePatientExitedFromCare(Integer patientId);

	public Integer getLastObsWithReasonOfExitForPatient(Integer patientId);

	public List<Integer> findObsBasedOnCreteria(boolean includeVoided,
			String gender, Integer locationId, Integer reasonExitCareId,
			Date dateFrom, Date dateTo, Integer minAge, Integer maxAge, Integer providerId);
	
	public List<Integer> getObsWithConceptReasonPatientExitedFromCareVoided(Integer conceptId,
			Integer locationId);
	
	public List<String> getReasonsOfResumingCare();
	
	public Integer getNumberOfPatientCareResumeByReason(String reason);

}
