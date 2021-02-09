import ch.insign.playauth.inject.PartyServiceModule;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.WSTestClient;
import play.test.WithServer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static play.mvc.Http.Status.INTERNAL_SERVER_ERROR;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.inMemoryDatabase;

public class ApplicationIntegrationTest extends WithServer {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure((Map) inMemoryDatabase())
                .build();
    }

    @Test
    public void testShouldReturnServerErrorWithoutReset() {
        try (WSClient ws = WSTestClient.newClient(port)) {
            CompletionStage<WSResponse> stage = ws.url("/").get();
            WSResponse response = stage.toCompletableFuture().get();
            assertEquals(INTERNAL_SERVER_ERROR, response.getStatus());
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testShouldReturnOk() throws Exception {
        try (WSClient ws = WSTestClient.newClient(port)) {
            CompletionStage<WSResponse> resetStage = ws.url(ch.insign.cms.controllers.routes.SetupController.reset().url()).get();
            resetStage.thenAccept(responseConsumer -> assertEquals(responseConsumer.getStatus(), OK)).thenRun(() -> {
                try {
                    CompletionStage<WSResponse> indexStage = ws.url("/").get();
                    WSResponse response = indexStage.toCompletableFuture().get();
                    assertEquals(OK, response.getStatus());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

}
