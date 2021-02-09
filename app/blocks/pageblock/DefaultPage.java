package blocks.pageblock;

import ch.insign.cms.models.PageBlock;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "default_page")
@DiscriminatorValue("DefaultPage")
public class DefaultPage extends PageBlock {

    static final String SITE_1_KEY = "site1";
	static final String SITE_2_KEY = "site2";

	private Boolean activateSlider = false;
	private Boolean displayTitle = false;

    public Boolean getActivateSlider() {
        return activateSlider;
    }

    public void setActivateSlider(Boolean activateSlider) {
        this.activateSlider = activateSlider;
    }

    public Boolean getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(Boolean displayTitle) {
        this.displayTitle = displayTitle;
    }

}
