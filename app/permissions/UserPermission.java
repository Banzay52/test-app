package permissions;

import ch.insign.playauth.authz.Allows;
import ch.insign.playauth.authz.DomainPermissionEnum;
import ch.insign.playauth.party.Party;

public enum UserPermission implements DomainPermissionEnum<Party> {
    /* Define new permissions for Party */
    READ_PHONE,

    /* Override PartyPermission's */
    @Allows("READ_PHONE")
    READ;
}
