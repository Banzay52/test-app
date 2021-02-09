package service;

import ch.insign.cms.services.AbstractDatatableService;
import ch.insign.playauth.PlayAuthApi;
import crud.data.entity.Brand;
import crud.data.entity.Car;
import crud.permission.CarPermission;
import org.apache.commons.lang3.StringUtils;
import play.db.jpa.JPAApi;
import play.mvc.Http;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CarDatatableService extends AbstractDatatableService<Car> {

    private final String[] SORTABLE_FIELDS = {"id", "model", "brand","registrationId", "price", "buyDate"};
    private final String[] SEARCHABLE_FIELDS = {"model", "registrationId", "price"};

    private final PlayAuthApi playAuthApi;

    @Inject
    public CarDatatableService(JPAApi jpaApi, PlayAuthApi playAuthApi) {
        super(jpaApi);
        this.playAuthApi = playAuthApi;
    }

    // Total count of entities in table
    @Override
    protected long getTotalSize(EntityManager em) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root cars = query.from(Car.class);

        query.select(builder.count(cars));

        return em.createQuery(query).getSingleResult();
    }

    @Override
    protected TypedQuery<Car> findByKeywords(EntityManager em, String keywords, String orderByField, String orderByDirection) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Car> query = builder.createQuery(Car.class);
        Root<Car> cars = query.from(Car.class);

        List<Predicate> conditions = new ArrayList<>();
        for (String keyword : StringUtils.split(keywords)) {
            Predicate[] likeConditions = Arrays.stream(SEARCHABLE_FIELDS)
                    .map(field -> builder.like(builder.lower(cars.get(field)), "%" + keyword.trim().toLowerCase() + "%"))
                    .toArray(Predicate[]::new);

            conditions.add(builder.or(likeConditions));
        }

        query.where(builder.and(conditions.toArray(new Predicate[]{})));

        if ("asc".equals(orderByDirection)) {
            query.orderBy(builder.asc(cars.get(orderByField)));
        } else {
            query.orderBy(builder.desc(cars.get(orderByField)));
        }

        return em.createQuery(query.select(cars));
    }

    // List of fields which could be used for sorting
    @Override
    protected List<String> getSortableFields() {
        return Arrays.asList(SORTABLE_FIELDS);
    }

    // List of string representations of the entity fields
    @Override
    protected List<Function<Car, String>> getTableFields(Http.Request req) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return Arrays.asList(
                car -> String.valueOf(car.getId()),
                car -> car.getModel(),
                car -> car.getBrand().map(Brand::getTitle).getOrElse("-"),
                car -> car.getRegistrationId(),
                car -> car.getPrice().get().toString(),
                car -> car.getBuyDate().map(formatter::format).getOrElse("-"),
                car -> {
                        String data ="";
                        if (playAuthApi.isPermitted(req, CarPermission.EDIT, car)) {
                            data = "<a title=\"Bearbeiten\" href=\"" +
                                    crud.controller.routes.CarController.edit(car.getId()).url()
                                    + "\"><i class=\"fa fa-edit\"></i></a> &nbsp;";
                        }
                        if (playAuthApi.isPermitted(req, CarPermission.DELETE, car)) {
                            data = data + "<a class=\"delete-car\" data-url=\"" +
                                    crud.controller.routes.CarController.requestDeleteCar(car.getId()).url() +
                                    "\" href=\"" +
                                    crud.controller.routes.CarController.requestDeleteCar(car.getId()).url() +
                                    "\"><i class=\"fa fa-trash\"></i></a> &nbsp;";
                        }
                        if(StringUtils.isBlank(data)) {
                            data = "-";
                        }
                        return data;
                }
        );
    }

    @Override
    protected CriteriaQuery buildSearchCriteria(CriteriaQuery query, CriteriaBuilder builder, Root root, String keywords,
                                                String orderByField, String orderByDirection) {
        Root<Car> cars = (Root<Car>) root;

        List<Predicate> conditionsAnd = new ArrayList<>();
        List<String> searchFields = Arrays.asList(SEARCHABLE_FIELDS);

        for (String keyword : StringUtils.split(keywords)) {
            Predicate[] likeConditions = searchFields.stream()
                    .map(field -> builder.like(builder.lower(cars.get(field)), "%" + keyword.trim().toLowerCase() + "%"))
                    .toArray(Predicate[]::new);

            conditionsAnd.add(builder.or(likeConditions));
        }

        query.where(builder.and(conditionsAnd.toArray(new Predicate[]{})));

        if (StringUtils.isNotEmpty(orderByField)) {
            Expression orderByFieldExpression = cars.get(orderByField);

            query.orderBy(StringUtils.defaultIfEmpty(orderByDirection, "desc").equals("desc")
                    ? builder.desc(orderByFieldExpression)
                    : builder.asc(orderByFieldExpression));
        }

        return query;
    }
}
