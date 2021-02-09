package service;

import ch.insign.cms.CMSApi;
import ch.insign.cms.services.PartyDatatableService;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.party.support.DefaultParty;
import org.apache.commons.lang3.StringUtils;
import party.User;
import play.db.jpa.JPAApi;
import play.mvc.Http;

import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UserDatatableService extends PartyDatatableService<User> {

    @Inject
    public UserDatatableService(CMSApi cmsApi, PlayAuthApi playAuthApi, JPAApi jpaApi) {
        super(cmsApi, playAuthApi, jpaApi);
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    protected List<String> getSortableFields() {
        return Arrays.asList("name", "email", "roleName", "lastLogin", "loginCount", "");
    }

    @Override
    protected List<Function<User, String>> getTableFields(Http.Request request) {
        return Arrays.asList(
                getPartyName(),
                getPartyEmail(),
                getPartyRoles(),
                getUserLastLogin(),
                getUserLoginCount(),
                getPartyActions(request)
        );
    }

    private Function<User, String> getUserLastLogin() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy'&nbsp;'HH:mm:ss");

        return user -> Optional.ofNullable(user.getLastLogin()).map(dateFormat::format).orElse("-");
    }

    private Function<User, String> getUserLoginCount() {
        return user -> Optional.ofNullable(user.getLoginCount()).map(Object::toString).get();
    }

    @Override
    protected CriteriaQuery buildSearchCriteria(CriteriaQuery query, CriteriaBuilder builder, Root root, String keywords,
                                                String orderByField, String orderByDirection) {
        Root<DefaultParty> users = (Root<DefaultParty>) root;

        List<Predicate> conditionsAnd = new ArrayList<>();
        List<String> searchFields = Arrays.asList("name", "email");

        for (String keyword : StringUtils.split(keywords)) {
            Predicate[] likeConditions = searchFields.stream()
                    .map(field -> builder.like(builder.lower(users.get(field)), "%" + keyword.trim().toLowerCase() + "%"))
                    .toArray(Predicate[]::new);

            conditionsAnd.add(builder.or(likeConditions));
        }

        query.where(builder.and(conditionsAnd.toArray(new Predicate[]{})));

        if (StringUtils.isNotEmpty(orderByField)) {
            Expression orderByFieldExpression;
            if (orderByField.equals("roleName")) {
                users.fetch("partyRoles", JoinType.LEFT);
                orderByFieldExpression = users.get("partyRoles").get("name");
            } else {
                orderByFieldExpression = users.get(orderByField);
            }

            query.orderBy(StringUtils.defaultIfEmpty(orderByDirection, "desc").equals("desc")
                    ? builder.desc(orderByFieldExpression)
                    : builder.asc(orderByFieldExpression));
        }

        return query;
    }
}
