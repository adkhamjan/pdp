package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.BaseModel;
import uz.pdp.model.Category;
import uz.pdp.util.FileUtil;
import uz.pdp.model.Product;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService {
    private static final String fileName = "product.json";
    private static List<Product> products;

    @SneakyThrows
    public ProductService() {
        products = FileUtil.read(fileName, Product.class);
    }

    @SneakyThrows
    static private void saveProducts() {
        FileUtil.write(fileName, products);
    }

    public List<Product> getProductsByCategoryId(UUID categoryId) {
//        List<Product> productList = new ArrayList<>();
//        for (Product product : products) {
//            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
//                productList.add(product);
//            }
//        }
//        return productList;
        return products.stream().filter(product -> product.isActive() && product.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    static public void deletedProductsByCategoryId(UUID categoryId) {
//        for (Product product : products) {
//            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
//                product.setActive(false);
//            }
//        }
        products.stream().filter(product -> product.isActive() && product.getCategoryId().equals(categoryId))
                .forEach(product -> product.setActive(false));
        saveProducts();
    }

    public void updateProduct(Product product, UUID productId) {
        Optional<Product> optionalProduct = getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product1 = optionalProduct.get();
            if (product1.isActive()) {
                product1.setProductName(product.getProductName());
                product1.setPrice(product.getPrice());
                product1.setUpdateDate(LocalDateTime.now());
                saveProducts();
            }
        }
    }

    public static Optional<Product> getProductById(UUID id) {
        return products.stream().filter(product -> product.getId().equals(id))
                .findFirst();
    }

    private boolean isProductByName(String name) {
//        for (Product product : products) {
//            if (product.isActive() && product.getProductName().equals(name)) {
//                return true;
//            }
//        }
//        return false;
        return products.stream().anyMatch(product -> product.isActive() && product.getProductName().equals(name));
    }

    public String addProduct(Product product) {
        if (isProductByName(product.getProductName())) {
            return "Bunday product mavjud \n";
        }
        Optional<Category> optionalCategory = CategoryService.getCategoryById(product.getCategoryId());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            if (category.getNodeType() == null || !category.getNodeType()) {
                category.setNodeType(false);
                products.add(product);
                saveProducts();
                CategoryService.saveCategories();
                return "Successful \n";
            }
        }
        return "Not found category \n";
    }

    public String deletedProduct(UUID id) {
        Optional<Product> optionalProduct = getProductById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.isActive()) {
                product.setActive(false);
                saveProducts();
                return "Successful \n";
            }
        }
        return "Not found product \n";
    }

    public List<Product> getAllProducts() {
//        List<Product> activeProduct = new ArrayList<>();
//        for (Product product : products) {
//            if(product.isActive()) {
//                activeProduct.add(product);
//            }
//        }
//        return activeProduct;
        return products.stream().filter(BaseModel::isActive)
                .collect(Collectors.toList());
    }
}
