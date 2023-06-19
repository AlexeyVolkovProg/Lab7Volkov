package org.itmocorp.model.parsing;

import org.itmocorp.model.data.Product;
import org.itmocorp.model.managers.CollectionManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class CSVWriter {
    public static void csvWriterMethod(String filePath, String fileSeparator) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            writer.write("name, coordinateX, coordinateY, price, manufactureCost, unitOfMeasure, personName, personBirthday, personPassportId\n");
            for (Product product1 : CollectionManager.getProducts()) {
                String[] product_string = {product1.getName(), String.valueOf(product1.getCoordinates().getX()),
                        String.valueOf(product1.getCoordinates().getY()), String.valueOf(product1.getPrice()), String.valueOf(product1.getManufactureCost()),
                        String.valueOf(product1.getUnitOfMeasure()), product1.getOwner().getName(), product1.getOwner().getBirthday(), product1.getOwner().getPassportID()};
                String result = String.join(fileSeparator, product_string);
                writer.write(result + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
