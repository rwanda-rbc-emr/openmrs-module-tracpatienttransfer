/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.view.chart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientUtil;

/**
 * @author Yves GAKUBA
 * 
 */
public class EvolutionOfPEFCareByReasonOfExitChartView extends
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

		/*
		 * String yAxisLabel = TransferOutInPatientUtil.getMessage(
		 * "tracpatienttransfer.graph.evolution.yAxis", null); String xAxisLabel
		 * = TransferOutInPatientUtil.getMessage(
		 * "tracpatienttransfer.graph.evolution.xAxis", null);
		 * 
		 * Integer conceptId = (request.getParameter("reason") != null) ?
		 * Integer .valueOf(request.getParameter("reason")) :
		 * TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;
		 * 
		 * TimeSeriesCollection series = new TimeSeriesCollection();
		 * 
		 * // ---------------------------- if (request.getParameter("reason") !=
		 * null) { series.addSeries(createAnswerSerie(conceptId)); } else {
		 * Concept reasonForExitCare = Context .getConceptService() .getConcept(
		 * TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE);
		 * Collection<ConceptAnswer> answers = reasonForExitCare.getAnswers();
		 * 
		 * for (ConceptAnswer ca : answers) {
		 * series.addSeries(createAnswerSerie(ca.getAnswerConcept()
		 * .getConceptId())); } }
		 * 
		 * // ----------------------------
		 * 
		 * XYDataset xyDataset = series;
		 * 
		 * JFreeChart chart = ChartFactory.createTimeSeriesChart(Context
		 * .getConceptService().getConcept(conceptId).getDisplayString(),
		 * yAxisLabel, xAxisLabel, xyDataset, true, false, false);
		 * 
		 * XYPlot plot = (XYPlot) chart.getPlot();
		 * plot.setBackgroundPaint(Color.lightGray);
		 * plot.setDomainGridlinePaint(Color.white);
		 * plot.setRangeGridlinePaint(Color.white);
		 * 
		 * XYItemRenderer r = plot.getRenderer(); if (r instanceof
		 * XYLineAndShapeRenderer) { XYLineAndShapeRenderer renderer =
		 * (XYLineAndShapeRenderer) r; renderer.setBaseShapesVisible(true);
		 * renderer.setBaseShapesFilled(true); // renderer.setSeriesPaint(0,
		 * Color.BLUE); }
		 * 
		 * DateAxis axis = (DateAxis) plot.getDomainAxis();
		 * axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		 * 
		 * return chart;
		 */

		return koraGraphic(request);
	}

	private JFreeChart koraGraphic(HttpServletRequest request) {
		String categoryAxisLabel = MohTracUtil.getMessage(
				"tracpatienttransfer.graph.evolution.yAxis", null);
		String valueAxisLabel = MohTracUtil.getMessage(
				"tracpatienttransfer.graph.evolution.xAxis", null);

		Integer conceptId = (request.getParameter("reason") != null) ? Integer
				.valueOf(request.getParameter("reason"))
				: TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE;

		JFreeChart chart = ChartFactory.createLineChart(Context
				.getConceptService().getConcept(conceptId).getDisplayString(),
				categoryAxisLabel, valueAxisLabel, createDataset(conceptId,
						request), // data
				PlotOrientation.VERTICAL, true, false, false);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		// customise the range axis...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setUpperMargin(0.15);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		CategoryAxis axis = (CategoryAxis) plot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		// customise the renderer...
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);
		if (request.getParameter("reason") != null){
		renderer
				.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setItemLabelsVisible(true);}

		return chart;
	}

	private CategoryDataset createDataset(Integer conceptId,
			HttpServletRequest request) {

		// row keys...
//		String series1 = MohTracUtil.getMessage(
//				"tracpatienttransfer.graph.evolution.xAxis", null);

		// create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);

		Date d = new Date();

		if (request.getParameter("reason") != null) {
			for (int i = 0; i < 12; i++) {

				Date d1 = new Date(
						d.getTime()
								- (TransferOutInPatientConstant.YEAR_IN_MILLISECONDS - (TransferOutInPatientConstant.MONTH_IN_MILLISECONDS * i)));
				int month = d1.getMonth();
				int year = d1.getYear();

				Date startDate = new Date(year, month, 1);
				Date endDate = new Date(year, month, TransferOutInPatientUtil
						.daysInMonth((month + 1), year));

				month += 1;
				year += 1900;

				String dayLabel = new SimpleDateFormat("MMM-yyyy").format(d1);

				int value = pts
						.getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
								conceptId, startDate, endDate).intValue();

				// ts.add(new Month(month, year), value);
				dataset.addValue(value, Context.getConceptService().getConcept(conceptId).getDisplayString(), dayLabel);

			}

		} else {
			Concept reasonForExitCare = Context
					.getConceptService()
					.getConcept(
							TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE);
			Collection<ConceptAnswer> answers = reasonForExitCare.getAnswers();

			for (ConceptAnswer ca : answers) {
//				series.addSeries(createAnswerSerie(ca.getAnswerConcept()
//						.getConceptId()));
				
				for (int i = 0; i < 12; i++) {

					Date d1 = new Date(
							d.getTime()
									- (TransferOutInPatientConstant.YEAR_IN_MILLISECONDS - (TransferOutInPatientConstant.MONTH_IN_MILLISECONDS * i)));
					int month = d1.getMonth();
					int year = d1.getYear();

					Date startDate = new Date(year, month, 1);
					Date endDate = new Date(year, month, TransferOutInPatientUtil
							.daysInMonth((month + 1), year));

					month += 1;
					year += 1900;

					String dayLabel = new SimpleDateFormat("MMM-yyyy").format(d1);

					int value = pts
							.getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
									ca.getAnswerConcept()
									.getConceptId(), startDate, endDate).intValue();

					// ts.add(new Month(month, year), value);
					dataset.addValue(value, ca.getAnswerConcept()
							.getDisplayString(), dayLabel);

				}
				
			}
		}

		// int daysInWeeks = 12;
		//		
		// while (daysInWeeks >= 0) {
		// Date curDate = new Date(today.getTime() -
		// (VCTTracConstant.ONE_DAY_IN_MILLISECONDS * daysInWeeks));
		// String dayLabel = new SimpleDateFormat("EEE dd").format(curDate);
		// double value = vms.getNumberOfClientByDateOfRegistration(curDate);
		// dataset.addValue(value, series1, dayLabel);
		//			
		// daysInWeeks -= 1;
		// }

		return dataset;

	}

	/**
	 * @param conceptId
	 * @return
	 */
	private TimeSeries createAnswerSerie(Integer conceptId) {
		TimeSeries ts = new TimeSeries(Context.getConceptService().getConcept(
				conceptId).getDisplayString(), Month.class);

		PatientTransferService pts = Context
				.getService(PatientTransferService.class);

		Date d = new Date();
		for (int i = 0; i < 12; i++) {

			Date d1 = new Date(
					d.getTime()
							- (TransferOutInPatientConstant.YEAR_IN_MILLISECONDS - (TransferOutInPatientConstant.MONTH_IN_MILLISECONDS * i)));
			int month = d1.getMonth();
			int year = d1.getYear();

			Date startDate = new Date(year, month, 1);
			Date endDate = new Date(year, month, TransferOutInPatientUtil
					.daysInMonth((month + 1), year));

			month += 1;
			year += 1900;

			int value = pts.getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
					conceptId, startDate, endDate).intValue();

			ts.add(new Month(month, year), value);
		}

		return ts;
	}

	private TimeSeries createAnswerSerie1(Integer conceptId) {
		TimeSeries ts = new TimeSeries(Context.getConceptService().getConcept(
				conceptId).getDisplayString(), Month.class);

		PatientTransferService pts = Context
				.getService(PatientTransferService.class);

		Date d = new Date();
		Date d1 = new Date(d.getTime()
				- TransferOutInPatientConstant.YEAR_IN_MILLISECONDS);

		// /log.info("--------->>>> today="+Context.getDateFormat().format(d)+"-----oneYearFromNow="+Context.getDateFormat().format(d1));

		List<Integer> years = new ArrayList<Integer>();
		List<Integer> months = new ArrayList<Integer>();

		int initMonth = d1.getMonth();
		int initYear = d1.getYear();

//		log.info("--------->>>> initMonth=" + initMonth + "-----initYear="
//				+ initYear);

		for (int count = 0; count < 12; count++) {
			months.add(initMonth);
			years.add(initYear);

			initMonth = (initMonth == 11) ? 0 : (initMonth + 1);
			initYear = (initMonth == 11) ? (initYear + 1) : initYear;

//			log.info("------------" + (initMonth == 11)
//					+ "----Increment----->>>> initMonth=" + initMonth
//					+ "-----initYear=" + initYear);
		}

//		log.info(">>>>>>>>>>----------" + years.size() + "-----"
//				+ months.size() + "------------<<<<<<<<<<<<<");

		for (int i = 0; i < 12; i++) {

			// Date d1 = new Date(
			// d.getTime()
			// - (TransferOutInPatientConstant.YEAR_IN_MILLISECONDS -
			// (TransferOutInPatientConstant.MONTH_IN_MILLISECONDS * i)));
			// int month = d1.getMonth();
			// int year = d1.getYear();

			int month = months.get(i).intValue();
			int year = years.get(i).intValue();

//			log.info(">>>>>>>>>>........." + i + ". " + (month + 1) + "-"
//					+ year);

			Date startDate = new Date(year, month, 1);
			Date endDate = new Date(year, month, TransferOutInPatientUtil
					.daysInMonth((month + 1), year));

//			log.info(">>>>>>>>>>.........startDate="
//					+ Context.getDateFormat().format(startDate) + "-endDate="
//					+ Context.getDateFormat().format(endDate));

			// month += 1;
			// year += 1900;

			int value = pts.getNumberOfObsWithConceptReasonOfExitAndWithAnswer(
					conceptId, startDate, endDate).intValue();

			// log.info(">>>>>>>>>>>>>> Series-- m"+month+"-y"+year+"---sM_"+(new
			// Month(month, year))+"--sV_"+value);

			ts.add(new Month(month, year), value);
		}

		// log.info("------------------- TimeSeries--end---------------"+ts);
		return ts;
	}

}
