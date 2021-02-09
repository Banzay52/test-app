package crud.validator;

import crud.data.form.CarForm;
import crud.data.repository.CarRepository;
import io.vavr.control.Option;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.inject.Inject;
import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CarFormConstraints {

    // -- UniqueRegistrationId

    /**
     * Defines field as unique registration id.
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Constraint(validatedBy = UniqueRegistrationIdValidator.class)
    public @interface UniqueRegistrationId {
        String message() default "example.crud.car.registrationId.nonunique";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    /**
     * Validator for {@code @UniqueRegistrationId}
     */
    public static class UniqueRegistrationIdValidator implements Constraints.PlayConstraintValidator<UniqueRegistrationId, CarForm> {

        private final CarRepository carRepository;
        private String message;

        @Inject
        public UniqueRegistrationIdValidator(CarRepository carRepository) {
            this.carRepository = carRepository;
        }

        @Override
        public void initialize(UniqueRegistrationId constraintAnnotation) {
            this.message = constraintAnnotation.message();
        }

        @Override
        public boolean isValid(CarForm form, ConstraintValidatorContext context) {
            Option<ValidationError> error = carRepository.findOneByRegistrationId(form.getRegistrationId())
                    .filter(car -> !car.getId().equals(form.getId()))
                    .map(car -> new ValidationError("registrationId", message));

            return reportValidationStatus(error.getOrNull(), context);
        }
    }
}
