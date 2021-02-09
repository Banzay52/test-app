package crud.data.repository;

import ch.insign.commons.db.repository.PagingAndSortingRepositoryImpl;
import crud.data.entity.Car;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.db.jpa.JPAApi;

import javax.inject.Inject;

public class CarRepository extends PagingAndSortingRepositoryImpl<Car, Long> {

    private final JPAApi jpaApi;

    @Inject
    public CarRepository(JPAApi jpaApi) {
        super(jpaApi, Car.class, Long.class);
        this.jpaApi = jpaApi;
    }

    public Option<Car> findOneByRegistrationId(String registrationId) {
        return jpaApi.withTransaction(em -> {
            return Try.of(() -> Option.of(em.createNamedQuery("Car.findByRegistrationId", Car.class)
                    .setParameter("registrationId", registrationId)
                    .getSingleResult())).getOrElse(Option.none());
        });
    }

}
