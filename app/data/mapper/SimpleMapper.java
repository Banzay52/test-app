package data.mapper;

import org.mapstruct.MappingTarget;

public interface SimpleMapper<E, F> {

    F toForm(E e);

    E fromForm(F f);

    E update(F source, @MappingTarget E target);
}
