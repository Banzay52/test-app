package blocks.teaserblock;

import ch.insign.cms.models.ContentBlock;
import ch.insign.commons.db.MString;
import play.mvc.Http;
import play.twirl.api.Html;
import play.data.Form;

import javax.persistence.*;

@Entity
@Table(name = "block_teaser")
@DiscriminatorValue("TeaserContentBlock")
public class TeaserBlock extends ContentBlock {

    @MString.MStringRequired
    @OneToOne(cascade= CascadeType.ALL)
    private MString subtitle = new MString();

    @MString.MStringRequired
    @OneToOne(cascade= CascadeType.ALL)
    private MString logoUrl = new MString();

    @MString.MStringRequired
    @OneToOne(cascade= CascadeType.ALL)
    private MString linkText = new MString();

    @MString.MStringRequired
    @OneToOne(cascade= CascadeType.ALL)
    private MString linkUrl = new MString();

    public MString getLogoUrl() {
        return this.logoUrl;
    }

    public void setLogoUrl(MString logoUrl) {
        this.logoUrl = logoUrl;
    }

    public MString getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(MString subtitle) {
        this.subtitle = subtitle;
    }

    public MString getLinkText() {
        return linkText;
    }

    public void setLinkText(MString linkText) {
        this.linkText = linkText;
    }

    public MString getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(MString linkUrl) {
        this.linkUrl = linkUrl;
    }

}
