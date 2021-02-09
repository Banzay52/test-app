package crud.data.service;

import crud.data.entity.Car;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public interface CarService {
    Either<Throwable, Car> save(Car car);
    Try<Void> delete(Car car);
    Option<Car> findOneById(long id);
    Iterable<Car> findAll();
    Try<Void> deleteAll();
}
