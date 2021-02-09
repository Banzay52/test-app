package data.mapper;

import io.vavr.control.Option;

public class CommonMapper {

    public <A> A genericOptionToObjectOrNull(Option<A> optional) {
        return optional.getOrElse(() -> null);
    }

}
