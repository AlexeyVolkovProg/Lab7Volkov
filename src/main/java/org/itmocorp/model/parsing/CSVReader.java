package org.itmocorp.model.parsing;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.itmocorp.model.managers.CollectionManager;
import org.itmocorp.model.data.Product;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    public static void csvReaderMethod(String filePath, String fileSeparator) {
        CSVParser parser = new CSVParserBuilder().withSeparator(fileSeparator.charAt(0)).build();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             com.opencsv.CSVReader reader1 = new CSVReaderBuilder(br).withCSVParser(parser).withSkipLines(1).build()) {
            String[] line;
            while (true) {
                line = reader1.readNext();
                if (line == null) {
                    break;
                }
                if (line.length != 9) {
                    System.out.println("Не удалось загрузить элемент. Строка имеет неверный формат");
                } else {
                    System.out.println("Строка была считана из файла, для дальнейшей проверки и преобразования в объект коллекции");
                    Product product = TransformToProduct.transformArguments(line);
                    if (product != null) {
                        CollectionManager.addToVector(product);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Неверное имя файла или доступ к файлу ограничен.");
            System.exit(0);
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
