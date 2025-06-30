package uz.pdp;

import uz.pdp.model.*;
import uz.pdp.service.*;

import java.util.*;


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
                    System.out.print(" enter name:");
                    String name = scannerStr.nextLine();
                    System.out.print(" Username kiriting:");
                    String userName = scannerStr.nextLine();
                    System.out.print(" Passwordingizni kiriting:");
                    String password = scannerStr.nextLine();
                    System.out.println(userService.add(new User(name, userName, password, USER)));
                }
                case 2 -> {
                    System.out.print("usernameni kiriting:");
                    String username = scannerStr.nextLine();
                    System.out.print("password kiriting:");
                    String password = scannerStr.nextLine();
                    Optional<User> optionalUser = userService.login(username, password);
                    if (optionalUser.isEmpty()) {
                        System.out.println("Parol yoki Username xato qayta kiriting !!! ");
                    } else {
                        login(optionalUser.get());
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
            userMenu(currUser);
        }
    }

    public static void userMenu(User currUser) {
        UUID cartId = UUID.randomUUID();
        int step = 10;
        while (step != 0) {
            System.out.println("""
                    1. Add Product to Cart
                    2. My cart
                    3. Deleted Cart
                    4. To Order
                    5. My Orders list
                    6. List all Orders
                    7. Update User
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
                        CartItem cartItem = new CartItem(cartId, productId, quantity);
                        System.out.println(cartService.addProductToCart(cartItem, currUser));
                        System.out.println("0.Back    1.Sotib olishni davom ettirish");
                        step1 = scannerInt.nextInt();
                    }
                }
                case 2 -> {
                    Cart cart1 = cartService.getCartByCartId(cartId);
                    if (cart1 == null) {
                        System.out.println("Sizda cart mavjud emas \n");
                        break;
                    }
                    List<CartItem> cartItems = cart1.getCartItemList();
                    System.out.print("Total price:" + cart1.getTotalPrice() + " ");
                    for (CartItem cartItem : cartItems) {
                        String productName = "Yo'q";
                        Optional<Product> optionalProduct = ProductService.getProductById(cartItem.getProductId());
                        if (optionalProduct.isPresent()) {
                            productName = optionalProduct.get().getProductName();
                        }
                        System.out.print(productName + ":" + cartItem.getQuantity() + "   ");
                    }
                    System.out.println();
                }
                case 3 -> {
                    System.out.println(cartService.deletedCart(cartId));
                    cartId = UUID.randomUUID();
                }
                case 4 -> {
                    Cart currCart = cartService.getCartByCartId(cartId);
                    if (currCart == null) {
                        System.out.println("Sizda cart mavjud emas \n");
                        break;
                    }
                    cartService.addCartToOrders(currCart);
                    cartId = UUID.randomUUID();
                }
                case 5 -> {
                    List<Cart> orders = cartService.getOrdersByUserId(currUser.getId());
                    showCart(orders, currUser);
                }
                case 6 -> {
                    List<Cart> orders = cartService.getAllOrders();
                    showCart(orders, currUser);
                }
                case 7 -> {
                    User user = new User();
                    System.out.print("Enter name : ");
                    user.setName(scannerStr.nextLine());
                    System.out.print("Enter password");
                    user.setPassword(scannerStr.nextLine());
                    userService.updateUser(user, currUser.getId());
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
                    5. Update Category
                    0. Exit
                    """);
            step = scannerInt.nextInt();
            switch (step) {
                case 1 -> {
                    UUID parentId = chooseCategory(null, false);
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
                    UUID categoryId = chooseCategory(null, true);
                    if (categoryId == null) {
                        break;
                    }
                    System.out.println(categoryService.deleted(categoryId));
                }
                case 4 -> {
                    List<Category> categories = categoryService.getALLCategories();
                    for (Category category : categories) {
                        System.out.println(category);
                    }
                    System.out.println();
                }
                case 5 -> {
                    UUID categoryId = chooseCategory(null, true);
                    if (categoryId == null) {
                        break;
                    }

                    Category category = new Category();
                    System.out.print("Enter Name : ");
                    category.setName(scannerStr.nextLine());
                    categoryService.updateCategory(category, categoryId, currUser.getId());
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
                    6. Update Product
                    0. Exit
                    """);
            step = scannerInt.nextInt();
            switch (step) {
                case 1 -> {
                    UUID categoryId = selectCategoryForProduct(null);
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
                    UUID productId = selectProduct(null);
                    if (productId == null) {
                        break;
                    }
                    System.out.println(productService.deletedProduct(productId));
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
                case 6 -> {
                    UUID productId = selectProduct(null);
                    if (productId == null) {
                        break;
                    }
                    Product product = new Product();
                    System.out.print("Enter Product name : ");
                    product.setProductName(scannerStr.nextLine());
                    System.out.println("Enter Product price");
                    product.setPrice(scannerInt.nextInt());
                    productService.updateProduct(product, productId);
                }
            }
        }
    }

    public static UUID enterLastCategory(Category category, boolean isDelete) {
        while (true) {
            System.out.println();
            if (category != null) {
                System.out.println(category.getName());
            }
            if (category == null || category.getNodeType() == null || category.getNodeType() || isDelete) {
                System.out.println("0.Back   1.Choose");
                int step = scannerInt.nextInt();
                if (step == 0) {
                    return (category != null) ? category.getParentId() : null;
                }
                if (step == 1)
                    return (category != null) ? category.getId() : UUID.fromString("ce72e6af-cdf1-4d40-a25e-62b5a9567c9e");
            } else {
                System.out.println("0.Back");
                int step = scannerInt.nextInt();
                if (step == 0) {
                    return category.getParentId();
                }
            }
        }
    }

    public static UUID chooseCategory(UUID id, boolean isDelete) {
        while (true) {
            List<Category> childCategories;
            Category category;
            if (id == null) {
                childCategories = categoryService.getParentCategories();
                category = null;
            } else {
                childCategories = categoryService.getChildCategoryById(id);
                Optional<Category> optionalCategory = CategoryService.getCategoryById(id);
                if (optionalCategory.isPresent()) {
                    category = optionalCategory.get();
                }
                else {
                    throw new RuntimeException();
                }
            }

            if (childCategories.isEmpty()) {
                UUID id1 = enterLastCategory(category, isDelete);
                if (id == null) return id1;
                if (id.equals(id1)) return id;
                id = id1;
            } else {
                showChildCategories(childCategories, category);
                System.out.println("0.Back   1.Choose   2.Enter");
                int step = scannerInt.nextInt();
                if (step == 0) {
                    if (category == null) return null;
                    id = category.getParentId();
                } else if (step == 1) {
                    if (id == null) return UUID.fromString("ce72e6af-cdf1-4d40-a25e-62b5a9567c9e");
                    return id;
                } else if (step == 2) {
                    System.out.println("Enter number");
                    int temp = scannerInt.nextInt();
                    if (temp >= 1 && temp <= childCategories.size()) {
                        id = childCategories.get(temp - 1).getId();
                    } else {
                        System.out.println("Not found category\n");
                    }
                } else System.out.println("Not found number\n");
            }
        }
    }

    public static <T> void showChildCategories(List<T> t, Category parentCategory) {
        if (parentCategory != null) {
            System.out.println(parentCategory.getName());
        }
        int i = 1;
        for (T item : t) {
            System.out.println(i++ + ". " + item);
        }
        System.out.println();
    }

    public static List<Product> showProducts(Category parentCategory, boolean isNumber) {
        List<Product> productList = productService.getProductsByCategoryId(parentCategory.getId());
        int i = 1;
        System.out.println(parentCategory.getName());
        for (Product product : productList) {
            if (isNumber) {
                System.out.println(i++ + ". " + product);
            } else System.out.println(product);
        }
        System.out.println();
        return productList;
    }

    public static UUID enterCategory(Category category, List<Category> childCategories) {
        while (true) {
            showChildCategories(childCategories, category);
            System.out.println("0. Back   \nEnter number");
            int temp = scannerInt.nextInt();
            if (temp == 0) {
                return (category != null) ? category.getParentId() : null;
            }
            if (temp >= 1 && temp <= childCategories.size()) {
                return childCategories.get(temp - 1).getId();
            }
            System.out.println("Not found category\n");
        }
    }

    public static UUID selectCategoryForProduct(UUID id) {
        while (true) {
            List<Category> childCategories;
            Category category;
            if (id == null) {
                childCategories = categoryService.getParentCategories();
                category = null;
            } else {
                childCategories = categoryService.getChildCategoryById(id);
                Optional<Category> optionalCategory = CategoryService.getCategoryById(id);
                if (optionalCategory.isPresent()) {
                    category = optionalCategory.get();
                }
                else {
                    throw new RuntimeException();
                }
            }

            if (childCategories.isEmpty()) {
                showProducts(Objects.requireNonNull(category), false);
                if (category.getNodeType() == null || !category.getNodeType()) {
                    System.out.println("0. Back   1. Add");
                    int step = scannerInt.nextInt();
                    if (step == 0) {
                        id = category.getParentId();
                    } else if (step == 1) {
                        return id;
                    }
                } else {
                    System.out.println("0. Back");
                    int step = scannerInt.nextInt();
                    if (step == 0) {
                        id = category.getParentId();
                    }
                }
            } else {
                UUID id1 = enterCategory(category, childCategories);
                if (id == null && id1 == null) return null;
                id = id1;
            }
        }
    }

    public static UUID chooseProduct(Category category) {
        int temp;
        while (true) {
            List<Product> productList = showProducts(category, true);
            System.out.println("0.Back \nEnter number");
            temp = scannerInt.nextInt();
            if (temp == 0) {
                return category.getParentId();
            } else if (temp >= 1 && temp <= productList.size()) {
                return productList.get(temp - 1).getId();
            } else System.out.println("Not found category\n");
        }
    }

    public static UUID selectProduct(UUID id) {
        while (true) {
            List<Category> categories;
            Category category;
            if (id == null) {
                categories = categoryService.getParentCategories();
                category = null;
            } else {
                categories = categoryService.getChildCategoryById(id);
                Optional<Category> optionalCategory = CategoryService.getCategoryById(id);
                if (optionalCategory.isPresent()) {
                    category = optionalCategory.get();
                }
                else {
                    throw new RuntimeException();
                }
            }

            if (categories.isEmpty()) {
                UUID id1 = chooseProduct(category);
                if (id1 == null) {
                    id = null;
                } else if (category != null && category.getParentId().equals(id1)) {
                    id = id1;
                }
            } else {
                UUID id1 = enterCategory(category, categories);
                if (id == null && id1 == null) return null;
                id = id1;
            }
        }
    }

    public static void showCart(List<Cart> orders, User currUser) {
        for (Cart order : orders) {
            List<CartItem> cartItems = order.getCartItemList();
            System.out.print("Total price:" + order.getTotalPrice() + " ");
            for (CartItem cartItem : cartItems) {
                Product product;
                Optional<Product> optionalProduct = ProductService.getProductById(cartItem.getProductId());
                if (optionalProduct.isPresent()) {
                    product = optionalProduct.get();
                } else throw new RuntimeException();
                System.out.print(product.getProductName() + ":" + cartItem.getQuantity() + ", productActive:" + product.isActive() + ";  ");
            }
            System.out.println();
        }
        System.out.println();
    }
}