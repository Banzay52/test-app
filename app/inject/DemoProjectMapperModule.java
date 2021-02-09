package inject;

import com.google.inject.AbstractModule;
import crud.data.mapper.CarMapper;
import data.mapper.UserMapper;
import data.mapper.UserProfileMapper;
import service.CarDatatableService;

/**
 * Bindings for MapStruct mappers at `crud.mappers` package.
 */
public class DemoProjectMapperModule extends AbstractModule {

    private static final String IMPLEMENTATION_SUFFIX = "Impl";

    @Override
    protected void configure() {
        bind(CarDatatableService.class).asEagerSingleton();
        bind(CarMapper.class).to(loadImplementation(CarMapper.class));
        bind(UserMapper.class).to(loadImplementation(UserMapper.class));
        bind(UserProfileMapper.class).to(loadImplementation(UserProfileMapper.class));
    }

    /**
     * <p>
     * IDE won't find the implementation class and show up red warnings because the implementation classes are created
     * during compile time by MapStruct library.
     * <p>
     * Manually marking the target directory with generated sources as "Generated Sources Root" solves this issue, so it
     * might be the solution to commit that setting to GIT with other sources.
     */
    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> loadImplementation(Class<T> interfaze) {
        try {
            return (Class<? extends T>) getClass().getClassLoader().loadClass(interfaze.getName() + IMPLEMENTATION_SUFFIX);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
