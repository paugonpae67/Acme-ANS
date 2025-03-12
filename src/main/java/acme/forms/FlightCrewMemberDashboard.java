
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	List<String>				LastFiveDestination;
	Integer						NumberOfLegsGravityBetween0And3;
	Integer						NumberOfLegsGravityBetween4And7;
	Integer						NumberOfLegsGravityBetween8And10;
	List<String>				LastMembersTeamInLastLeg;
	List<String>				SetAssignationConfirmedStatus;
	List<String>				SetAssignationCancelledStatus;
	List<String>				SetAssignationPendingStatus;
	Double						averageAssignationsNumberLastMonth;
	Integer						minimumAssignationsNumberLastMonth;
	Integer						maximumAssignationsNumberLastMonth;
	Double						standardDeviationAssignationsNumberLastMonth;
}
