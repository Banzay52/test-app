package blocks.errorblock;

import ch.insign.cms.blocks.errorblock.ErrorPage;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "default_error_page")
@DiscriminatorValue("DefaultErrorPage")
public class DefaultErrorPage extends ErrorPage {

    public static final String COLLECTION_SLOT = "slot1";

}
