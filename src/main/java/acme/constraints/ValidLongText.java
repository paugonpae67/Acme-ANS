
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.hibernate.validator.constraints.Length;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation

@Length(min = 1, max = 255)

public @interface ValidLongText {

	// Standard validation properties -----------------------------------------

	String message() default "{acme.validation.text.message}";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
