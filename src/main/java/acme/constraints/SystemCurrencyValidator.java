
package acme.constraints;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import acme.entities.systemCurrency.SystemCurrency;

public class SystemCurrencyValidator implements ConstraintValidator<ValidSystemCurrency, SystemCurrency> {

	@Override
	public boolean isValid(final SystemCurrency systemCurrency, final ConstraintValidatorContext context) {
		if (systemCurrency == null || systemCurrency.getCurrency() == null || systemCurrency.getAcceptedCurrencies() == null)
			return true;

		List<String> acceptedList = Arrays.asList(systemCurrency.getAcceptedCurrencies().split(","));
		return acceptedList.contains(systemCurrency.getCurrency());
	}
}
