package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Category;
import uz.pdp.model.FileUtil;
import uz.pdp.model.Product;

import java.util.*;

public class ProductService {
    private static final String fileJson = "product.json";
    private static List<Product> products;
    private static Map<UUID,Product> productMap;

    @SneakyThrows
    public ProductService() {
        products = FileUtil.read(fileJson, Product.class);
        productMap = new HashMap<>();

        for (Product p : products) {
            productMap.put(p.getId(), p);
        }
    }

    @SneakyThrows
    static private void saveProducts(){
        FileUtil.write(fileJson, products);
    }

    public List<Product> getProductsByCategoryId(UUID categoryId) {
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                productList.add(product);
            }
        }
        return productList;
    }

    static public void deletedProductsByCategoryId(UUID categoryId) {
        for (Product product : products) {
            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                product.setActive(false);
            }
        }
        saveProducts();
    }

    public static Product getProductById(UUID id){
        return productMap.get(id);
    }

    private boolean isProductByName(String name) {
        for (Product product : products) {
            if (product.isActive() && product.getProductName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String addProduct(Product product) {
        if (isProductByName(product.getProductName())) {
            return "Bunday product mavjud \n";
        }
        Category category = CategoryService.getCategoryById(product.getCategoryId());
        if (category != null && (category.getNodeType() == null || !category.getNodeType())) {
            category.setNodeType(false);
            products.add(product);
            productMap.put(product.getId(), product);
            saveProducts();
            return "Successful \n";
        }
        return "Not found category \n";
    }

    public String deletedProduct(UUID id) {
        Product product = productMap.get(id);
        if (product != null && product.isActive()) {
            product.setActive(false);
            saveProducts();
            return "Successful \n";
        }
        return "Not found product \n";
    }
}
