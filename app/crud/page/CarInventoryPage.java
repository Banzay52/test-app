package crud.page;

import ch.insign.cms.models.PageBlock;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Block class that extends PageBlock archetype to represent car page and enhance its with navigation
 * {@link http://play-cms.com/doc/cms/create-your-own-cms-block}
 */
@Entity
@Table(name = "car_inventory_page")
@DiscriminatorValue("CarInventoryPage")
public class CarInventoryPage extends PageBlock {

    private String dealerTitle;

    public String getDealerTitle() {
        return dealerTitle;
    }

    public void setDealerTitle(String dealerTitle) {
        this.dealerTitle = dealerTitle;
    }

}
