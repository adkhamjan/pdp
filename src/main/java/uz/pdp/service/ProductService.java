package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Category;
import uz.pdp.util.FileUtil;
import uz.pdp.model.Product;

import java.time.LocalDateTime;
import java.util.*;

public class ProductService {
    private static final String fileName = "product.json";
    private static List<Product> products;
    private static Map<UUID,Product> productMap;

    @SneakyThrows
    public ProductService() {
        products = FileUtil.read(fileName, Product.class);
        productMap = new HashMap<>();

        for (Product p : products) {
            productMap.put(p.getId(), p);
        }
    }

    @SneakyThrows
    static private void saveProducts(){
        FileUtil.write(fileName, products);
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

    public void updateProduct(Product product, UUID productId) {
        Product product1 = getProductById(productId);
        if (product1 != null && product1.isActive()) {
            product1.setProductName(product.getProductName());
            product1.setPrice(product.getPrice());
            product1.setUpdateDate(LocalDateTime.now());
            saveProducts();
        }
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
            CategoryService.saveCategories();
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

    public List<Product> getAllProducts() {
        List<Product> activeProduct = new ArrayList<>();
        for (Product product : products) {
            if(product.isActive()) {
                activeProduct.add(product);
            }
        }
        return activeProduct;
    }
}
