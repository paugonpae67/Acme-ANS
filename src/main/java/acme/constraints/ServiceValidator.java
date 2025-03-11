
package acme.constraints;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.service.Service;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Date currentMoment = MomentHelper.getCurrentMoment();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentMoment);

			int currentYear = calendar.get(Calendar.YEAR);

			String lastTwoDigitsOfYear = String.valueOf(currentYear).substring(2);
			String lastTwo = service.getPromotionCode().substring(service.getPromotionCode().length() - 2);
			super.state(context, lastTwo.equals(lastTwoDigitsOfYear), "*", "acme.validation.promotion-code.year-mismatch");
		}
		result = !super.hasErrors(context);
		return result;
	}

}
