
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = LegValidator.class)
@Target({
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLeg {

	String message() default "{acme.validation.leg}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
