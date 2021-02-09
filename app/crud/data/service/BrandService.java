package crud.data.service;

import crud.data.entity.Brand;
import io.vavr.control.Either;
import io.vavr.control.Try;

public interface BrandService {
    Either<Throwable, Brand> save(Brand brand);
    Try<Void> delete(Brand brand);
    Iterable<Brand> findAll();
    Try<Void> deleteAll();
}
