
package acme.forms;

import acme.client.components.basis.AbstractForm;
import acme.entities.review.Review;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	Integer						numberAirportInternational;
	Integer						numberAirportDomestic;
	Integer						numberAirportRegional;
	Integer						numberAirlineLuxury;
	Integer						numberAirlineStandard;
	Integer						numberAirlineLowCost;
	Integer						ratioAirlinesWithEmailAndPhone;
	Integer						ratioActiveAircraft;
	Integer						ratioNonActiveAircraft;
	Integer						ratioReviewScore;
	Integer						numberReviewsOver10weeks;
	Double						averageReviewsOver10weeks;
	Review						minimumReviewsOver10weeks;
	Review						maximumReviewsOver10weeks;
	Double						stddevReviewsOver10weeks;

}
