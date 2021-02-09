package util;

import ch.insign.cms.CMSApi;
import ch.insign.cms.security.CmsAuthorizationHandler;
import ch.insign.cms.utils.Error;
import ch.insign.playauth.authz.AuthorizationHandler;
import org.apache.shiro.authz.AuthorizationException;
import play.db.jpa.JPAApi;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class DemoProjectAuthorizationHandler extends CmsAuthorizationHandler implements AuthorizationHandler {

    @Inject
    public DemoProjectAuthorizationHandler(CMSApi cmsApi, MessagesApi messagesApi, Error error) {
        super(cmsApi, messagesApi, error);
    }

    @Override
    public Result onUnauthorized(Http.Request req, AuthorizationException e) {
        return super.onUnauthorized(req, e);
    }

    @Override
    public Result onUnauthenticated(Http.Request req, AuthorizationException e) {
        return super.onUnauthenticated(req, e);
    }

}
