/**
 * All necessary constants for the module
 */
package org.openmrs.module.tracpatienttransfer.util;

/**
 * @author Yves GAKUBA
 * 
 */
public class TransferOutInPatientConstant {

	public final static Long YEAR_IN_MILLISECONDS = 31536000000L;

	public final static Long MONTH_IN_MILLISECONDS = 2628000000L;

	public static int DATE_OF_DEATH = 1815;

	public static int PATIENT_DEAD = 1742;

	public static int PATIENT_DEFAULTED = 1743;

	public static int PATIENT_TRANSFERED_OUT = 1744;

	public static int PATIENT_REFUSED = 3580;

	public static int CAUSE_OF_DEATH = 5002;

	public static int REASON_FOR_TRANSFER_OUT = 3004;

	public static int REASON_PATIENT_EXITED_FROM_CARE = 1811;

	public static int TRANSFER_OUT_DATE = 3001;

	public static int TRANSFER_OUT_TO_A_LOCATION = 3003;

	public static int[] DAY_IN_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
			31, 30, 31 };
}
