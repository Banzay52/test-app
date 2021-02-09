package data.mapper;

import data.form.RegisterUserForm;
import org.mapstruct.Mapper;
import party.User;

@Mapper(config = DefaultMapperConfig.class)
public interface UserMapper extends SimpleMapper<User, RegisterUserForm> {
}
