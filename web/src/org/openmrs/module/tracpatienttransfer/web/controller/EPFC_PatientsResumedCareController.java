/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.tracpatienttransfer.util.FileExporter;
import org.openmrs.module.tracpatienttransfer.util.TracPatientTransferConfigurationUtil;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientTag;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yves GAKUBA
 * 
 */
public class EPFC_PatientsResumedCareController extends
		ParameterizableViewController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(getViewName());

		List<Integer> res;
		List<Integer> numberOfPages;

		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/login.htm"));

		if (!TracPatientTransferConfigurationUtil.isConfigured())
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/module/mohtracportal/configuration.form"));

		loadUtils(mav);

		ObsService os = Context.getObsService();
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);

		int pageSize = TracPatientTransferConfigurationUtil
				.getNumberOfRecordPerPage();
		String pageNumber = request.getParameter("page");

		// rebuilding the existing parameters
		rebuildExistingParameters(request, mav);

		List<Obs> data = new ArrayList<Obs>();

		if (pageNumber == null) {
			mav.setViewName(getViewName() + "?page=1");
		} else
			mav.setViewName(getViewName());

		Integer locationId = null, conceptId = null;

		try {
			conceptId = (request.getParameter("reason") != null && request
					.getParameter("reason").trim().compareTo("") != 0) ? Integer
					.parseInt(request.getParameter("reason"))
					: null;
			locationId = (request.getParameter("location") != null && request
					.getParameter("location").trim().compareTo("") != 0) ? Integer
					.parseInt(request.getParameter("location"))
					: null;

			if (pageNumber.compareToIgnoreCase("1") == 0
					|| pageNumber.compareToIgnoreCase("") == 0) {

				res = new ArrayList<Integer>();
				res = pts.getObsWithConceptReasonPatientExitedFromCareVoided(
						conceptId, locationId);
				request.getSession().setAttribute("pefc_voided_res", res);

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
				request.getSession().setAttribute("pefc_voided_numberOfPages",
						numberOfPages);

			} else {
				res = (ArrayList<Integer>) request.getSession().getAttribute(
						"pefc_voided_res");
				numberOfPages = (ArrayList<Integer>) request.getSession()
						.getAttribute("pefc_voided_numberOfPages");

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

			if (Integer.valueOf(pageNumber) > 1)
				mav.addObject("prevPage", (Integer.valueOf(pageNumber)) - 1);
			else
				mav.addObject("prevPage", -1);
			if (Integer.valueOf(pageNumber) < numberOfPages.size())
				mav.addObject("nextPage", (Integer.valueOf(pageNumber)) + 1);
			else
				mav.addObject("nextPage", -1);
			mav.addObject("lastPage",
					((numberOfPages.size() >= 1) ? numberOfPages.size() : 1));

			String locationTitle = (locationId != null) ? MohTracUtil
					.getMessage("Encounter.location", null)
					+ " : "
					+ Context.getLocationService().getLocation(locationId)
							.getName() : "";
			String reasonOfExitTitle = (conceptId != null) ? MohTracUtil
					.getMessage(
							"tracpatienttransfer.general.reasonofexit.title",
							null)
					+ " : "
					+ TransferOutInPatientTag
							.getConceptNameById("" + conceptId) : "";

			String title = reasonOfExitTitle
					+ ((reasonOfExitTitle.trim().compareTo("") == 0 || locationTitle
							.trim().compareTo("") == 0) ? "" : " ; ")
					+ locationTitle;
			title = (title.trim().compareTo("") == 0) ? MohTracUtil.getMessage(
					"tracpatienttransfer.general.allpatientresumedcare.title", null) : title;
			mav.addObject("title", title);

			FileExporter fexp = new FileExporter();

			if (request.getParameter("export") != null
					&& request.getParameter("export")
							.compareToIgnoreCase("csv") == 0) {
				fexp
						.exportToCSVFile(
								request,
								response,
								res,
								"list_of_patients_exited_from_care_and_resumed_care.csv",
								"List of Patients exited from care and resumed care");
			}
			if (request.getParameter("export") != null
					&& request.getParameter("export")
							.compareToIgnoreCase("pdf") == 0) {
				fexp
						.exportToPDF(
								request,
								response,
								res,
								"list_of_patients_exited_from_care_and_resumed_care.pdf",
								"List of Patients exited from care and resumed care");
			}

			Integer nullVal = null;
			mav.addObject("nullVal", nullVal);

		} catch (Exception ex) {
			String msg = getMessageSourceAccessor().getMessage(
					"tracpatienttransfer.error.onloaddata");
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					msg);
			ex.printStackTrace();
		}

		return mav;
	}

	/**
	 * @param mav
	 * @throws Exception
	 */
	private void loadUtils(ModelAndView mav) throws Exception {
		mav.addObject("tracnetIdentifierTypeId",
				TracPatientTransferConfigurationUtil
						.getTracNetIdentifierTypeId());
		mav.addObject("localIdentifierTypeId",
				TracPatientTransferConfigurationUtil
						.getLocalHealthCenterIdentifierTypeId());
		// mav.addObject("obsList",
		// Context.getService(PatientTransferService.class).getObsWithConceptReasonPatientExitedFromCareVoided(null,
		// null));
	}

	/**
	 * @param request
	 * @param mav
	 */
	private void rebuildExistingParameters(HttpServletRequest request,
			ModelAndView mav) {
		String param = (request.getParameter("reason") != null) ? "&reason="
				+ request.getParameter("reason") : "";
		param += (request.getParameter("location") != null) ? "&location="
				+ request.getParameter("location") : "";
		param += (request.getParameter("provider") != null) ? "&provider="
				+ request.getParameter("provider") : "";
		mav.addObject("prmtrs", param);
	}

}
