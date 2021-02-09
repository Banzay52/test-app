package crud.data.service;

import com.google.inject.Inject;
import crud.data.entity.Brand;
import crud.data.repository.BrandRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.Collections;

public class DemoBrandService implements BrandService {
    private final BrandRepository brandRepository;

    @Inject
    public DemoBrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Either<Throwable, Brand> save(Brand brand) {
        return Try.of(() -> brandRepository.save(brand)).toEither();
    }

    @Override
    public Try<Void> delete(Brand brand) {
        return Try.run(() -> brandRepository.delete(brand));
    }

    @Override
    public Iterable<Brand> findAll() {
        return Try.of(brandRepository::findAll).toEither().getOrElse(Collections.emptyList());
    }

    @Override
    public Try<Void> deleteAll() {
        return Try.run(brandRepository::deleteAll);
    }

}
