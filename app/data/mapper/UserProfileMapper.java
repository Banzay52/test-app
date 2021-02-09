package data.mapper;

import data.form.UserProfileForm;
import org.mapstruct.Mapper;
import party.User;

@Mapper(config = DefaultMapperConfig.class)
public interface UserProfileMapper extends SimpleMapper<User, UserProfileForm> {
}
