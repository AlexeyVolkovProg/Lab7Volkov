package org.itmocorp.model.data;

import org.itmocorp.model.exceptions.RangeOutExceprion;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private Long x; //  Максимальное значение поля: 116, Поле не может быть null
    private static final Long MAX_X_VALUE = 116L;
    private static final Long MIN_X_VALUE = Long.MIN_VALUE;
    private Integer y; // Максимальное значение поля: 502, Поле не может быть null
    private static final int MAX_Y_VALUE = 502;
    private static final int MIN_Y_VALUE = Integer.MIN_VALUE;
    public Coordinates(long x, int y) {
        setX(x);
        setY(y);
    }

    public void setX(Long x) {
        if (x == null)
        {
            throw new NullPointerException();
        } else if (x > MAX_X_VALUE) {
            throw new RangeOutExceprion();
        } else{
            this.x = x;
        }
    }

    public void setY(Integer y) {
        if (y == null)
        {
            throw new NullPointerException();
        } else if (y > MAX_Y_VALUE) {
            throw new RangeOutExceprion();
        } else{
            this.y = y;
        }
    }

    public Long getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Long getMAX_X_VALUE() {
        return MAX_X_VALUE;
    }

    public static Long getMIN_X_VALUE() {
        return MIN_X_VALUE;
    }

    public static int getMAX_Y_VALUE() {
        return MAX_Y_VALUE;
    }

    public static int getMIN_Y_VALUE() {
        return MIN_Y_VALUE;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
