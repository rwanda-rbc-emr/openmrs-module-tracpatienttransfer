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
package org.openmrs.module.tracpatienttransfer.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.tracpatienttransfer.util.FileExporter;
import org.openmrs.module.tracpatienttransfer.util.TracPatientTransferConfigurationUtil;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientTag;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yves GAKUBA
 */
public class ExitPatientFromCareListController extends
		ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.web.servlet.mvc.ParameterizableViewController#
	 * handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();

		List<Integer> res;
		List<Integer> numberOfPages;

		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/login.htm"));

		if (!TracPatientTransferConfigurationUtil.isConfigured())
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/module/mohtracportal/configuration.form"));

		// load utils for the page
		loadUtils(mav);

		ObsService os = Context.getObsService();
		PatientTransferService service = Context.getService(PatientTransferService.class);

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
				res = service.getObsWithConceptReasonOfExit(conceptId,
						locationId);
				request.getSession().setAttribute("epfc_res", res);

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
				request.getSession().setAttribute("epfc_numberOfPages",
						numberOfPages);

			} else {
				res = (ArrayList<Integer>) request.getSession().getAttribute(
						"epfc_res");
				numberOfPages = (ArrayList<Integer>) request.getSession()
						.getAttribute("epfc_numberOfPages");

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
					"tracpatienttransfer.general.allexit.title", null) : title;
			mav.addObject("title", title);

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
		mav.addObject("patientDeceasedConceptId",
				TransferOutInPatientConstant.PATIENT_DEAD);
		mav.addObject("causeOfDeathConceptId",
				TransferOutInPatientConstant.CAUSE_OF_DEATH);
		mav.addObject("patientTransferredOutConceptId",
				TransferOutInPatientConstant.PATIENT_TRANSFERED_OUT);
		mav.addObject("transferredToLocationConceptId",
				TransferOutInPatientConstant.TRANSFER_OUT_TO_A_LOCATION);
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
