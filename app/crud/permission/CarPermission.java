package crud.permission;

import ch.insign.playauth.authz.DomainPermissionEnum;
import crud.data.entity.Car;

/**
 * Declares permissions which can be used to limit access to instances of Car type
 */
public enum CarPermission implements DomainPermissionEnum<Car> {
    EDIT, ADD, DELETE, BROWSE
}
