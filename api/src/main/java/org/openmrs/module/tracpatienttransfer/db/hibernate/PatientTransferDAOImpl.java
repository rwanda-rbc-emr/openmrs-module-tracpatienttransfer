/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.db.hibernate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracpatienttransfer.db.PatientTransferDAO;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;

/**
 * @author Yves GAKUBA
 * 
 */
public class PatientTransferDAOImpl implements PatientTransferDAO {

	private Log log = LogFactory.getLog(this.getClass());
	private SessionFactory sessionFactory;

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Auto generated method comment
	 * 
	 * @return
	 */
	private Session getSession() {
		if (getSessionFactory().isClosed())
			log.info(">>>>PTO_DAO>> sessionFactory is closed!");
		Session session = getSessionFactory().getCurrentSession();
		if (session == null) {
			log.info(">>>>PTO_DAO>> Trying to close the existing session...");
			Context.closeSession();
			log.info(">>>>PTO_DAO>> Session closed.");
			log.info(">>>>PTO_DAO>> Trying to open new session...");
			Context.openSession();
			log.info(">>>>PTO_DAO>> New Session created.");
			try {
				session = getSessionFactory().getCurrentSession();
			} catch (Exception e) {
				log.error(">>>>>>>>PTO_DAO>> Session Error : " + session);
				e.printStackTrace();
			}
		}
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openmrs.module.tracpatienttransfer.db.PatientTransferDAO#
	 * getObsWithConceptReasonOfExit(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getObsWithConceptReasonOfExit(Integer conceptId,
			Integer locationId) {

		String query = "SELECT obs_id FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;

		query += (null != conceptId) ? " AND value_coded=" + conceptId : "";
		query += (null != locationId) ? " AND location_id=" + locationId : "";
		query += " AND voided=0 ORDER BY obs_datetime DESC";

		List<Integer> obsIdList = getSession().createSQLQuery(query).list();

		return obsIdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openmrs.module.tracpatienttransfer.db.PatientTransferDAO#
	 * isThePatientExitedFromCare(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public boolean isThePatientExitedFromCare(Integer patientId) {
		List<Integer> obsIdList = getSession()
				.createSQLQuery(
						"SELECT obs_id FROM obs WHERE person_id="
								+ patientId
								+ " AND concept_id="
								+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE
								+ " AND voided=0").list();

		return (obsIdList != null && obsIdList.size() != 0) ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openmrs.module.tracpatienttransfer.db.PatientTransferDAO#
	 * getObsWithConceptReasonOfExitWithReason(java.lang.Integer,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getObsWithConceptReasonOfExitAndWithAnswer(
			Integer conceptId, String option) {
		String query = "SELECT obs_id FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE
				+ " AND value_coded=" + conceptId;
		if (option.compareTo("2") == 0)
			query += " AND voided=0";
		else if (option.compareTo("3") == 0)
			query += " AND voided=1";
		query += " ORDER BY obs_datetime DESC";
		List<Integer> obsIdList = getSession().createSQLQuery(query).list();

		return obsIdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openmrs.module.tracpatienttransfer.db.PatientTransferDAO#
	 * getNumberOfObsWithConceptReasonOfExitWithReason(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public Integer getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
			Integer conceptId, Date startDate, Date endDate) {

		String query = "SELECT obs_id FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE
				+ " AND value_coded=" + conceptId + " AND voided=0";
		query += (null != startDate) ? " AND obs_datetime>='"
				+ new SimpleDateFormat("yyyy-MM-dd").format(startDate) + "'"
				: "";
		query += (null != endDate) ? " AND obs_datetime<='"
				+ new SimpleDateFormat("yyyy-MM-dd").format(endDate) + "'" : "";

		List<Integer> obsIdList = getSession().createSQLQuery(query).list();

		return obsIdList.size();
	}

	@Override
	public Integer getLastObsWithReasonOfExitForPatient(Integer patientId) {
		String query = "SELECT MAX(obs_id) FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE
				+ " AND voided=0 AND person_id=" + patientId.intValue();

		Object o = getSession().createSQLQuery(query).uniqueResult();

		return (o == null) ? null : (Integer) o;
	}

	@Override
	public List<Integer> findObsBasedOnCreteria(boolean includeVoided,
			String gender, Integer locationId, Integer reasonExitCareId,
			Date dateFrom, Date dateTo, Integer maxAge, Integer minAge,
			Integer providerId) {

		String query = "SELECT o.obs_id FROM obs o INNER JOIN person p ON o.person_id=p.person_id"
				+ ((providerId == null) ? ""
						: " INNER JOIN encounter e ON o.encounter_id=e.encounter_id")
				+ " WHERE o.concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;

		query += (includeVoided) ? "" : " AND o.voided=0";
		query += (gender == null) ? "" : " AND p.gender='" + gender + "'";
		query += (locationId == null) ? "" : " AND o.location_id=" + locationId;
		query += (reasonExitCareId == null) ? "" : " AND o.value_coded="
				+ reasonExitCareId;
		query += (dateFrom == null) ? ""
				: " AND CAST(o.obs_datetime AS DATE)>='"
						+ new SimpleDateFormat("yyyy-MM-dd").format(dateFrom)
						+ "'";
		query += (dateTo == null) ? "" : " AND CAST(o.obs_datetime AS DATE)<='"
				+ new SimpleDateFormat("yyyy-MM-dd").format(dateTo) + "'";
		query += (providerId == null) ? "" : " AND e.provider_id=" + providerId;

		query += " ORDER BY o.obs_datetime DESC";

		List<Integer> tempList = getSession().createSQLQuery(query).list();
		List<Integer> obsIdList = tempList;
		boolean minOk = true, maxOk = true;

		if (minAge != null || maxAge != null) {
			obsIdList = new ArrayList<Integer>();
			for (Integer obsId : tempList) {
				minOk = true;
				maxOk = true;
				Obs o = Context.getObsService().getObs(obsId);

				if (minAge != null) {
					if (o.getPerson().getAge().intValue() >= minAge.intValue())
						minOk = true;
					else
						minOk = false;
				}

				if (maxAge != null) {
					if (o.getPerson().getAge().intValue() <= maxAge.intValue())
						maxOk = true;
					else
						maxOk = false;
				}

				if (minOk && maxOk)
					obsIdList.add(obsId);
			}
		}

		return obsIdList;
	}

	@Override
	public List<Integer> getObsWithConceptReasonOfExitVoided(Integer conceptId,
			Integer locationId) {
		String query = "SELECT obs_id FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;

		query += (null != conceptId) ? " AND value_coded=" + conceptId : "";
		query += (null != locationId) ? " AND location_id=" + locationId : "";

		query += " AND voided=1 ORDER BY obs_datetime DESC";

		List<Integer> obsIdList = getSession().createSQLQuery(query).list();

		return obsIdList;
	}

	@Override
	public List<String> getReasonsOfResumingCare() {
		String query = "SELECT DISTINCT(void_reason) FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;

		query += " AND voided=1 ORDER BY void_reason ASC";

		List<String> reasonsOfResumingCare = getSession().createSQLQuery(query)
				.list();

		return reasonsOfResumingCare;
	}

	@Override
	public Integer getNumberOfPatientCareResumeByReason(String reason) {
		String query = "SELECT COUNT(void_reason) FROM obs WHERE concept_id="
				+ TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;

		query += " AND voided=1 AND void_reason='" + reason + "'";

		Integer numberOfPatientCareResumed = Integer.valueOf(""
				+ getSession().createSQLQuery(query).uniqueResult());

		return numberOfPatientCareResumed;
	}

}
