package crud.data.entity;

import io.vavr.control.Option;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "example_crud_car")
@NamedQueries(value = {
        @NamedQuery(name = "Car.findByRegistrationId", query = "SELECT c FROM Car c WHERE c.registrationId = :registrationId"),
        @NamedQuery(
                name = "Car.findByBrandIdAndSearchTerm",
                query = "SELECT c FROM Car c INNER JOIN c.brand b WHERE " +
                        "(c.model LIKE :searchTerm1 " +
                        "OR c.registrationId LIKE :searchTerm2)" +
                        "AND b.id = :brandId"),
        @NamedQuery(
                name = "Car.findBySearchTerm",
                query = "SELECT c FROM Car c WHERE " +
                        "c.model LIKE :searchTerm1 " +
                        "OR c.registrationId LIKE :searchTerm2"),
        @NamedQuery(
                name = "Car.findByBrandIdAndSearchTerm.count",
                query = "SELECT COUNT(c) FROM Car c INNER JOIN c.brand b WHERE " +
                        "(c.model LIKE :searchTerm1 " +
                        "OR c.registrationId LIKE :searchTerm2)" +
                        "AND b.id = :brandId"),
        @NamedQuery(
                name = "Car.findBySearchTerm.count",
                query = "SELECT COUNT(c) FROM Car c WHERE " +
                        "c.model LIKE :searchTerm1 " +
                        "OR c.registrationId LIKE :searchTerm2")
        }
)
public class Car {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Brand brand;

    @Constraints.Required
    @Constraints.MaxLength(value = 48)
    private String model;

    @Constraints.Required
    @Constraints.MaxLength(value = 10)
    @Column(unique = true)
    private String registrationId;

    @Constraints.Min(value = 0, message = "example.crud.car.price.error.message")
    private BigDecimal price;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date buyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Option<Brand> getBrand() {
        return Option.of(brand);
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

    public Option<BigDecimal> getPrice() {
        return Option.of(price);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Option<Date> getBuyDate() {
        return Option.of(buyDate);
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

}
