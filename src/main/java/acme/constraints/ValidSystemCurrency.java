
package acme.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = SystemCurrencyValidator.class)
@Target({
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSystemCurrency {

	String message() default "The system currency must be included in the accepted currencies.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
