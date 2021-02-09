package crud.controller;

import ch.insign.cms.controllers.GlobalActionWrapper;
import ch.insign.cms.permissions.aop.RequiresBackendAccess;
import ch.insign.cms.utils.AjaxResult;
import ch.insign.cms.utils.Error;
import ch.insign.cms.views.admin.utils.AdminContext;
import ch.insign.commons.db.SecureForm;
import ch.insign.playauth.PlayAuthApi;
import crud.data.form.CarForm;
import crud.data.mapper.CarMapper;
import crud.data.service.BrandService;
import crud.data.service.CarService;
import crud.permission.CarPermission;
import crud.views.html.edit;
import crud.views.html.list;
import crud.views.html.removeCarDialog;
import io.vavr.Tuple;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import service.CarDatatableService;

import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.vavr.API.*;

/**
 * Encapsulates a business logic tied to cars management
 */
@With({GlobalActionWrapper.class})
@RequiresBackendAccess
public class CarController extends Controller {

    private final PlayAuthApi playAuthApi;
    private final FormFactory formFactory;
    private final Error error;
    private final MessagesApi messagesApi;
    private final CarMapper carMapper;
    private final CarDatatableService carDatatableService;
    private final CarService carService;
    private final BrandService brandService;
    private final edit edit;
    private final list list;

    @Inject
    public CarController(
            PlayAuthApi playAuthApi,
            FormFactory formFactory,
            Error error,
            MessagesApi messagesApi,
            CarMapper carMapper,
            CarDatatableService carDatatableService,
            CarService carService,
            BrandService brandService,
            edit edit,
            list list) {
        this.playAuthApi = playAuthApi;
        this.formFactory = formFactory;
        this.error = error;
        this.messagesApi = messagesApi;
        this.carMapper = carMapper;
        this.carDatatableService = carDatatableService;
        this.carService = carService;
        this.brandService = brandService;
        this.edit = edit;
        this.list = list;
    }

    /**
     * Initializes car form includes validation rules
     * @return edit template with secure form to render
     */
    public Result add(Http.Request request) {
        /* Prevent unauthorized users to access this page.
         * @see {https://confluence.insign.ch/display/PLAY/Play+Auth+-+Authorization#PlayAuth-Authorization-ProtectingControllers}
         */
        playAuthApi.requirePermission(request, CarPermission.ADD);

        Form<CarForm> form = formFactory.form(CarForm.class);

        /* Protect the form from parameter tampering attacks.
         * @see {https://confluence.insign.ch/display/PLAY/Play+Commons#PlayCommons-SecureForm}
         */
        return ok(SecureForm.signForms(edit.render(
                form,
                StreamSupport.stream(brandService.findAll().spliterator(), false).collect(Collectors.toList()),
                request)));
    }

    /**
     * Binds data from request, validate its data and if hasn't errors, persist its data to database
     * @return redirect to car list page if new car was saved or bad request, if something was wrong
     */
    public Result doAdd(Http.Request request) {
        playAuthApi.requirePermission(request, CarPermission.ADD);

        Form<CarForm> form = formFactory.form(CarForm.class).bindFromRequest(request);

        return Match(form.hasErrors()).of(
                        Case($(true), () -> badRequest(SecureForm.signForms(edit.render(
                                form,
                                StreamSupport.stream(brandService.findAll().spliterator(), false).collect(Collectors.toList()),
                                request)))),
                        Case($(false), () -> carService.save(carMapper.fromForm(form.get()))
                                .map(c -> redirect(routes.CarController.list()).flashing(
                                        AdminContext.MESSAGE_SUCCESS,
                                        messagesApi.preferred(request).at(
                                                "example.crud.car.add.successful.message",
                                                c.getModel()
                                        )
                                ))
                                .getOrElse(error.internal(messagesApi.preferred(request).at("example.crud.car.add.failed.message")).apply(request)))
        );
    }

    /**
     * Finds a car by specified id and if exists, initialize a filled form includes validation rules
     * @param id - car id
     * @return edit template with secure form to render or not found page with error message
     */
    public Result edit(Http.Request request, Long id) {
        return carService.findOneById(id).map(car -> {
            playAuthApi.requirePermission(request, CarPermission.EDIT, car);

            Form<CarForm> form = formFactory.form(CarForm.class).fill(carMapper.toForm(car));

            return ok(SecureForm.signForms(edit.render(form, StreamSupport.stream(brandService.findAll().spliterator(), false).collect(Collectors.toList()),  request)));
        }).getOrElse(error.notFound(messagesApi.preferred(request).at("example.crud.car.error.notfound", id)).apply(request));
    }

    /**
     * Binds data from request, validate its data and saves its if hasn't errors
     * @param id - car id
     * @return redirect to car list page if success or throws bad request is errors present
     */
    public Result doEdit(Http.Request request, Long id) {
        return carService.findOneById(id)
                .peek(car -> playAuthApi.requirePermission(request, CarPermission.EDIT, car))
                .map(car -> Tuple.of(car, formFactory.form(CarForm.class).bindFromRequest(request)))
                .map(t -> Match(t._2.hasErrors()).of(
                        Case($(true), () -> badRequest(SecureForm.signForms(edit.render(
                                t._2,
                                StreamSupport.stream(brandService.findAll().spliterator(), false).collect(Collectors.toList()),
                                request)))),
                        Case($(false), () -> carService.save(carMapper.update(t._2.get(), t._1))
                                .map(c -> redirect(routes.CarController.list()).flashing(
                                        AdminContext.MESSAGE_SUCCESS,
                                        messagesApi.preferred(request).at(
                                                "example.crud.car.edit.successful.message",
                                                c.getModel()
                                        )
                                ))
                                .getOrElse(error.internal(messagesApi.preferred(request).at("example.crud.car.edit.failed.message")).apply(request)))
                ))
                .getOrElse(error.notFound(messagesApi.preferred(request).at("example.crud.car.notfound.message", id)).apply(request));
    }

    /**
     * Delete a car by specified id
     * @param id - car id
     * @return redirect to car list page if success or not found status, if no car present with specified id
     */
    public Result delete(Http.Request request, Long id) {
        return carService.findOneById(id)
                .peek(car -> playAuthApi.requirePermission(request, CarPermission.DELETE, car))
                .map(car -> carService.delete(car)
                        .map(v -> AjaxResult.ok(messagesApi.preferred(request).at("example.crud.car.delete.successful.message")))
                        .getOrElse(error.internal(messagesApi.preferred(request).at("example.crud.car.error.delete")).apply(request)))
                .getOrElse(error.notFound(messagesApi.preferred(request).at("example.crud.car.error.notfound", id)).apply(request));
    }

    /**
     * Prints a list with all cars with possibility to filter its data by brand/pagination
     * @return a page - the list of cars with pagination to render
     */
    public Result list(Http.Request request) {
        return ok(list.render(request));
    }

    public Result datatable(Http.Request request) {
        return ok(carDatatableService.getDatatable(request));
    }

    @RequiresBackendAccess
    public Result requestDeleteCar(Http.Request request, Long id) {
        return carService.findOneById(id)
                .peek(car -> playAuthApi.requirePermission(request, CarPermission.DELETE, car))
                .map(car -> ok(removeCarDialog.render(car)))
                .getOrElse(AjaxResult.error(messagesApi.preferred(request).at("example.crud.car.notfound.message", id))) ;
    }

}
