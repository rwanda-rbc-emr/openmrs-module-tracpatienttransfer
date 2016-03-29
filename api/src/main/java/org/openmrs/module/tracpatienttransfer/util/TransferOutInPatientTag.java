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
package org.openmrs.module.tracpatienttransfer.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;

/**
 * @author Yves GAKUBA
 */
public class TransferOutInPatientTag {

	/**
	 * @param personId
	 * @return
	 */
	public static String getPersonNames(Integer personId) {
		if (personId == null)
			return "-";
		Person person = Context.getPersonService().getPerson(personId);
		if (person == null)
			return "-";
		PersonName personName = person.getPersonName();
		if (personName == null)
			return "-";
		String names = (transformToCamelCase(personName.getGivenName()) + " " + transformToCamelCase(personName
				.getMiddleName()))
				+ " "
				+ ((personName.getFamilyName() != null) ? personName
						.getFamilyName().trim().toUpperCase() : "");

		return names;
	}

	/**
	 * @param word
	 * @return
	 */
	private static String transformToCamelCase(String word) {
		if (word == null || word.compareTo("") == 0)
			return word;
		String temp = "";
		int i = 0;
		for (char c : word.toCharArray()) {
			if (i == 0)
				temp += (c + "").toUpperCase();
			else
				temp += (c + "").toLowerCase();
			i++;
		}

		return temp;
	}

	/**
	 * @param e
	 * @param conceptId
	 * @return
	 */
	public static String getObservationValueFromEncounter(Obs ob,
			Integer conceptId) {
//		Log log = LogFactory.getLog(TransferOutInPatientTag.class);
		if (ob == null || conceptId == null)
			return "-";
		else {
			if (ob.getEncounter() != null) {
				Encounter e = ob.getEncounter();
				for (Obs o : e.getAllObs()) {
					if (o.getConcept().getConceptId().intValue() == conceptId.intValue()) {
						//return o.getValueCoded().getDisplayString().toUpperCase();
						String value=o.getValueAsString(Context.getLocale());
						return (value!=null)?value.toUpperCase():"-";
					}
				}
			}
			return "-";
		}
	}

	/**
	 * @param patientId
	 * @param identifierTypeId
	 * @return
	 */
	public static String personIdentifierByPatientIdAndIdentifierTypeId(
			Integer patientId, Integer identifierTypeId) {
		PatientIdentifier pi = null;
		Patient p = null;
		p = Context.getPatientService().getPatient(patientId);
		if (p != null)
			pi = p.getPatientIdentifier(identifierTypeId);
		return (pi != null) ? pi.toString() : "-";
	}

	/**
	 * @param obs
	 * @return
	 */
	public static String conceptValueByObs(Obs obs) {
		if (obs == null)
			return "-";
		return (obs.getValueCoded() != null) ? obs.getValueCoded()
				.getDisplayString() : "-";
	}

	/**
	 * @param conceptId
	 * @return
	 */
	public static String getConceptNameById(String conceptId) {
		Concept c = Context.getConceptService().getConcept(
				Integer.valueOf(conceptId));

		return (c != null) ? c.getName().getName() : "-";
	}

	/**
	 * @param obs
	 * @return
	 */
	public static String obsVoidedReason(Obs obs) {
		if (obs == null)
			return "-";
		return (obs.getVoided() == true) ? "Yes("
				+ obs.getVoidReason()
				+ " - "
				+ TransferOutInPatientTag
						.getPersonNames((obs.getVoidedBy() != null) ? obs
								.getVoidedBy().getPerson().getPersonId() : null)
				+ ")"
				: "No";
	}

	/**
	 * @param obs
	 * @return
	 */
	public static String getProviderByObs(Obs obs) {
		Log log = LogFactory.getLog(TransferOutInPatientTag.class);
		try {
			if (obs == null)
				return "-";
			if (obs.getEncounter() != null) {
				return obs.getEncounter().getProvider().getPersonName()
						.toString();
				// Person provider = obs.getEncounter().getProvider();
				// if (provider != null) {
				// return TransferOutInPatientTag.getPersonNames(obs
				// .getEncounter().getProvider().getPersonId());
				// } else
				// return "-";
			} else
				return "-";
		} catch (Exception e) {
			e.printStackTrace();
			return "-";
		}
	}

	/**
	 * @param conceptId
	 * @return
	 */
	public static String getNumberOfPatientExitedFromCareWithReason(
			Integer conceptId) {
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);
		return ""
				+ pts.getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
						conceptId, null, null);
	}

	/**
	 * @param option
	 * @return
	 */
	public static String getNumberOfPatientExitedFromCare() {
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);
		List<Integer> obsIdList = pts.getObsWithConceptReasonOfExit(null, null);
		if (null != obsIdList)
			return "" + obsIdList.size();
		return "0";
	}

	/**
	 * @param identifierTypeId
	 * @return
	 */
	public static String getIdentifierTypeNameById(String identifierTypeId) {
		if (identifierTypeId.trim().compareToIgnoreCase("") == 0)
			return "";
		PatientIdentifierType identifierType = Context.getPatientService()
				.getPatientIdentifierType(Integer.parseInt(identifierTypeId));
		return (identifierType != null) ? identifierType.getName() : "";
	}

}
