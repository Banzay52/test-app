package crud.data.service;

import com.google.inject.Inject;
import crud.data.entity.Car;
import crud.data.repository.CarRepository;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.Collections;

public class DemoCarService implements CarService {
    private final CarRepository carRepository;

    @Inject
    public DemoCarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Either<Throwable, Car> save(Car car) {
        return  Try.of(() -> carRepository.save(car)).toEither();
    }

    @Override
    public Try<Void> delete(Car car) {
        return Try.run(() -> carRepository.delete(car));
    }

    @Override
    public Option<Car> findOneById(long id) {
        return Try.of(() -> Option.ofOptional(carRepository.findById(id))).getOrElse(Option.none());
    }

    @Override
    public Iterable<Car> findAll() {
        return Try.of(carRepository::findAll).toEither().getOrElse(Collections.emptyList());
    }

    @Override
    public Try<Void> deleteAll() {
        return Try.run(carRepository::deleteAll);
    }

}
