package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Cart;
import uz.pdp.model.CartItem;
import uz.pdp.util.FileUtil;

import java.util.*;

public class CartService {
    private final String fileName = "orders.json";
    private List<Cart> orderList;
    private List<Cart> cartList;

    @SneakyThrows
    public CartService() {
        orderList = new ArrayList<>();
        cartList = new ArrayList<>();
        orderList = FileUtil.read(fileName, Cart.class);
    }

    @SneakyThrows
    private void saveCarts() {
        FileUtil.write(fileName, orderList);
    }

    public String addProductToCart(Cart currCart, CartItem cartItem) {
        for (Cart cart : cartList) {
            if (cart.getId().equals(currCart.getId())) {
                CartItem cartItem1 = hasCartItem(cart.getCartList(), cartItem);
                if (cartItem1 == null) {
                    cart.getCartList().add(cartItem);
                } else {
                    cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity());
                }
                return "Successful";
            }
        }
        createCart(currCart);
        currCart.getCartList().add(cartItem);
        return "Successful";
    }

    public CartItem hasCartItem(List<CartItem> cartItemList, CartItem cartItem) {
        for (CartItem item : cartItemList) {
            if (item.getProductId().equals(cartItem.getProductId())) {
                return item;
            }
        }
        return null;
    }

    public Cart getCartByCartId(UUID cartId) {
        for (Cart cart : cartList) {
            if (cart.getId().equals(cartId)) {
                return cart;
            }
        }
        return null;
    }

    public Cart getCartById(UUID id) {
        for (Cart cart : cartList) {
            if (cart.getId().equals(id)) {
                return cart;
            }
        }
        return null;
    }

    public String deletedCart(UUID cartId) {
        Cart cart = getCartById(cartId);
        if (cart == null) {
            return "not found cart";
        }
        cartList.remove(cart);
        return "Successful";
    }

    public void addCartToOrders(Cart cart) {
        orderList.add(cart);
        saveCarts();
        deletedCart(cart.getId());
    }

    public List<Cart> getOrdersByUserId(UUID userId) {
        List<Cart> carts = new ArrayList<>();
        for (Cart cart : orderList) {
            if (cart.getUserId().equals(userId)) {
                carts.add(cart);
            }
        }
        return carts;
    }

    public List<Cart> getAllOrders() {
        return orderList;
    }

    public void createCart(Cart cart) {
        cartList.add(cart);
    }

//    public String addProductToCart(UUID productId, UUID userId, UUID cartId, int quantity) {
//        Product product = ProductService.getProductById(productId);
//        if (product != null && product.isActive()) {
//            Cart newCart = new Cart(cartId, userId, productId, quantity);
//            List<Cart> carts = cartMapByCartId.get(cartId);
//            if (carts == null) {
//                carts = new ArrayList<>();
//                carts.add(newCart);
//                cartMapByCartId.put(cartId, carts);
//
//                Set<UUID> cartIds = cartMapByUserId.get(userId);
//                if (cartIds == null) {
//                    cartIds = new HashSet<>();
//                }
//                cartIds.add(cartId);
//                cartMapByUserId.put(userId, cartIds);
//
//                cartList.add(newCart);
//                saveCarts();
//                return "Successful \n";
//            }
//            for (Cart cart : carts) {
//                if (cart.getProductId().equals(productId)) {
//                    cart.setQuantity(cart.getQuantity() + quantity);
//                    saveCarts();
//                    return "Successful \n";
//                }
//            }
//            cartList.add(newCart);
//            carts.add(newCart);
//            saveCarts();
//            return "Successful \n";
//        }
//        return "not found product \n";
//    }
}