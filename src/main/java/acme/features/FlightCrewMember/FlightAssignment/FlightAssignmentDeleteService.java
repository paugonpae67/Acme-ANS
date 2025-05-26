
package acme.features.FlightCrewMember.FlightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.features.FlightCrewMember.ActivityLog.ActivityLogClaimRepository;
import acme.realms.FlightCrewMember;
import acme.realms.FlightCrewMemberStatus;

@GuiService
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentClaimRepository	repository;

	@Autowired
	private ActivityLogClaimRepository		activityLogRepository;


	@Override
	public void authorise() {
		boolean status = true;

		if (!super.getRequest().getMethod().equals("POST"))
			status = false;
		else {
			Integer Id;
			FlightAssignment assignment;
			FlightCrewMember member2;
			Integer legId = super.getRequest().getData("leg", Integer.class);
			Id = super.getRequest().getData("id", Integer.class);
			Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (Id == null)
				status = false;
			else if (legId == null)
				status = false;
			else if (memberId == null)
				status = false;
			else if (legId != 0) {
				Leg leg = this.repository.findLegById(legId);
				boolean validLeg = leg != null && !leg.isDraftMode(); // pa borrar da igual q este o no en pasado el leg. 
				FlightCrewMember m = this.repository.findFlightCrewMemberById(memberId);
				boolean validMember = m != null && m.getAvailabilityStatus() == FlightCrewMemberStatus.AVAILABLE;

				boolean correctMember = true;
				String employeeCode = super.getRequest().getData("flightCrewMembers", String.class);

				FlightCrewMember member = this.repository.findMemberByEmployeeCode(employeeCode);
				correctMember = member != null && memberId == member.getId();

				assignment = this.repository.findAssignmentById(Id);
				member2 = assignment == null ? null : assignment.getFlightCrewMembers();
				status = correctMember && validMember && super.getRequest().getPrincipal().hasRealm(member2) && member2 != null && validLeg && assignment != null && assignment.isDraftMode();

			}
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int id;
		FlightAssignment assignment;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentById(id);

		super.getBuffer().addData(assignment);

	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "remarks", "currentStatus");
		flightAssignment.setLeg(leg);
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		/*
		 * Collection<ActivityLog> act = this.repository.getActivityLogByFlight(flightAssignment.getId());
		 * if (!act.isEmpty())
		 * super.state(false, "*", "acme.validation.assignment.delete");
		 */
		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		//un activity log siempre va a estar realcionado con un flightassignemnt publicado y que portanto no se va a poder borrar 

		FlightCrewMember m = this.repository.findFlightCrewMemberById(memberId);
		boolean validMember = m != null && m.getAvailabilityStatus() == FlightCrewMemberStatus.AVAILABLE;
		super.state(validMember, "*", "acme.validation.assignment.statusIncorrect");

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.delete(flightAssignment);

	}

	/*
	 * @Override
	 * public void unbind(final FlightAssignment assignment) {
	 * int memberId;
	 * Collection<Leg> legs;
	 * SelectChoices legChoices = null;
	 * 
	 * Dataset dataset;
	 * 
	 * SelectChoices currentStatus;
	 * SelectChoices duty;
	 * 
	 * memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
	 * legs = this.repository.findAllLegsFuturePublished(MomentHelper.getCurrentMoment());
	 * Collection<Leg> legsOfMember;
	 * 
	 * legsOfMember = this.repository.findLegsByFlightCrewMember(memberId, assignment.getId());
	 * 
	 * for (Leg l : legsOfMember)
	 * if (legs.contains(l))
	 * legs.remove(l);
	 * 
	 * FlightCrewMember member;
	 * 
	 * member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
	 * currentStatus = SelectChoices.from(FlightAssignmentStatus.class, assignment.getCurrentStatus());
	 * duty = SelectChoices.from(FlightAssignmentDuty.class, assignment.getDuty());
	 * 
	 * dataset = super.unbindObject(assignment, "remarks", "moment", "currentStatus", "duty", "draftMode");
	 * dataset.put("currentStatus", currentStatus);
	 * legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
	 * 
	 * dataset.put("duty", duty);
	 * dataset.put("leg", legChoices.getSelected().getKey());
	 * dataset.put("legs", legChoices);
	 * dataset.put("flightCrewMembers", assignment.getFlightCrewMembers().getEmployeeCode());
	 * super.getResponse().addData(dataset);
	 * }
	 */

}
