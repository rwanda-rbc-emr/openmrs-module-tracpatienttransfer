/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.util;

import java.util.HashMap; //import java.util.List;
//
//import org.openmrs.Concept;
//import org.openmrs.ConceptAnswer;
//import org.openmrs.User;
//import org.openmrs.api.context.Context;
import org.openmrs.module.mohtracportal.util.MohTracUtil;

/**
 * @author Administrator
 * 
 */
public class TransferOutInPatientUtil {

	/**
	 * Retrieve a hashmap of concept answers (concept id, bestname) given the id
	 * of the coded concept question
	 * 
	 * @param codedConceptQuestionId
	 * @return
	 */
	public static HashMap<Integer, String> createConceptCodedOptions(
			int codedConceptQuestionId) {
		return MohTracUtil.createConceptCodedOptions(codedConceptQuestionId);
	}

	/**
	 * Creates a map of all providers in the system
	 * 
	 * @return
	 */
	public static HashMap<Integer, String> createProviderOptions() {
		return MohTracUtil.createProviderOptions();
	}

	/**
	 * Get a message from message propertie file
	 * 
	 * @param messageId
	 * @param parameters
	 * @return
	 */
	public static String getMessage(String messageId, Object[] parameters) {
		String msg = MohTracUtil.getMessage(messageId, parameters);
		return msg;
	}

	/*
	 * public Integer getAge(Date onDate) { if (birthdate == null) return null;
	 * 
	 * // Use default end date as today. Calendar today =
	 * Calendar.getInstance(); // But if given, use the given date. if (onDate
	 * != null) today.setTime(onDate);
	 * 
	 * // If date given is after date of death then use date of death as end
	 * date if(getDeathDate() != null && today.getTime().after(getDeathDate()))
	 * { today.setTime(getDeathDate()); }
	 * 
	 * Calendar bday = Calendar.getInstance(); bday.setTime(birthdate);
	 * 
	 * int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR);
	 * 
	 * // Adjust age when today's date is before the person's birthday int
	 * todaysMonth = today.get(Calendar.MONTH); int bdayMonth =
	 * bday.get(Calendar.MONTH); int todaysDay =
	 * today.get(Calendar.DAY_OF_MONTH); int bdayDay =
	 * bday.get(Calendar.DAY_OF_MONTH);
	 * 
	 * if (todaysMonth < bdayMonth) { age--; } else if (todaysMonth == bdayMonth
	 * && todaysDay < bdayDay) { // we're only comparing on month and day, not
	 * minutes, etc age--; }
	 * 
	 * return age; }
	 */

	public static int daysInMonth(Integer month, Integer year) {
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		} else if (month == 2) {
			if (null == year)
				return 28;
			else
				return (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29
						: 28);
		} else {
			return 31;
		}
	}

}
