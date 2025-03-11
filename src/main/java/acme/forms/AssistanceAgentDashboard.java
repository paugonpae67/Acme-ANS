
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistanceAgentDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	Double						numberofClaimsResolvedSuccessfully;
	Double						numberofClaimsRejected;
	List<Integer>				top3MonthsHighestNumberClaims;
	Double						avergeNumberLogsClaimsHave;
	Integer						minimumNumberLogsClaimsHave;
	Integer						maximumNumberLogsClaimsHave;
	Double						standardDeviationNumberLogsClaimsHave;
	Double						avergeNumberClaimsAssistedLastMonth;
	Integer						NumberClaimsAssistedLastMonth;
	Integer						maximumNumberClaimsAssistedLastMonth;
	Double						standardDeviationNumberClaimsAssistedLastMonth;
}
