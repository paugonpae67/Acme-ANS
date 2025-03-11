
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = FlightNumberValidator.class)
@Target({
	ElementType.FIELD, ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFlightNumber {

	String message() default "{acme.validation.flight-number}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
