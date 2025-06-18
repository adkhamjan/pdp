package uz.pdp;

import uz.pdp.model.*;
import uz.pdp.service.*;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static uz.pdp.Enums.UserType.ADMIN;
import static uz.pdp.Enums.UserType.USER;

public class Main {
    static Scanner scannerStr = new Scanner(System.in);
    static Scanner scannerInt = new Scanner(System.in);

    static UserService userService = new UserService();
    static ProductService productService = new ProductService();
    static CartService cartService = new CartService();
    static CategoryService categoryService = new CategoryService();

    public static void main(String[] args) {
        int step = 10;
        while (step != 0) {
            System.out.println("1.Registor   2.Login    0.Exit");
            step = scannerInt.nextInt();
            switch (step) {
                case 1 -> {
                    System.out.print(" Ismingizni kiriting:");
                    String name = scannerStr.nextLine();
                    System.out.print(" Username kiriting:");
                    String userName = scannerStr.nextLine();
                    System.out.print(" enter password:");
                    String password = scannerStr.nextLine();
                    userService.add(new User(name, userName, password, USER));
                }
                case 2 -> {
                    System.out.print(" usernameni kiriting:");
                    String username = scannerStr.nextLine();
                    System.out.print(" password kiriting:");
                    String password = scannerStr.nextLine();
                    User currUser = userService.login(username, password);
                    if (currUser == null) {
                        System.out.println("Parol yoki Username xato qayta kiriting !!! ");
                    }else{
                        login(currUser);
                    }
                }
            }
        }
    }

    public static void login(User currUser) {
        if (currUser.getTypeUser().equals(ADMIN)){
            int step = 10;
            while (step != 0) {
                System.out.println("""
                        ========== ASOSIY MENYU ==========
                               1. Kategoriya qo‘shish
                               2. ID bo‘yicha ichki (quyi) kategoriyani ko‘rish
                               3. Kategoriyani o‘chirish
                               4. Mahsulot qo‘shish
                               5. Kategoriya ID bo‘yicha mahsulotlarni ko‘rish
                               6. Mahsulotni o‘chirish
                               0. Chiqish
                               ==================================
                               Iltimos, amal raqamini tanlang:
                        
                        """);
                step = scannerInt.nextInt();
                switch (step) {
                    case 1 -> {
                        List<Category> categories = categoryService.getALLCategories();
                        for (Category category : categories) {
                            System.out.println(category);
                        }
                        System.out.println("""
                                Qaysi kategoriyaga qo'shmoqchisiz ?
                                Id kiriting :
                                Bosh kategoriya uchun : 1
                                """);
                        String id = scannerStr.nextLine();
                        Category category = new Category();
                        System.out.println("Kategoriya nomi: ");
                        String name = scannerStr.nextLine();
                        category.setName(name);
                        category.setCreatedById(currUser.getId());
                        try {
                            int i = Integer.parseInt(id);
                            if (i == 1) {
                                System.out.println(categoryService.addCategory(category));
                            }
                        }
                        catch (Exception e) {
                            UUID uuid = UUID.fromString(id);
                            System.out.println(categoryService.addCategory(category, uuid));
                        }
                    }
                    case 2 -> {
                        List<Category> categories = categoryService.getALLCategories();
                        for (Category category : categories) {
                            System.out.println(category);
                        }
                        System.out.println(" Id kiriting");
                        UUID id = UUID.fromString(scannerStr.nextLine());
                        List<Category> childCategory = categoryService.getChildCategoryById(id);
                        for (Category category : childCategory) {
                            System.out.println(category);
                        }
                    }
                    case 3 -> {
                        System.out.println(" Id kiriting");
                        UUID id = UUID.fromString(scannerStr.nextLine());
                        System.out.println(categoryService.deleted(id));
                    }
                    case 4 -> {
                        System.out.println("Enter CategoryId");
                        UUID categoryId = UUID.fromString(scannerStr.nextLine());
                        System.out.println("Enter Product Name");
                        String productName = scannerStr.nextLine();
                        System.out.println("Enter Product price");
                        int price = scannerInt.nextInt();
                        Product product = new Product(categoryId, productName, price);
                        System.out.println(productService.addProduct(product));
                    }
                    case 5 -> {
                        System.out.println("Enter CategoryId");
                        UUID categoryId = UUID.fromString(scannerStr.nextLine());
                        List<Product> productList = productService.getProductsByCategoryId(categoryId);
                        for (Product product : productList) {
                            System.out.println(product);
                        }
                        System.out.println();
                    }
                    case 6 -> {
                        System.out.println("Enter ProductId");
                        UUID productId = UUID.fromString(scannerStr.nextLine());
                        System.out.println(productService.deletedProduct(productId));
                    }
                }
            }
        } else {
            UUID cartId = null;
            int step = 10;
            while (step != 0) {
                System.out.println("""
                        1. cart yaratish
                        2. Add Product to Cart
                        3. Get My Carts
                        4. Get all Carts
                        0. chiqish
                        """);
                step = scannerInt.nextInt();
                switch (step) {
                    case 1 -> {
                        cartId = cartService.createCart();
                        System.out.println("Ajoyib \n");
                    }
                    case 2 -> {
                        if (cartId != null) {
                            System.out.println("Enter ProductId");
                            UUID productId = UUID.fromString(scannerStr.nextLine());
                            System.out.println("Enter quantity");
                            int quantity = scannerInt.nextInt();
                            System.out.println(cartService.addProductToCart(productId, currUser.getId(), cartId, quantity));
                        }
                        else {
                            System.out.println("Not found cart! create cart");
                        }
                    }
                    case 3 -> {
                        List<List<Cart>> cartlist = cartService.getCartByUserId(currUser.getId());
                        for (List<Cart> carts : cartlist) {
                            for (Cart cart : carts) {
                                Product product = ProductService.getProductById(cart.getProductId());
                                String productName = product.getProductName();
                                System.out.print(productName + ":" + cart.getQuantity() + ", productActive:" + product.isActive() + "; ");
                            }
                            System.out.println();
                        }
                    }
                    case 4 -> {
                        List<Cart> carts = cartService.getAllCarts();
                        for (Cart cart : carts) {
                            boolean productActive = ProductService.getProductById(cart.getProductId()).isActive();
                            String productName = ProductService.getProductById(cart.getProductId()).getProductName();
                            System.out.println(productName + ":" + cart.getQuantity() + ", productActive:" + productActive + ", "+ cart);
                        }
                        System.out.println();
                    }
                }
            }
        }
    }
}