/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.tracpatienttransfer.util.FileExporter;
import org.openmrs.module.tracpatienttransfer.util.TracPatientTransferConfigurationUtil;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientUtil;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yves GAKUBA
 * 
 */
public class AdvancedSearchFormController extends ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/login.htm"));

		if (!TracPatientTransferConfigurationUtil.isConfigured())
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/module/mohtracportal/configuration.form"));

		ModelAndView mav = new ModelAndView();
		String pageNumber = request.getParameter("page");
		if (pageNumber == null) {
			pageNumber = "1";
		}
		mav.setViewName(getViewName());

		loadUtils(mav);

		manageListing(mav, request, response);

		return mav;
	}

	/**
	 * @param mav
	 */
	private void loadUtils(ModelAndView mav) throws Exception {
		mav.addObject("providers", TransferOutInPatientUtil
				.createProviderOptions());
		mav
				.addObject(
						"exitFromCareReasons",
						TransferOutInPatientUtil
								.createConceptCodedOptions(TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE));
		mav
				.addObject(
						"reasonForExitingCare",
						Context
								.getConceptService()
								.getConcept(
										TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE)
								.getDisplayString());
//		mav
//				.addObject(
//						"causeOfDeathOptions",
//						TransferOutInPatientUtil
//								.createConceptCodedOptions(TransferOutInPatientConstant.CAUSE_OF_DEATH));
		mav.addObject("patientDeadConceptId",
				TransferOutInPatientConstant.PATIENT_DEAD);
		mav.addObject("patientTransferredOutConceptId",
				TransferOutInPatientConstant.PATIENT_TRANSFERED_OUT);
//		mav.addObject("causeOfDeath_title", Context.getConceptService()
//				.getConcept(TransferOutInPatientConstant.CAUSE_OF_DEATH)
//				.getDisplayString());
		mav.addObject("tracnetIdentifierTypeId",
				TracPatientTransferConfigurationUtil
						.getTracNetIdentifierTypeId());
		mav.addObject("localIdentifierTypeId",
				TracPatientTransferConfigurationUtil
						.getLocalHealthCenterIdentifierTypeId());
		mav.addObject("patientDeceasedConceptId", TransferOutInPatientConstant.PATIENT_DEAD);
		mav.addObject("causeOfDeathConceptId", TransferOutInPatientConstant.CAUSE_OF_DEATH);
		mav.addObject("transferredToLocationConceptId", TransferOutInPatientConstant.TRANSFER_OUT_TO_A_LOCATION);
	}

	/**
	 * @param mav
	 * @param request
	 */
	private void manageListing(ModelAndView mav, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Integer> res;
		List<Integer> numberOfPages;

		ObsService os = Context.getObsService();
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);

		int pageSize = TracPatientTransferConfigurationUtil
				.getNumberOfRecordPerPage();
		String pageNumber = request.getParameter("page");
		if (pageNumber == null) {
			pageNumber = "1";
		}
		// rebuilding the existing parameters
		rebuildExistingParameters(request, mav);

		List<Obs> data = new ArrayList<Obs>();

		boolean includeVoided = false;
		String gender = null;
		Integer locationId = null, reasonExitCareId = null, minAge = null, maxAge = null, providerId = null;
		Date dateFrom = null, dateTo = null;

		try {
			if (pageNumber.compareToIgnoreCase("1") == 0
					|| pageNumber.compareToIgnoreCase("") == 0) {

				includeVoided = (request.getParameter("includeVoided") != null && request
						.getParameter("includeVoided").trim()
						.compareToIgnoreCase("on") == 0) ? true : false;

				gender = (request.getParameter("gender") != null && request
						.getParameter("gender").trim().compareToIgnoreCase("") != 0) ? request
						.getParameter("gender")
						: null;

				locationId = (request.getParameter("location") != null && request
						.getParameter("location").trim()
						.compareToIgnoreCase("") != 0) ? Integer
						.valueOf(request.getParameter("location")) : null;

				reasonExitCareId = (request.getParameter("reasonExitCare") != null && request
						.getParameter("reasonExitCare").trim()
						.compareToIgnoreCase("") != 0) ? Integer
						.valueOf(request.getParameter("reasonExitCare")) : null;

//				causeOfDeathId = (request.getParameter("causeOfDeath") != null && request
//						.getParameter("causeOfDeath").trim()
//						.compareToIgnoreCase("") != 0) ? Integer
//						.valueOf(request.getParameter("causeOfDeath")) : null;
//				causeOfDeathId = (reasonExitCareId != null && reasonExitCareId == TransferOutInPatientConstant.PATIENT_DEAD) ? causeOfDeathId
//						: null;

				dateFrom = (request.getParameter("dateFrom") != null && request
						.getParameter("dateFrom").trim()
						.compareToIgnoreCase("") != 0) ? Context
						.getDateFormat().parse(
								(request.getParameter("dateFrom"))) : null;

				dateTo = (request.getParameter("dateTo") != null && request
						.getParameter("dateTo").trim().compareToIgnoreCase("") != 0) ? Context
						.getDateFormat()
						.parse((request.getParameter("dateTo")))
						: null;

				minAge = (request.getParameter("minAge") != null && request
						.getParameter("minAge").trim().compareToIgnoreCase("") != 0) ? Integer
						.valueOf(request.getParameter("minAge"))
						: null;

				maxAge = (request.getParameter("maxAge") != null && request
						.getParameter("maxAge").trim().compareToIgnoreCase("") != 0) ? Integer
						.valueOf(request.getParameter("maxAge"))
						: null;

				providerId = (request.getParameter("provider") != null && request
						.getParameter("provider").trim()
						.compareToIgnoreCase("") != 0) ? Integer
						.valueOf(request.getParameter("provider")) : null;

				res = new ArrayList<Integer>();
				res = pts.findObsBasedOnCreteria(includeVoided, gender,
						locationId, reasonExitCareId, dateFrom, dateTo, maxAge,
						minAge, providerId);
				request.getSession().setAttribute("as_res", res);

				// data collection
				for (int i = 0; i < pageSize; i++) {
					if (res.size() == 0)
						break;
					if (i >= res.size() - 1) {
						data.add(os.getObs(res.get(i)));
						break;
					} else
						data.add(os.getObs(res.get(i)));
				}

				// paging
				int n = (res.size() == ((int) (res.size() / pageSize))
						* pageSize) ? (res.size() / pageSize) : ((int) (res
						.size() / pageSize)) + 1;
				numberOfPages = new ArrayList<Integer>();
				for (int i = 1; i <= n; i++) {
					numberOfPages.add(i);
				}
				request.getSession().setAttribute("as_numberOfPages",
						numberOfPages);

			} else {
				res = (ArrayList<Integer>) request.getSession().getAttribute(
						"as_res");
				numberOfPages = (ArrayList<Integer>) request.getSession()
						.getAttribute("as_numberOfPages");

				for (int i = (pageSize * (Integer.parseInt(pageNumber) - 1)); i < pageSize
						* (Integer.parseInt(pageNumber)); i++) {
					if (i >= res.size())
						break;
					else
						data.add(os.getObs(res.get(i)));
				}
			}

			// page infos
			Object[] pagerInfos = new Object[3];
			pagerInfos[0] = (res.size() == 0) ? 0 : (pageSize * (Integer
					.parseInt(pageNumber) - 1)) + 1;
			pagerInfos[1] = (pageSize * (Integer.parseInt(pageNumber)) <= res
					.size()) ? pageSize * (Integer.parseInt(pageNumber)) : res
					.size();
			pagerInfos[2] = res.size();

			String pageInf = MohTracUtil
					.getMessage(
							"tracpatienttransfer.pagingInfo.showingResults",
							pagerInfos);

			mav.addObject("numberOfPages", numberOfPages);
			mav.addObject("obsList", data);
			mav.addObject("pageSize", pageSize);
			mav.addObject("pageInfos", pageInf);
			mav.addObject("title", MohTracUtil
					.getMessage(
							"tracpatienttransfer.search.result",
							null));

			createNavigationParamsForPaging(mav, Integer.valueOf(pageNumber),
					numberOfPages.size());

			FileExporter fexp = new FileExporter();

			if (request.getParameter("export") != null
					&& request.getParameter("export")
							.compareToIgnoreCase("csv") == 0) {
				fexp.exportToCSVFile(request, response, res,
						"list_of_patients_exited_from_care.csv",
						"List of Patients exited from care");
			}
			if (request.getParameter("export") != null
					&& request.getParameter("export")
							.compareToIgnoreCase("pdf") == 0) {
				fexp.exportToPDF(request, response, res,
						"list_of_patients_exited_from_care.pdf",
						"List of Patients exited from care");
			}

		} catch (Exception e) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					"tracpatienttransfer.error.onloaddata");
			e.printStackTrace();
		}

	}

	/**
	 * @param request
	 * @param mav
	 */
	private void rebuildExistingParameters(HttpServletRequest request,
			ModelAndView mav) {
		// location
		String param = (request.getParameter("location") != null) ? "&location="
				+ request.getParameter("location")
				: "";
		// provider
		param += (request.getParameter("provider") != null) ? "&provider="
				+ request.getParameter("provider") : "";
		// reason for exit care
		param += (request.getParameter("reasonExitCare") != null) ? "&reasonExitCare="
				+ request.getParameter("reasonExitCare")
				: "";
		// cause of death
		param += (request.getParameter("causeOfDeath") != null) ? "&causeOfDeath="
				+ request.getParameter("causeOfDeath")
				: "";
		// date
		param += (request.getParameter("dateFrom") != null) ? "&dateFrom="
				+ request.getParameter("dateFrom") : "";
		param += (request.getParameter("dateTo") != null) ? "&dateTo="
				+ request.getParameter("dateTo") : "";
		// age
		param += (request.getParameter("ageFrom") != null) ? "&ageFrom="
				+ request.getParameter("ageFrom") : "";
		param += (request.getParameter("ageTo") != null) ? "&ageTo="
				+ request.getParameter("ageTo") : "";
		// gender
		param += (request.getParameter("gender") != null) ? "&gender="
				+ request.getParameter("gender") : "";
		// include verbose
		param += (request.getParameter("includeVoided") != null) ? "&includeVoided="
				+ request.getParameter("includeVoided")
				: "";

		mav.addObject("prmtrs", param);
	}

	private void createNavigationParamsForPaging(ModelAndView mav,
			int pageNumber, int pageSize) {
		if (pageNumber > 1)
			mav.addObject("prevPage", (pageNumber) - 1);
		else
			mav.addObject("prevPage", -1);
		if (pageNumber < pageSize)
			mav.addObject("nextPage", (pageNumber) + 1);
		else
			mav.addObject("nextPage", -1);
		mav.addObject("lastPage", ((pageSize >= 1) ? pageSize : 1));
	}

}
