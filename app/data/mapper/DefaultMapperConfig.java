package data.mapper;

import org.mapstruct.MapperConfig;

/**
 * Provides a default shared configuration for mappers.
 *
 * @see {http://mapstruct.org/documentation/stable/reference/html/#shared-configurations}
 */
@MapperConfig(
    componentModel = "jsr330",
    uses = CommonMapper.class
)
public class DefaultMapperConfig {
}
