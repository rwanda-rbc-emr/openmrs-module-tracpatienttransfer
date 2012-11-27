/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.impl;

import java.util.Date;
import java.util.List;

import org.openmrs.module.tracpatienttransfer.db.PatientTransferDAO;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;

/**
 * @author Yves GAKUBA
 * 
 */
public class PatientTransferServiceImpl implements PatientTransferService {

	private PatientTransferDAO patientExitDao;

	/**
	 * @return the patientExitDao
	 */
	public PatientTransferDAO getPatientExitDao() {
		return patientExitDao;
	}

	/**
	 * @param patientExitDao
	 *            the patientExitDao to set
	 */
	public void setPatientExitDao(PatientTransferDAO patientExitDao) {
		this.patientExitDao = patientExitDao;
	}

	public List<Integer> getObsWithConceptReasonOfExit(Integer conceptId,
			Integer locationId) {
		return patientExitDao.getObsWithConceptReasonOfExit(conceptId,
				locationId);
	}

	public boolean isThePatientExitedFromCare(Integer patientId) {
		return patientExitDao.isThePatientExitedFromCare(patientId);
	}

	public List<Integer> getObsWithConceptReasonOfExitAndWithAnswer(
			Integer conceptId, String option) {
		return patientExitDao.getObsWithConceptReasonOfExitAndWithAnswer(
				conceptId, option);
	}

	public Integer getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
			Integer conceptId, Date startDate, Date endDate) {
		return patientExitDao
				.getNumberOfObsWithConceptReasonOfExitAndWithAnswer(conceptId,
						startDate, endDate);
	}

	@Override
	public Integer getLastObsWithReasonOfExitForPatient(Integer patientId) {
		return patientExitDao.getLastObsWithReasonOfExitForPatient(patientId);
	}

	@Override
	public List<Integer> findObsBasedOnCreteria(boolean includeVoided,
			String gender, Integer locationId, Integer reasonExitCareId,
			Date dateFrom, Date dateTo, Integer minAge, Integer maxAge,
			Integer providerId) {
		return patientExitDao.findObsBasedOnCreteria(includeVoided, gender,
				locationId, reasonExitCareId, dateFrom, dateTo, minAge, maxAge,
				providerId);
	}

	@Override
	public List<Integer> getObsWithConceptReasonPatientExitedFromCareVoided(
			Integer conceptId, Integer locationId) {
		return patientExitDao.getObsWithConceptReasonOfExitVoided(conceptId,
				locationId);
	}

	@Override
	public List<String> getReasonsOfResumingCare() {
		return patientExitDao.getReasonsOfResumingCare();
	}

	@Override
	public Integer getNumberOfPatientCareResumeByReason(String reason) {
		return patientExitDao.getNumberOfPatientCareResumeByReason(reason);
	}

}
