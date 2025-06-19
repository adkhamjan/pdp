package uz.pdp;

import uz.pdp.model.*;
import uz.pdp.service.*;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static uz.pdp.enums.UserType.ADMIN;
import static uz.pdp.enums.UserType.USER;

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
                    System.out.print(" Passwordingizni kiriting:");
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
                    } else {
                        login(currUser);
                    }
                }
            }
        }
    }

    public static void login(User currUser) {
        if (currUser.getTypeUser().equals(ADMIN)) {
            int step = 1;
            while (step != 0) {
                System.out.println("1. Category    2.Product  0.Exit");
                step = scannerInt.nextInt();
                if (step == 1) {
                    setCategoryService(currUser);
                } else if (step == 2) {
                    setProductService(currUser);
                }
            }
        } else {
            Cart cart = new Cart(currUser.getId());
            int step = 10;
            while (step != 0) {
                System.out.println("""
                        1. Add Product to Cart
                        2. My cart
                        3. Deleted Cart
                        4. To Order
                        5. My Orders list
                        6. List all Orders
                        0. Exit
                        """);
                step = scannerInt.nextInt();
                switch (step) {
                    case 1 -> {
                        int step1 = 1;
                        while (step1 != 0) {
                            UUID productId = selectProduct(null);
                            if (productId == null) {
                                break;
                            }
                            System.out.println("Enter quantity");
                            int quantity = scannerInt.nextInt();
                            CartItem cartItem = new CartItem(cart.getId(), productId, quantity);
                            System.out.println(cartService.addProductToCart(cart, cartItem));
                            System.out.println("0.Back    1.Sotib olishni davom ettirish");
                            step1 = scannerInt.nextInt();
                        }
                    }
                    case 2 -> {
                        Cart cart1 = cartService.getCartByCartId(cart.getId());
                        if (cart1 == null) {
                            System.out.println("Sizda cart mavjud emas \n");
                            break;
                        }
                        List<CartItem> cartItems = cart1.getCartList();
                        for (CartItem cartItem : cartItems) {
                            String productName = ProductService.getProductById(cartItem.getProductId()).getProductName();
                            System.out.print(productName + ":" + cartItem.getQuantity() + "   ");
                        }
                        System.out.println();
                    }
                    case 3 -> {
                        System.out.println(cartService.deletedCart(cart.getId()));
                        cart = new Cart(currUser.getId());
                    }
                    case 4 -> {
                        Cart currCart = cartService.getCartByCartId(cart.getId());
                        if (currCart == null) {
                            System.out.println("Sizda cart mavjud emas \n");
                            break;
                        }
                        cartService.addCartToOrders(currCart);
                        cart = new Cart(currUser.getId());
                    }
                    case 5 -> {
                        List<Cart> userOrders = cartService.getOrdersByUserId(currUser.getId());
                        for (Cart order : userOrders) {
                            List<CartItem> cartItems = order.getCartList();
                            for (CartItem cartItem : cartItems) {
                                Product product = ProductService.getProductById(cartItem.getProductId());
                                String productName = product.getProductName();
                                System.out.print(productName + ":" + cartItem.getQuantity() + ", productActive:" + product.isActive()+";  ");
                            }
                            System.out.println();
                        }
                        System.out.println();
                    }
                    case 6 -> {
                        List<Cart> orders = cartService.getAllOrders();
                        for (Cart order : orders) {
                            List<CartItem> cartItems = order.getCartList();
                            for (CartItem cartItem : cartItems) {
                                Product product = ProductService.getProductById(cartItem.getProductId());
                                String productName = product.getProductName();
                                System.out.print(productName + ":" + cartItem.getQuantity() + ", productActive:" + product.isActive()+";  ");
                            }
                            System.out.println();
                        }
                        System.out.println();
                    }
                }
            }
        }
    }

    public static void setCategoryService(User currUser) {
        int step = 1;
        while (step != 0) {
            System.out.println("""
                    1. Add Category
                    2. Get ChildCategory By Id
                    3. Delete Category
                    4. All Category
                    0. Exit
                    """);
            step = scannerInt.nextInt();
            switch (step) {
                case 1 -> {
                    UUID parentId = enterParentCategory();
                    if (parentId == null) {
                        break;
                    }
                    Category category = new Category();
                    System.out.println("Kategoriya nomi: ");
                    category.setName(scannerStr.nextLine());
                    category.setCreatedById(currUser.getId());
                    if (String.valueOf(parentId).equals("ce72e6af-cdf1-4d40-a25e-62b5a9567c9e")) {
                        System.out.println(categoryService.addCategory(category));
                    } else {
                        System.out.println(categoryService.addCategory(category, parentId));
                    }
                }
                case 2 -> {
                    List<Category> categories = categoryService.getALLCategories();
                    for (Category category : categories) {
                        System.out.println(category);
                    }
                    System.out.println();
                    System.out.println(" Id kiriting");
                    UUID id = UUID.fromString(scannerStr.nextLine());
                    List<Category> childCategory = categoryService.getChildCategoryById(id);
                    for (Category category : childCategory) {
                        System.out.println(category);
                    }
                    System.out.println();
                }
                case 3 -> {
                    System.out.println(" Id kiriting");
                    UUID id = UUID.fromString(scannerStr.nextLine());
                    System.out.println(categoryService.deleted(id));
                }
                case 4 -> {
                    List<Category> categories = categoryService.getALLCategories();
                    for (Category category : categories) {
                        System.out.println(category);
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void setProductService(User currUser) {
        int step = 1;
        while (step != 0) {
            System.out.println("""
                    1. Add Product
                    2. Get Product By CategoryId
                    3. Delete Product
                    4. All Product
                    5. All Category
                    0. Exit
                    """);
            step = scannerInt.nextInt();
            switch (step) {
                case 1 -> {
                    UUID categoryId = enterCategoryProduct(null);
                    if (categoryId == null) {
                        break;
                    }
                    Product product = new Product();
                    System.out.println("Enter product name");
                    product.setProductName(scannerStr.nextLine());
                    System.out.println("Enter price");
                    product.setPrice(scannerInt.nextInt());
                    product.setCategoryId(categoryId);
                    System.out.println(productService.addProduct(product));
                }
                case 2 -> {
                    System.out.println("Enter CategoryId");
                    UUID categoryId = UUID.fromString(scannerStr.nextLine());
                    List<Product> productList = productService.getProductsByCategoryId(categoryId);
                    for (Product product : productList) {
                        System.out.println(product);
                    }
                    System.out.println();
                }
                case 3 -> {
                    UUID id = selectProduct(null);
                    if (id == null) {
                        break;
                    }
                    System.out.println(productService.deletedProduct(id));
                }
                case 4 -> {
                    List<Product> products = productService.getAllProducts();
                    for (Product product : products) {
                        System.out.println(product);
                    }
                    System.out.println();
                }
                case 5 -> {
                    List<Category> categories = categoryService.getALLCategories();
                    for (Category category : categories) {
                        System.out.println(category);
                    }
                    System.out.println();
                }
            }
        }
    }


    // to find parent
    public static UUID enterParentCategory() {
        List<Category> categories = categoryService.getParentCategories();
        for (Category category : categories) {
            System.out.println(category);
        }
        System.out.println();
        System.out.println("0.Back   1.Add   2.Enter");
        int step = scannerInt.nextInt();
        if (step == 0) {
            return null;
        }
        if (step == 1) {
            return UUID.fromString("ce72e6af-cdf1-4d40-a25e-62b5a9567c9e");
        }
        if (step == 2) {
            System.out.println("Enter Id");
            UUID id = UUID.fromString(scannerStr.nextLine());
            for (Category category : categories) {
                if (category.getId().equals(id)) {
                    return enterCategory(id);
                }
            }
            System.out.println("Not found category \n");
        }
        return enterParentCategory();
    }

    public static UUID enterCategory(UUID id) {
        if (id == null) {
            return enterParentCategory();
        }
        List<Category> childCategory = categoryService.getChildCategoryById(id);
        if (childCategory.isEmpty()) {
            return enterLastCategory(id);
        }
        Category category1 = CategoryService.getCategoryById(id);
        System.out.println(category1.getName());
        for (Category category : childCategory) {
            System.out.println(category);
        }
        System.out.println();
        System.out.println("0.Back   1.Add   2.Enter");
        int step = scannerInt.nextInt();
        if (step == 0) {
            return enterCategory(category1.getParentId());
        }
        if (step == 1) return id;
        if (step == 2) {
            System.out.println("Enter Id");
            id = UUID.fromString(scannerStr.nextLine());
            System.out.println();
            for (Category category : childCategory) {
                if (category.getId().equals(id)) {
                    return enterCategory(id);
                }
            }
            System.out.println("Not found category\n");
        }
        return enterCategory(id);
    }

    public static UUID enterLastCategory(UUID id) {
        Category category = CategoryService.getCategoryById(id);
        Category category1 = CategoryService.getCategoryById(id);
        System.out.println(category1.getName() +"\n");
        if (category.getNodeType() == null || category.getNodeType()){
            System.out.println("0.Back   1.Add");
            int step = scannerInt.nextInt();
            if (step == 0) {
                return enterCategory(category.getParentId());
            }
            if (step == 1) return id;
            return enterCategory(id);
        }
        System.out.println("0.Back");
        int step = scannerInt.nextInt();
        if (step == 0) {
            return enterCategory(category.getParentId());
        }
        return enterCategory(id);
    }

    public static UUID enterCategoryProduct(UUID id) {
        List<Category> categories;
        if (id == null) {
            categories = categoryService.getParentCategories();
            for (Category category : categories) {
                System.out.println(category);
            }
            System.out.println();
        } else {
            categories = categoryService.getChildCategoryById(id);
            for (Category category : categories) {
                System.out.println(category);
            }
            System.out.println();
        }

        int step;
        if (categories.isEmpty()) {
            System.out.println();
            Category category = CategoryService.getCategoryById(id);
            List<Product> productList = productService.getProductsByCategoryId(category.getId());
            System.out.println(category.getName());
            for (Product product : productList) {
                System.out.println(product);
            }
            System.out.println();
            if (category.getNodeType() == null || !category.getNodeType()){
                System.out.println("0.Back   1.Add");
                step = scannerInt.nextInt();
                if (step == 0) {
                    return enterCategoryProduct(category.getParentId());
                }
                if (step == 1) return id;
                return enterCategoryProduct(id);
            }
            System.out.println("0.Back");
            step = scannerInt.nextInt();
            if (step == 0) {
                return enterCategoryProduct(category.getParentId());
            }
            return enterCategoryProduct(id);
        }
        System.out.println("0.Back   1.Enter");
        step = scannerInt.nextInt();
        if (step == 0) {
            if (id == null) {
                return null;
            }
            Category category = CategoryService.getCategoryById(id);
            return enterCategoryProduct(category.getParentId());
        }
        if (step == 1) {
            System.out.println("Enter Id");
            id = UUID.fromString(scannerStr.nextLine());
            for (Category category : categories) {
                if (category.getId().equals(id)) {
                    return enterCategoryProduct(id);
                }
            }
            System.out.println("Not found category\n");
        }
        return enterCategory(id);
    }

    public static UUID selectProduct(UUID id) {
        List<Category> categories;
        if (id == null) {
            categories = categoryService.getParentCategories();
            for (Category category : categories) {
                System.out.println(category);
            }
            System.out.println();
        } else {
            categories = categoryService.getChildCategoryById(id);
            for (Category category : categories) {
                System.out.println(category);
            }
            System.out.println();
        }

        int step;
        if (categories.isEmpty()) {
            Category category = CategoryService.getCategoryById(id);
            List<Product> productList = productService.getProductsByCategoryId(category.getId());
            for (Product product : productList) {
                System.out.println(product);
            }
            System.out.println();
            if (productList.isEmpty()) {
                System.out.println("0.Back");
                step = scannerInt.nextInt();
                if (step == 0) {
                    return selectProduct(category.getParentId());
                }
                return selectProduct(id);
            }
            System.out.println("0.Back   1.Choice");
            step = scannerInt.nextInt();
            if (step == 0) {
                return selectProduct(category.getParentId());
            }
            if (step == 1) {
                System.out.println("Enter product Id");
                UUID productId = UUID.fromString(scannerStr.nextLine());
                for (Product product : productList) {
                    if (product.getId().equals(productId)) {
                        return productId;
                    }
                }
                System.out.println("Not found productId");
                return selectProduct(id);
            }
        }
        System.out.println("0.Back   1.Enter");
        step = scannerInt.nextInt();
        if (step == 0) {
            if (id == null) {
                return null;
            }
            Category category = CategoryService.getCategoryById(id);
            return selectProduct(category.getParentId());
        }
        if (step == 1) {
            System.out.println("Enter Id");
            id = UUID.fromString(scannerStr.nextLine());
            for (Category category : categories) {
                if (category.getId().equals(id)) {
                    return selectProduct(id);
                }
            }
            System.out.println("Not found category");
        }
        return selectProduct(id);
    }
}