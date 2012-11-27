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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientUtil;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Base class for views that render usage data as a comma separated values
 */
public abstract class AbstractChartView extends AbstractView {

	protected static final Log log = LogFactory.getLog(AbstractChartView.class);

	private static Font font = new Font("Verdana", Font.PLAIN, 10);
	private static Color bkColor = new Color(240, 240, 250);

	/**
	 * @see org.springframework.web.servlet.view.AbstractView
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// Respond as a PNG image
		response.setContentType("image/png");

		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");

		int width = Integer.valueOf(request.getParameter("width"));
		int height = Integer.valueOf(request.getParameter("height"));;

		JFreeChart chart = createChart(model, request);
		chart.setBackgroundPaint(Color.WHITE);
		chart.getPlot().setOutlineStroke(new BasicStroke(0));
		chart.getPlot().setOutlinePaint(getBackgroundColor());
		chart.getPlot().setBackgroundPaint(getBackgroundColor());
		chart.getPlot().setNoDataMessage(TransferOutInPatientUtil.getMessage("tracpatienttransfer.error.noDataAvailable",null));

		ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart,
				width, height);
	}

	/**
	 * Generates a JFreeChart
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the servlet request
	 * @return the chart object
	 */
	protected abstract JFreeChart createChart(Map<String, Object> model,
			HttpServletRequest request);

	/**
	 * Gets the font for chart rendering
	 */
	protected Font getFont() {
		return font;
	}

	/**
	 * Gets the plot background color
	 */
	protected Color getBackgroundColor() {
		return bkColor;
	}
}
