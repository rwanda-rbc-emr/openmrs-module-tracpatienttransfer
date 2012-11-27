/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.util;

import org.openmrs.module.mohtracportal.util.MohTracConfigurationUtil;

/**
 * @author Yves GAKUBA
 * 
 */
public class TracPatientTransferConfigurationUtil {

	/**
	 * constructor
	 */
	private TracPatientTransferConfigurationUtil() {
	}

	/**
	 * @return Returns true or false.<br/>
	 *         If the module is configured it returns true, otherwise it returns
	 *         false.
	 */
	public static boolean isConfigured() throws Exception {
		return MohTracConfigurationUtil.isConfigured();
	}

	/**
	 * @return Returns the HIV Program Id from configurations done in MOH-TRAC Portal
	 * @throws Exception
	 */
	public static Integer getHivProgramId() throws Exception {
		return MohTracConfigurationUtil.getHivProgramId();
	}

	/**
	 * @return Returns the number of record per page (25 is the default but can
	 *         be changed)
	 * @throws Exception
	 */
	public static Integer getNumberOfRecordPerPage() throws Exception {
		return MohTracConfigurationUtil.getNumberOfRecordPerPage();
	}

	/**
	 * @return Returns the default Location
	 * @throws Exception
	 */
	public static Integer getDefaultLocationId() throws Exception {
		return MohTracConfigurationUtil.getDefaultLocationId();
	}

	/**
	 * @return The TRACNET identifier type id, in case it has not been
	 *         configured, it will return null.
	 */
	public static int getTracNetIdentifierTypeId() throws Exception {
		return MohTracConfigurationUtil.getTracNetIdentifierTypeId();
	}

	/**
	 * @return The Local Health Center identifier type id, in case it has not
	 *         been configured, it will return null.
	 */
	public static int getLocalHealthCenterIdentifierTypeId() throws Exception {
		return MohTracConfigurationUtil.getLocalHealthCenterIdentifierTypeId();
	}

}
