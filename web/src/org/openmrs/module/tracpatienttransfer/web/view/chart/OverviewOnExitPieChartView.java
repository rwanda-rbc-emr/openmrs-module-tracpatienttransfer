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
package org.openmrs.module.tracpatienttransfer.web.view.chart;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.util.ContextProvider;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientTag;
import org.springframework.context.ApplicationContext;

/**
 * View to render data as a chart image
 */
public class OverviewOnExitPieChartView extends AbstractChartView {

	@Override
	protected JFreeChart createChart(Map<String, Object> model,
			HttpServletRequest request) {

		JFreeChart chart = null;
		UserContext userContext = Context.getUserContext();
		ApplicationContext appContext = ContextProvider.getApplicationContext();
		try {
			DefaultPieDataset pieDataset = new DefaultPieDataset();
			Concept reasonForExitCare = Context
					.getConceptService()
					.getConcept(
							TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE);
			Collection<ConceptAnswer> answers = reasonForExitCare.getAnswers();

			int total = Integer.valueOf(TransferOutInPatientTag
					.getNumberOfPatientExitedFromCare());

			int other = 0;
			Float percentage = 0f;
			for (ConceptAnswer ca : answers) {
				int nbrOfPatient = (Integer.valueOf(TransferOutInPatientTag
						.getNumberOfPatientExitedFromCareWithReason(ca
								.getAnswerConcept().getConceptId())));
				percentage = (100f * nbrOfPatient / total);
				
				if (percentage > 5)
					pieDataset.setValue(ca.getAnswerConcept()
							.getDisplayString()
							+ " (" + nbrOfPatient + " - " + MohTracUtil.roundTo2DigitsAfterComma(percentage.doubleValue()) + "%)",
							MohTracUtil.roundTo2DigitsAfterComma(percentage.doubleValue()));
				else
					other += nbrOfPatient;
			}

			if (other > 0) {
				String otherTitle = appContext.getMessage(
						"tracpatienttransfer.others", null, userContext
								.getLocale());
				percentage = (100f * other / total);
				pieDataset.setValue(otherTitle + " (" + other + " - "
						+ MohTracUtil.roundTo2DigitsAfterComma(percentage.doubleValue()) + "%)", MohTracUtil.roundTo2DigitsAfterComma(percentage.doubleValue()));
			}
			chart = ChartFactory.createPieChart(reasonForExitCare
					.getDisplayString(), pieDataset, true, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chart;
	}
}
