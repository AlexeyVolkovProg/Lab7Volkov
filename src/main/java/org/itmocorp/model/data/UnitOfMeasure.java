package org.itmocorp.model.data;

import java.io.Serializable;

public enum UnitOfMeasure implements Comparable<UnitOfMeasure>, Serializable {
        CENTIMETERS(0),
        METERS(1),
        SQUARE_METERS(2),
        PCS(3);

        private Integer size;
        UnitOfMeasure(Integer size){
                this.size = size;
        }

}
