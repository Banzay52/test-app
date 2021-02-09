package blocks.teaserblock;

import blocks.teaserblock.html.teaserEdit;
import blocks.teaserblock.html.teaserShow;
import ch.insign.cms.blocks.BlockWrapper;
import ch.insign.cms.blocks.GenericBlockComponent;
import ch.insign.cms.repositories.BlockRepository;
import ch.insign.cms.views.admin.utils.BackUrl;
import ch.insign.cms.views.html._blockBase;
import ch.insign.commons.db.SmartFormFactory;
import play.data.Form;
import play.libs.F;
import play.mvc.Http;
import play.twirl.api.Html;

import javax.inject.Inject;

public class TeaserBlockComponent extends GenericBlockComponent<TeaserBlock> implements BlockWrapper<TeaserBlock> {

    private final SmartFormFactory formFactory;
    private final BlockRepository<TeaserBlock> blockRepository;
    private final _blockBase blockBase;

    @Inject
    public TeaserBlockComponent(SmartFormFactory formFactory,
                                BlockRepository<TeaserBlock> blockRepository,
                                _blockBase blockBase) {
        this.formFactory = formFactory;
        this.blockRepository = blockRepository;
        this.blockBase = blockBase;
    }

    @Override
    public boolean supports(String blockName) {
        return TeaserBlock.class.getName().equals(blockName);
    }

    @Override
    public Html display(Http.Request request, TeaserBlock block) {
        return teaserShow.render(block, request);
    }

    @Override
    public Html edit(Http.Request request, TeaserBlock block) {
        Form<TeaserBlock> form = formFactory.form(TeaserBlock.class).fill(block);

        return teaserEdit.render(block, form, BackUrl.get(request), request);
    }

    @Override
    public F.Either<Html, TeaserBlock> save(Http.Request request, TeaserBlock block) {
        Form<TeaserBlock> editForm = formFactory.form(TeaserBlock.class)
                .fill(block)
                .bindFromRequest(request);

        if (editForm.hasErrors()) {
            return F.Either.Left(teaserEdit.render(block, editForm, BackUrl.get(request), request));
        }

        TeaserBlock updatedBlock = editForm.get();
        return F.Either.Right(blockRepository.save(updatedBlock));
    }

    @Override
    public Html wrapContent(Http.Request request, Html content, TeaserBlock block) {
        return blockBase.render(
                block,
                false,
                true,
                true,
                false,
                "Content",
                "red",
                false,
                "pcms-horizontal",
                content,
                request
        );
    }

}
