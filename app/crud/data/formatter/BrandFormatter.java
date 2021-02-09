package crud.data.formatter;

import crud.data.entity.Brand;
import crud.data.repository.BrandRepository;
import io.vavr.Value;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.data.format.Formatters;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Locale;

public class BrandFormatter extends Formatters.SimpleFormatter<Brand> {

    private final BrandRepository repository;

    @Inject
    public BrandFormatter(BrandRepository repository) {
        this.repository = repository;
    }

    @Override
    public Brand parse(String text, Locale locale) throws ParseException {
        return Try.of(() -> Option.ofOptional(repository.findById(Long.parseLong(text))))
                .map(Value::getOrNull)
                .getOrElseThrow(t -> new ParseException(t.getMessage(), 0));
    }

    @Override
    public String print(Brand brand, Locale locale) {
        return String.valueOf(brand.getId());
    }

}
