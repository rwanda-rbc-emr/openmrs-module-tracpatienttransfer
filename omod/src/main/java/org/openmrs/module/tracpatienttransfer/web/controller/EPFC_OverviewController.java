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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yves GAKUBA
 * 
 *         This controller backs the /web/tracpatienttransfer/overview.jsp page.
 *         This controller is tied to that jsp page in the
 *         /metadata/moduleApplicationContext.xml file
 */
public class EPFC_OverviewController extends ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/login.htm"));
		
		mav.setViewName(getViewName());

		Concept reasonForExingCare = Context.getConceptService().getConcept(
				TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE);
		mav.addObject("reasons", reasonForExingCare.getAnswers());
		mav.addObject("reasonForExitingCareTitle", reasonForExingCare
				.getDisplayString());

		return mav;
	}

}
