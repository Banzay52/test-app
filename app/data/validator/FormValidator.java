package data.validator;

import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Http;

import java.util.List;

public abstract class FormValidator<T> {

    public Form<T> validate(Form<T> form, Http.Request request) {
        List<ValidationError> customErrors = validateOnCustomErrors(form, request);
        for (ValidationError e : customErrors) {
            form = form.withError(e);
        }
        return form;
    }

    protected abstract List<ValidationError> validateOnCustomErrors(Form<T> form, Http.Request request);
}
