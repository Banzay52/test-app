package crud.data.form;

import play.data.validation.Constraints.*;

public class BrandForm {

    private long id;

    @Required
    @MaxLength(value = 48)
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
