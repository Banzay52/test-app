package crud.data.mapper;

import crud.data.entity.Car;
import crud.data.form.CarForm;
import data.mapper.DefaultMapperConfig;
import data.mapper.SimpleMapper;
import org.mapstruct.Mapper;

@Mapper(config = DefaultMapperConfig.class)
public interface CarMapper extends SimpleMapper<Car, CarForm> {
}
