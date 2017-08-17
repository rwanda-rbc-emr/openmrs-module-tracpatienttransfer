/**
 * 
 */
package org.openmrs.module.tracpatienttransfer.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohorderentrybridge.api.MoHOrderEntryBridgeService;
import org.openmrs.module.tracpatienttransfer.util.TracPatientTransferConfigurationUtil;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientConstant;
import org.openmrs.module.tracpatienttransfer.util.TransferOutInPatientUtil;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * 
 */
public class PatientExitFromCareFormController extends
		ParameterizableViewController {

	private Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		DateFormat df = Context.getDateFormat();

		if (Context.getAuthenticatedUser() == null)
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/login.htm"));

		if (!TracPatientTransferConfigurationUtil.isConfigured())
			return new ModelAndView(new RedirectView(request.getContextPath()
					+ "/module/mohtracportal/configuration.form"));

		mav.setViewName(getViewName());
		Patient patient = null;
		try {
			patient = Context.getPatientService().getPatient(
					Integer.valueOf(request.getParameter("patientId")));
			mav
					.addObject(
							"exitFromCareReasons",
							TransferOutInPatientUtil
									.createConceptCodedOptions(TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE));
			mav
					.addObject(
							"causeOfDeathOptions",
							TransferOutInPatientUtil
									.createConceptCodedOptions(TransferOutInPatientConstant.CAUSE_OF_DEATH));
			mav.addObject("providers", TransferOutInPatientUtil
					.createProviderOptions());
			mav.addObject("providerId", Context.getAuthenticatedUser()
					.getUserId());
			mav.addObject("locationId", TracPatientTransferConfigurationUtil
					.getDefaultLocationId());
			mav.addObject("patient", patient);
			mav.addObject("patientDeadConceptId",
					TransferOutInPatientConstant.PATIENT_DEAD);
			mav.addObject("patientTransferedOutConceptId",
					TransferOutInPatientConstant.PATIENT_TRANSFERED_OUT);

			mav
					.addObject(
							"reasonForExitingCare",
							Context
									.getConceptService()
									.getConcept(
											TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE)
									.getDisplayString());
			mav
					.addObject(
							"reasonForTransferOut",
							Context
									.getConceptService()
									.getConcept(
											TransferOutInPatientConstant.REASON_FOR_TRANSFER_OUT)
									.getDisplayString());
			mav.addObject("causeOfDeath_title", Context.getConceptService()
					.getConcept(TransferOutInPatientConstant.CAUSE_OF_DEATH)
					.getDisplayString());
			mav.addObject("transferOutDate", Context.getConceptService()
					.getConcept(TransferOutInPatientConstant.TRANSFER_OUT_DATE)
					.getDisplayString());
			mav
					.addObject(
							"transferOutToLocation",
							Context
									.getConceptService()
									.getConcept(
											TransferOutInPatientConstant.TRANSFER_OUT_TO_A_LOCATION)
									.getDisplayString());

			if (request.getParameter("save") != null) {
				boolean saved = save(request, response);
				log
						.info(">>>>>>>>>>>>PatientExitedFromCare>>>  Transfer Saved ["
								+ saved + "]");
				if (saved) {
					Date encounterDate=df.parse(request
							.getParameter("encounterDate"));
					boolean stopPrgm = stopProgramForPatientWithId(patient
							.getPatientId(), encounterDate);
					boolean stopOrders = stopAllOrders(patient.getPatientId(),
							encounterDate,
							request);
					String errorMsg = "";
					errorMsg += (stopPrgm) ? "" : ""
							+ getMessageSourceAccessor().getMessage(
									"tracpatienttransfer.error.stopprogram");
					errorMsg += (stopOrders) ? "" : " / "
							+ getMessageSourceAccessor().getMessage(
									"tracpatienttransfer.error.stoporders");
					if (!stopOrders || !stopPrgm)
						request.getSession().setAttribute(
								WebConstants.OPENMRS_ERROR_ATTR, errorMsg);

				} else {
					String msg = getMessageSourceAccessor().getMessage(
							"tracpatienttransfer.error.save");
					request.getSession().setAttribute(
							WebConstants.OPENMRS_ERROR_ATTR, msg);
				}
			}
		} catch (Exception ex) {
			log
					.error(">>>>>>>>>>>>PatientExitedFromCare>>> An error occured : "
							+ ex.getMessage());
			ex.printStackTrace();
		}
		return mav;
	}

	/**
	 * For a given patient and discontinued date, it stops all orders
	 * 
	 * @param patientId
	 *            The patient you want to stop all orders
	 * @param discontinuedDate
	 *            The date you want to be the discontinueddate for all orders
	 * @return
	 */
	private boolean stopAllOrders(int patientId, Date discontinuedDate,
			HttpServletRequest request) {
		Patient p = Context.getPatientService().getPatient(patientId);
		List<DrugOrder> drugOrders = Context.getService(MoHOrderEntryBridgeService.class)
				.getDrugOrdersByPatient(p);
		Concept discontinuedReason = Context.getConceptService().getConcept(
				Integer.parseInt(request.getParameter("reasonExitCare")));
		try {
			for (DrugOrder drOr : drugOrders) {
				DrugOrder dr = null;
				if (drOr.isActive()) {
					dr = drOr;
					Context.getOrderService().discontinueOrder(dr, discontinuedReason, discontinuedDate, Context.getService(MoHOrderEntryBridgeService.class).getFirstCurrentProvider(), dr.getEncounter());

					log
							.info(">>>>>>>>>>>>PatientExitedFromCare>>> Trying to stop DrugOrder#"
									+ dr.getOrderId()
									+ " for Patient#"
									+ dr.getPatient().getPatientId());
					Context.getOrderService().saveOrder(dr, null);
					log
							.info(">>>>>>>>>>>>PatientExitedFromCare>>> Order stopped");
				}
			}

			return true;
		} catch (Exception ex) {
			log
					.error(">>>>>>>>>>>>PatientExitedFromCare>>> An error occured : "
							+ ex.getMessage());
			ex.printStackTrace();

			return false;
		}
	}

	/**
	 * Remove the patient from those followed in any open program where the
	 * patient is enrolled
	 * 
	 * @param patientId
	 *            The patientId for the patient to remove from program
	 * @param dateCompleted
	 *            Date of completion of all open program
	 * @return
	 */
	private boolean stopProgramForPatientWithId(int patientId,
			Date dateCompleted) {
		Patient p = Context.getPatientService().getPatient(patientId);
		Collection<PatientProgram> patientPrograms = Context
				.getProgramWorkflowService().getPatientPrograms(p);

		log.info(">>>>>>>>>>>>PatientExitedFromCare>>> Trying to end "
				+ patientPrograms.size()
				+ " program(s) in which the patient is enrolled in...");

		try {
			for (PatientProgram pp : patientPrograms) {
				PatientProgram programToEnd = null;
				if (TracPatientTransferConfigurationUtil.getHivProgramId() != null && pp.getProgram() != null && pp.getDateCompleted() == null && TracPatientTransferConfigurationUtil.getHivProgramId().equals(pp.getProgram().getProgramId())) {
					programToEnd = pp;
					programToEnd.setDateCompleted(dateCompleted == null ? new Date() : dateCompleted);
					log
							.info(">>>>>>>>>>>>PatientExitedFromCare>>> Trying to save patientProgram#"
									+ programToEnd.getProgram().getProgramId()
									+ " for Patient#"
									+ programToEnd.getPatient().getPatientId());
					Context.getProgramWorkflowService().updatePatientProgram(
							programToEnd);
					log
							.info(">>>>>>>>>>>>PatientExitedFromCare>>> Program Completed.");
				}
			}
			return true;
		} catch (Exception ex) {
			log
					.error("An error occured when trying to end a patient_program : \n"
							+ ex.getMessage());
			return false;
		}
	}

	/**
	 * Save the encounter for a patient exiting care
	 * 
	 * @param request
	 * @param response
	 */
	private boolean save(HttpServletRequest request,
			HttpServletResponse response) {

		User user = Context.getAuthenticatedUser();
		EncounterService es = Context.getEncounterService();
		LocationService ls = Context.getLocationService();
		PatientService ps = Context.getPatientService();
		ConceptService cs = Context.getConceptService();
		Location location = null;
		Patient patient = null;
		DateFormat df = Context.getDateFormat();

		try {
			if (Context.isAuthenticated()) {
				location = ls.getLocation(Integer.valueOf(request
						.getParameter("location")));
				Date encounterDate = df.parse(request
						.getParameter("encounterDate"));
				patient = ps.getPatient(Integer.valueOf(request
						.getParameter("patientId")));

				Encounter enc = new Encounter();
				enc.setCreator(user);
				enc.setDateCreated(new Date());
				enc.setEncounterDatetime(encounterDate);
				enc.setEncounterType(es.getEncounterType(2));
				enc.setLocation(location);
				enc.setPatient(patient);
				enc.setProvider(Context.getUserService().getUser(
						Integer.valueOf(request.getParameter("provider"))));

				// reason for exit
				Concept reasonForExit = cs.getConcept(Integer.parseInt(request
						.getParameter("reasonExitCare")));
				Obs reasonExit = new Obs();
				reasonExit.setCreator(user);
				reasonExit.setLocation(location);
				reasonExit.setObsDatetime(encounterDate);
				reasonExit.setPerson(patient);
				reasonExit
						.setConcept(cs
								.getConcept(TransferOutInPatientConstant.REASON_PATIENT_EXITED_FROM_CARE));
				reasonExit.setValueCoded(reasonForExit);

				// Concept discontinuedReason = cs.getConcept(
				// reasonForExit.getConceptId());

				if (reasonForExit.getConceptId() == TransferOutInPatientConstant.PATIENT_TRANSFERED_OUT) {
					// reason transfer out
					Obs reasonForTransferOut = new Obs();
					reasonForTransferOut.setCreator(user);
					reasonForTransferOut.setLocation(location);
					reasonForTransferOut.setObsDatetime(encounterDate);
					reasonForTransferOut.setPerson(patient);
					reasonForTransferOut
							.setConcept(cs
									.getConcept(TransferOutInPatientConstant.REASON_FOR_TRANSFER_OUT));
					reasonForTransferOut.setValueText(request
							.getParameter("reasonTransferOut"));
					enc.addObs(reasonForTransferOut);

					// transfer out date
					Obs transferOutDate = new Obs();
					transferOutDate.setCreator(user);
					transferOutDate.setLocation(location);
					transferOutDate.setObsDatetime(encounterDate);
					transferOutDate.setPerson(patient);
					transferOutDate
							.setConcept(cs
									.getConcept(TransferOutInPatientConstant.TRANSFER_OUT_DATE));
					transferOutDate.setValueDatetime(df.parse(request
							.getParameter("transferOutDate")));
					enc.addObs(transferOutDate);

					// transfer to location
					Obs transferToLocation = new Obs();
					transferToLocation.setCreator(user);
					transferToLocation.setLocation(location);
					transferToLocation.setObsDatetime(encounterDate);
					transferToLocation.setPerson(patient);
					transferToLocation
							.setConcept(cs
									.getConcept(TransferOutInPatientConstant.TRANSFER_OUT_TO_A_LOCATION));

					// location_to from list or from text-free
					if (request.getParameter("chkbx_locationNotFound") != null) {
						transferToLocation.setValueText(request
								.getParameter("transferToLocationText"));
					} else {
						transferToLocation
								.setValueText(ls.getLocation(
										Integer.valueOf(request
												.getParameter("locationTo")))
										.getName());
					}
					enc.addObs(transferToLocation);
				} else if (reasonForExit.getConceptId() == TransferOutInPatientConstant.PATIENT_DEAD) {
					// date of death in case the patient is dead
					Obs dateOfDeath = new Obs();
					dateOfDeath.setCreator(user);
					dateOfDeath.setLocation(location);
					dateOfDeath.setObsDatetime(encounterDate);
					dateOfDeath.setPerson(patient);
					dateOfDeath
							.setConcept(cs
									.getConcept(TransferOutInPatientConstant.DATE_OF_DEATH));
					dateOfDeath.setValueDatetime(df.parse(request
							.getParameter("dateOfDeath")));
					enc.addObs(dateOfDeath);

					// cause of death in case the patient is dead
					Obs causeOfDeath = new Obs();
					causeOfDeath.setCreator(user);
					causeOfDeath.setLocation(location);
					causeOfDeath.setObsDatetime(encounterDate);
					causeOfDeath.setPerson(patient);
					causeOfDeath
							.setConcept(cs
									.getConcept(TransferOutInPatientConstant.CAUSE_OF_DEATH));
					causeOfDeath.setValueCoded(cs.getConcept(Integer
							.valueOf(request.getParameter("causeOfDeath"))));
					enc.addObs(causeOfDeath);
				}

				// add reason of exit
				enc.addObs(reasonExit);

				// save encounter
				es.saveEncounter(enc);

				if (reasonForExit.getConceptId() == TransferOutInPatientConstant.PATIENT_DEAD) {
					log
							.info(">>>>>EXIT>>PATIENT>>FROM>>CARE>>> Trying save the death of the person ...");
					patient.setDead(true);
					patient.setDeathDate(df.parse(request
							.getParameter("dateOfDeath")));

					Context.getPatientService().savePatient(patient);
					log
							.info(">>>>>EXIT>>PATIENT>>FROM>>CARE>>> The death of the person saved !");
				}
			}
			return true;

		} catch (Exception ex) {
			log
					.error(">>>>>>>>>>>>PatientExitedFromCare>>> "
							+ ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
}
