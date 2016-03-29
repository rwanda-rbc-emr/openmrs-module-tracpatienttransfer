/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohorderentrybridge.api.MoHOrderEntryBridgeService;
import org.openmrs.module.mohtracportal.service.MohTracPortalService;
import org.openmrs.module.mohtracportal.util.MohTracUtil;
import org.openmrs.module.tracpatienttransfer.service.PatientTransferService;
import org.openmrs.module.tracpatienttransfer.util.TracPatientTransferConfigurationUtil;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yves GAKUBA
 * 
 */
public class EPFC_ResumeCareFormController extends
		ParameterizableViewController {
	private Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/login.htm"));

		if (!TracPatientTransferConfigurationUtil.isConfigured())
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/module/mohtracportal/configuration.form"));

		mav.setViewName(getViewName());
		mav.addObject("patientDefaultedConceptId",
				TransferOutInPatientConstant.PATIENT_DEFAULTED);
		mav.addObject("patientRefusedConceptId",
				TransferOutInPatientConstant.PATIENT_REFUSED);
		mav.addObject("patientTransferredOutConceptId",
				TransferOutInPatientConstant.PATIENT_TRANSFERED_OUT);
		Patient patient = null;
		try {
			patient = Context.getPatientService().getPatient(
					Integer.valueOf(request.getParameter("patientId")));
			mav.addObject("patient", patient);
			if (request.getParameter("save") != null) {
				boolean saved = resumeCare(request, response);
				log.info(">>>>>>>RESUME>>CARE>>  Care resumed [" + saved + "]");

				if (saved) {
					;
				} else {
					String msg = getMessageSourceAccessor().getMessage(
							"tracpatienttransfer.error.resume");
					request.getSession().setAttribute(
							WebConstants.OPENMRS_ERROR_ATTR, msg);
				}
			}

			setLastExitInformation(request, mav);
		} catch (Exception ex) {
			log
					.error(">>>>>>>>>>>>EPFC_ResumeCareFormController>>> An error occured : "
							+ ex.getMessage());
			ex.printStackTrace();
		}
		return mav;
	}

	/**
	 * @param request
	 */
	private void setLastExitInformation(HttpServletRequest request,
			ModelAndView mav) {
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);
		Integer obsId = pts.getLastObsWithReasonOfExitForPatient(Integer
				.valueOf(request.getParameter("patientId")));

		if (null != obsId) {
			Obs lastObs = Context.getObsService().getObs(obsId);
			if (lastObs.getEncounter() != null) {
				mav.addObject("relatedEncounter", lastObs.getEncounter());
			}
			mav.addObject("lastObs", lastObs);
		}
	}

	/**
	 * Resume care
	 * 
	 * @param request
	 * @param response
	 */
	private boolean resumeCare(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PatientTransferService pts = Context
				.getService(PatientTransferService.class);
		Integer obsId = pts.getLastObsWithReasonOfExitForPatient(Integer
				.valueOf(request.getParameter("patientId")));

		if (null != obsId) {
			Obs lastObs = Context.getObsService().getObs(obsId);

			// void the observation
			Context.getObsService().voidObs(lastObs,
					request.getParameter("resumeCareReason"));

			if (lastObs.getEncounter() != null) {
				Context.getEncounterService().voidEncounter(
						lastObs.getEncounter(), "Resuming Care");
			}

			// if the patient was set dead
			if (lastObs.getPerson().isDead()) {
				Person p = lastObs.getPerson();
				p.setDead(false);
				p.setDeathDate(null);
				Context.getPersonService().updatePerson(p);
			}

			// if it was an error, restart all program and drug stopped
			if (request
					.getParameter("resumeCareReason")
					.trim()
					.compareTo(
							MohTracUtil
									.getMessage(
											"tracpatienttransfer.reason.resumeCare.error",
											null)) == 0) {
				// restart programs
				Collection<PatientProgram> ppList = Context
						.getProgramWorkflowService().getPatientPrograms(
								lastObs.getPatient());
				for (PatientProgram pp : ppList) {
					log.info(">>>>>>>PatientProgram....."
							+ pp.getDateCompleted()
							+ ".........PP="
							+ pp.getDateCompleted()
							+ "..."
							+ (pp.getDateCompleted() == lastObs
									.getObsDatetime()) + "...Obs="
							+ lastObs.getObsDatetime() + "------" + pp);
					PatientProgram programToRestart = null;
					if (pp.getDateCompleted() != null
							&& pp.getDateCompleted().compareTo(
									lastObs.getObsDatetime()) == 0) {
						programToRestart = pp;
						programToRestart.setDateCompleted(null);
						log
								.info(">>>>>>>PatientProgram.....Trying to update patientprogram...."
										+ pp);
						Context.getProgramWorkflowService()
								.updatePatientProgram(programToRestart);
						log.info(">>>>>>>PatientProgram.....UPDATED !");
					}
				}

				// restart drugs
				List<DrugOrder> dOrderList = Context.getService(MoHOrderEntryBridgeService.class).getDrugOrdersByPatient(lastObs.getPatient());
				for (DrugOrder dOrder : dOrderList) {
					log.info(">>>>>>>DrugOrder......."
							+ dOrder.isDiscontinuedRightNow()
							+ ".........DO="
							+ dOrder.getEffectiveStopDate()
							+ "...."
							+ (dOrder.getEffectiveStopDate() == lastObs
									.getObsDatetime()) + "...Obs="
							+ lastObs.getObsDatetime() + "----------" + dOrder);
					DrugOrder dOrderToRestart = null;
					if (dOrder.isDiscontinuedRightNow()
							&& dOrder.getEffectiveStopDate().compareTo(
									lastObs.getObsDatetime()) == 0) {
						dOrderToRestart = dOrder;
						//TODO Continue this ORder
						log
								.info(">>>>>>>PatientProgram.....Trying to update DrugOrder...."
										+ dOrderToRestart);
						Context.getOrderService().saveOrder(dOrderToRestart, null);
						log.info(">>>>>>>PatientProgram.....UPDATED !");
					}
				}

			} else // if it was a defaulted/refused patient, start a new hiv
					// program
			if (request
					.getParameter("resumeCareReason")
					.trim()
					.compareTo(
							MohTracUtil
									.getMessage(
											"tracpatienttransfer.reason.resumeCare.defaulted.return",
											null)) == 0 || request
											.getParameter("resumeCareReason")
											.trim()
											.compareTo(
													MohTracUtil
															.getMessage(
																	"tracpatienttransfer.reason.resumeCare.refused.rejoin",
																	null)) == 0) {
				PatientProgram pp = new PatientProgram();
				pp.setDateCreated(new Date());
				pp.setCreator(Context.getAuthenticatedUser());
				pp.setDateEnrolled(Context.getDateFormat().parse(
						request.getParameter("enrollmentDate")));
				pp.setPatient(Context.getPatientService().getPatient(
						lastObs.getPersonId()));
				pp
						.setProgram(Context.getProgramWorkflowService()
								.getProgram(
										TracPatientTransferConfigurationUtil
												.getHivProgramId()));

				Context.getProgramWorkflowService().savePatientProgram(pp);
			}
		}
		return true;
	}

}
