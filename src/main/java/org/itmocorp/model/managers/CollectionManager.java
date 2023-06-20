//package org.itmocorp.model.managers;
//
//import org.itmocorp.model.data.Product;
//import org.itmocorp.model.parsing.CSVReader;
//import org.itmocorp.model.parsing.CSVWriter;
//
//import java.util.Vector;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//public class CollectionManager {
//    private static CopyOnWriteArrayList<Product> Products = new CopyOnWriteArrayList<>();
//    private static String separator;
//    private static String filePath;
//    public CollectionManager(String filePath1, String fileSeparator) {
//        //CSVReader.csvReaderMethod(filePath1, fileSeparator);
//        separator = fileSeparator;
//        filePath = filePath1;
//    }
//
//    public static void addToVector(Product product){
//        Products.add(product);
//    }
//
//    public static void printVector(){
//        for (Product product : Products) {
//            System.out.println(product);
//        }
//    }
//
//    //public static void saveToFile(){
//        //CSVWriter.csvWriterMethod(filePath, separator);
//    //}
//
//    public static CopyOnWriteArrayList<Product> getProducts() {
//        return Products;
//    }
//}
