package util;

import org.junit.rules.ExternalResource;
import play.db.Database;
import play.db.Databases;

public class TestDatabase extends ExternalResource {

    public Database database;

    @Override
    public void before() {
        database = Databases.inMemoryWith("jndiName", "DefaultDS");
    }

    @Override
    public void after() {
        database.shutdown();
    }

}
