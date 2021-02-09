package controllers;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * According to the link, controller test is a part of unit tests
 * {@see https://www.playframework.com/documentation/2.5.x/JavaTest}
 */
public class AdminControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure((Map) inMemoryDatabase())
                .build();
    }

    @Test
    public void testShouldInsertMockDataToDatabaseAndCheckUnauthorizedAccess() {
        running(app, () -> {
            Result result = route(app, fakeRequest(ch.insign.cms.controllers.routes.SetupController.reset()));
            assertEquals(OK, result.status());
            assertTrue(contentAsString(result).contains("Data initialized, now go to /"));// Unwrap content of html body and check on containing some data

            result = route(app, fakeRequest(controller.routes.AdminController.myApp()).header("Host", "localhost:9000"));
            assertEquals(SEE_OTHER, result.status());// Due to fact that the user not signed(expected 401 handling)
            assertTrue(result.header("Location").isPresent());
            assertEquals("/admin/login", result.header("Location").get());
        });
    }

}
