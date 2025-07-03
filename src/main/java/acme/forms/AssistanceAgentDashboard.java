
package acme.forms;

import java.util.Collection;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistanceAgentDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	Double						numberofClaimsResolvedSuccessfully;
	Double						numberofClaimsRejected;
	Collection<String>			top3MonthsHighestNumberClaims;
	Double						avergeNumberLogsClaimsHave;
	Long						minimumNumberLogsClaimsHave;
	Long						maximumNumberLogsClaimsHave;
	Double						standardDeviationNumberLogsClaimsHave;
	Double						avergeNumberClaimsAssistedLastMonth;
	Integer						minimumNumberClaimsAssistedLastMonth;
	Integer						maximumNumberClaimsAssistedLastMonth;
	Double						standardDeviationNumberClaimsAssistedLastMonth;
}
