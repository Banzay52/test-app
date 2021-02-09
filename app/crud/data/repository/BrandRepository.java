package crud.data.repository;

import ch.insign.commons.db.repository.PagingAndSortingRepositoryImpl;
import crud.data.entity.Brand;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.db.jpa.JPAApi;

import javax.inject.Inject;

public class BrandRepository extends PagingAndSortingRepositoryImpl<Brand, Long> {

    @Inject
    public BrandRepository(JPAApi jpaApi) {
        super(jpaApi, Brand.class, Long.class);
    }

}
