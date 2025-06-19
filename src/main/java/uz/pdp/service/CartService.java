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
        int price = ProductService.getProductById(cartItem.getProductId()).getPrice();
        int totalPrice = price * cartItem.getQuantity();
        for (Cart cart : cartList) {
            if (cart.getId().equals(currCart.getId())) {
                CartItem cartItem1 = hasCartItem(cart.getCartList(), cartItem);
                if (cartItem1 == null) {
                    cart.getCartList().add(cartItem);
                } else {
                    cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity());
                }
                cart.setTotalPrice(cart.getTotalPrice() + totalPrice);
                return "Successful";
            }
        }
        createCart(currCart);
        currCart.getCartList().add(cartItem);
        currCart.setTotalPrice(totalPrice);
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

}