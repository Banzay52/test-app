package party;

import ch.insign.cms.CMSApi;
import ch.insign.commons.db.SmartFormFactory;
import ch.insign.playauth.PlayAuthApi;
import ch.insign.playauth.party.PartyRepository;
import ch.insign.playauth.party.PartyService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import play.Application;
import play.db.jpa.JPAApi;
import com.google.inject.Provider;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class UserService extends PartyService<User> {
    private final Provider<Application> application;
    private final JPAApi jpaApi;
    private final CMSApi cmsApi;

    @Inject
    public UserService(
            JPAApi jpaApi,
            CMSApi cmsApi,
            PlayAuthApi playAuthApi,
            PartyRepository<User> partyRepository,
            SmartFormFactory formFactory,
            Provider<Application> application) {
        super(playAuthApi, partyRepository, formFactory);
        this.application = application;
        this.jpaApi = jpaApi;
        this.cmsApi = cmsApi;
    }

    public Class<User> getPartyClass() {
        return User.class;
    }

    public void saveFileWithUniqueAvatarName(User user, String fileName) throws IOException {
        String appPath = application.get().path().getPath();
        File tmpFile = FileUtils.getFile(appPath + File.separator + fileName);
        saveFileWithUniqueAvatarName(user, tmpFile);
    }

    public void saveFileWithUniqueAvatarName(User user, File tmpFile) throws IOException {
        String appPath = application.get().path().getPath();

        File userAvatar = new File(appPath + cmsApi.getConfig().imageUploadRootPath() + File.separator +
                (new Date()).getTime() + user.getId());
        FileUtils.copyFile(tmpFile, userAvatar);
        user.setImage(userAvatar.getName());
        tmpFile.delete();
    }

    public void deletePreviousAvatarImage(User user) {
        String appPath = application.get().path().getPath();
        if(StringUtils.isNotBlank(user.getImage())) {
            FileUtils.getFile(appPath + cmsApi.getConfig().imageUploadRootPath() +File.separator +
                    user.getImage()).delete();
        }
    }

    @Override
    public void delete(User party) {
        jpaApi.withTransaction(em -> {
            em.createNamedQuery("TokenAction.deleteByUser")
                    .setParameter("user", party)
                    .executeUpdate();
        });

        super.delete(party);
    }

}
