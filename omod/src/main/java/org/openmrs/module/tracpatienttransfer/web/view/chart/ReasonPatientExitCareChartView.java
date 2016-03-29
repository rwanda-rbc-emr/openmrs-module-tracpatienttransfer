/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.view.chart;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.util.ContextProvider;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientTag;
import org.springframework.context.ApplicationContext;

/**
 * @author Administrator
 * 
 */
public class ReasonPatientExitCareChartView extends AbstractChartView {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmrs.module.tracpatienttransfer.web.view.chart.AbstractChartView
	 * #createChart(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected JFreeChart createChart(Map<String, Object> model,
			HttpServletRequest request) {
		JFreeChart chart = null;
		ApplicationContext appContext = ContextProvider.getApplicationContext();
		try {
			if (request.getParameter("reason") == null)
				return chart;
			DefaultPieDataset pieDataset = new DefaultPieDataset();

			Concept reasonOfExit = Context.getConceptService().getConcept(
					Integer.valueOf(request.getParameter("reason")));

			int total = Context.getPatientService().getAllPatients().size();

			int other = 0;
			Float percentage = 0f, percentageOther = 0f;
			int nbrOfPatient = (Integer.valueOf(TransferOutInPatientTag
					.getNumberOfPatientExitedFromCareWithReason(reasonOfExit
							.getConceptId())));
			percentage = (100f * nbrOfPatient / total);
			pieDataset.setValue(reasonOfExit.getDisplayString()
					+ " ("
					+ nbrOfPatient
					+ " - "
					+ MohTracUtil.roundTo2DigitsAfterComma(percentage
							.doubleValue()) + "%)", MohTracUtil
					.roundTo2DigitsAfterComma(percentage.doubleValue()));
			other = total - nbrOfPatient;
			percentageOther = (100.0f * other / total);
			pieDataset.setValue(appContext.getMessage(
					"tracpatienttransfer.others", null, Context.getLocale())
					+ " ("
					+ other
					+ " - "
					+ MohTracUtil.roundTo2DigitsAfterComma(percentageOther
							.doubleValue()) + "%)", MohTracUtil
					.roundTo2DigitsAfterComma(percentageOther.doubleValue()));
			chart = ChartFactory.createPieChart(
					reasonOfExit.getDisplayString(), pieDataset, true, true,
					false);

			PiePlot plot = (PiePlot) chart.getPlot();
			plot.setExplodePercent(0, 0.3);
			plot.setSectionOutlinesVisible(true);
			plot.setCircular(false);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chart;
	}

}
