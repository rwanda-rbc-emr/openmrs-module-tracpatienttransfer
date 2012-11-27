/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.view.chart;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openmrs.api.context.Context;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;

/**
 * @author Administrator
 * 
 */
public class ProportionOfPatientResumingCareByReasonChartView extends
		AbstractChartView {

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
		// create the chart...
		JFreeChart chart = ChartFactory.createBarChart("PATIENT RESUMING CARE", // chart
				// title
				"Reasons for Resuming Care", // domain axis label
				null, // range axis label
				createDataset(), // data
				PlotOrientation.HORIZONTAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);

		// set up gradient paints for series...
		GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f,
				0.0f, new Color(0, 0, 64));
		GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f,
				0.0f, new Color(0, 64, 0));
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);

		return chart;
	}

	private CategoryDataset createDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		PatientTransferService pts = Context
				.getService(PatientTransferService.class);
		List<String> reasonsOfResumingCare = pts.getReasonsOfResumingCare();

		for (String reason : reasonsOfResumingCare) {
			log.info(">>>>>>>>>>>>> numberOfPatientResumedCare : "+pts
					.getNumberOfPatientCareResumeByReason(reason));
			Integer numberOfPatientResumedCare = pts
					.getNumberOfPatientCareResumeByReason(reason);
			log.info(">>>>>>>>>>>>> numberOfPatientResumedCare : "+numberOfPatientResumedCare);
			dataset.addValue(((numberOfPatientResumedCare == null) ? 1
					: numberOfPatientResumedCare),
					"Reasons which makes patients to resume care", reason
							.toUpperCase());
		}

		return dataset;

	}

}
