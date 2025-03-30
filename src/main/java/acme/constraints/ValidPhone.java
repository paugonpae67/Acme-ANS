
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Target({
	ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@ReportAsSingleViolation

public @interface ValidPhone {

	// Standard validation properties -----------------------------------------

	String message() default "The format is incorrect";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
