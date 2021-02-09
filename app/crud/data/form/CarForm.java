package crud.data.form;

import crud.data.entity.Brand;
import crud.validator.CarFormConstraints;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

@Constraints.Validate
@CarFormConstraints.UniqueRegistrationId
public class CarForm implements Constraints.Validatable<ValidationError> {

    private Long id;

    @Constraints.Required
    private Brand brand;

    @Constraints.Required
    @Constraints.MaxLength(value = 48)
    private String model;

    @Constraints.Required
    @Constraints.MaxLength(value = 10)
    private String registrationId;

    @Constraints.Required
    @Constraints.Min(value = 0, message = "example.crud.car.price.error.message")
    @Digits(integer = 10, fraction = 2, message = "example.crud.car.price.error.message")
    private BigDecimal price;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm")
    private Date buyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    @Override
    public ValidationError validate() {
        if (buyDate != null && buyDate.before(new Date(0))) {
            return new ValidationError("buyDate", "example.crud.car.date.error");
        }

        return null;
    }
}
