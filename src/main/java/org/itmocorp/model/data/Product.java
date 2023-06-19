package org.itmocorp.model.data;

import org.itmocorp.model.exceptions.RangeOutExceprion;
import org.itmocorp.model.exceptions.ValueIsMissing;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс Product, объектами которого заполняется коллекция
 *
 * @author Alexey Volkov P3113
 */
public class Product implements Comparable<Product>, Serializable {
    private static int idSetter = 0; //Начальное значение, для дальнейшей генерации id товара
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private final ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long price; //Поле может быть null, Значение поля должно быть больше 0
    private final static long MIN_PRICE_VALUE = 0L;
    private final static long MAX_PRICE_VALUE = Integer.MAX_VALUE;
    private Long manufactureCost; //Поле может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private Person owner; //Поле может быть null

    /**
     * Конструктор класса Product
     *
     * @param name            название товара
     * @param coordinates     расположение товара
     * @param price           цена товара
     * @param manufactureCost стоимость изготовления товара
     * @param unitOfMeasure   единицы измерения размера товара
     * @param owner           владелец товара
     */

    public Product(String name, Coordinates coordinates, Long price, Long manufactureCost, UnitOfMeasure unitOfMeasure, Person owner) {
        this.creationDate = ZonedDateTime.now();
        setId(idSetter++);
        setName(name);
        setCoordinates(coordinates);
        setPrice(price);
        setManufactureCost(manufactureCost);
        setUnitOfMeasure(unitOfMeasure);
        setOwner(owner);
    }

    /**
     * Дополнительный конструктор класса Product.
     * Используется в серверной части в методе receive.
     *
     * @param product
     */
    public Product(Product product){
        this.creationDate = ZonedDateTime.now();
        setId(idSetter++);
        setName(product.getName());
        setCoordinates(product.getCoordinates());
        setPrice(product.getPrice());
        setManufactureCost(product.getManufactureCost());
        setUnitOfMeasure(product.getUnitOfMeasure());
        setOwner(product.getOwner());
    }

    /**
     * Поле описывает айди товара
     * Значение поля должно быть больше 0,
     * Значение этого поля должно быть уникальным,
     * Значение этого поля должно генерироваться автоматически
     *
     * @param id айди товара
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Поле не может быть null,
     * Строка не может быть пустой,
     *
     * @param name название товара
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException();
        } else if(name.isEmpty()){
            throw new ValueIsMissing();
        }
        else {
            this.name = name;
        }
    }

    /**
     * Поле не может быть null
     *
     * @param coordinates координаты товара
     */

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new NullPointerException();
        } else {
            this.coordinates = coordinates;
        }
    }

    /**
     * Поле не может быть null
     * Поле должно быть больше нуля
     *
     * @param price цена товара
     */
    public void setPrice(Long price) throws NullPointerException {
        if (price == null) {
            throw new NullPointerException();
        }else if(price < 0){
            throw new RangeOutExceprion();
        }else{
            this.price = price;
        }

    }

    /**
     * Поле может быть null
     *
     * @param manufactureCost оказывает стоимость изготовления товара
     */
    public void setManufactureCost(Long manufactureCost) throws NullPointerException {
        if (manufactureCost != null) {
            this.manufactureCost = manufactureCost;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", manufactureCost=" + manufactureCost +
                ", unitOfMeasure=" + unitOfMeasure +
                ", owner=" + owner +
                '}';
    }

    /**
     * Поле не может быть null
     *
     * @param unitOfMeasure показывает единицы измерения размера товара
     */
    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        if (unitOfMeasure != null) {
            this.unitOfMeasure = unitOfMeasure;
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Поле может быть null
     *
     * @param owner показывает владельцв
     */

    public void setOwner(Person owner) {
        if (owner != null) {
            this.owner = owner;
        } else {
            throw new NullPointerException();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Long getPrice() {
        return price;
    }

    public Long getManufactureCost() {
        return manufactureCost;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Person getOwner() {
        return owner;
    }

    public static long getMinPriceValue(){
        return MIN_PRICE_VALUE;
    }
    public static long getMaxPriceValue(){
        return MAX_PRICE_VALUE;
    }

    @Override
    public int compareTo(Product anotherProduct) {
        return this.price.compareTo(anotherProduct.price);
    }
}