package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Cart;
import uz.pdp.model.CartItem;
import uz.pdp.model.User;
import uz.pdp.util.FileUtil;

import java.util.*;

public class CartService {
    private final String fileName = "orders.json";
    private List<Cart> orderList;
    private final List<Cart> cartList;

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

    public String addProductToCart(CartItem cartItem, User user) {
        int price = priceCalculation(cartItem);
        for (Cart cart : cartList) {
            if (cart.getId().equals(cartItem.getCartId())) {
                CartItem cartItem1 = hasCartItem(cart.getCartItemList(), cartItem);
                if (cartItem1 == null) {
                    cart.getCartItemList().add(cartItem);
                } else {
                    cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity());
                }
                cart.setTotalPrice(cart.getTotalPrice() + price);
                return "Successful";
            }
        }
        Cart currCart = createCart(cartItem.getCartId(), user);
        currCart.getCartItemList().add(cartItem);
        currCart.setTotalPrice(price);
        return "Successful";
    }

    private int priceCalculation(CartItem cartItem) {
        int price = ProductService.getProductById(cartItem.getProductId()).getPrice();
        return price * cartItem.getQuantity();
    }

    private CartItem hasCartItem(List<CartItem> cartItemList, CartItem cartItem) {
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

    public Cart createCart(UUID cartId, User user) {
        Cart cart = new Cart(user.getId(), cartId);
        cartList.add(cart);
        return cart;
    }
}