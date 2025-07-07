package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Cart;
import uz.pdp.model.CartItem;
import uz.pdp.model.Product;
import uz.pdp.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CartService {
    private final String orderFileName = "orders.json";
    private final String cartFileName = "carts.json";
    private List<Cart> orderList;
    private List<Cart> cartList;

    @SneakyThrows
    public CartService() {
        orderList = new ArrayList<>();
        cartList = new ArrayList<>();
        orderList = FileUtil.read(orderFileName, Cart.class);
        cartList = FileUtil.read(cartFileName, Cart.class);
    }

    @SneakyThrows
    private void saveCarts() {
        FileUtil.write(cartFileName, cartList);
    }

    @SneakyThrows
    private void saveOrders() {
        FileUtil.write(orderFileName, orderList);
    }

    public String addProductToCart(CartItem cartItem, UUID userId) {
        int price = priceCalculation(cartItem);
//        for (Cart cart : cartList) {
//            if (cart.getId().equals(cartItem.getCartId())) {
//                CartItem cartItem1 = hasCartItem(cart.getCartItemList(), cartItem);
//                if (cartItem1 == null) {
//                    cart.getCartItemList().add(cartItem);
//                } else {
//                    cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity());
//                }
//                cart.setTotalPrice(cart.getTotalPrice() + price);
//                return "Successful";
//            }
//        }
        Cart currCart = cartList.stream().filter(cart -> cart.getId().equals(cartItem.getCartId())).
                findFirst().orElse(null);
        if (currCart != null) {
            CartItem cartItem1 = hasCartItem(currCart.getCartItemList(), cartItem);
            if (cartItem1 == null) {
                currCart.getCartItemList().add(cartItem);
            } else {
                cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity());
            }
            currCart.setTotalPrice(currCart.getTotalPrice() + price);
            saveCarts();
            return "Successful";
        }
        currCart = createCart(cartItem.getCartId(), userId);
        currCart.getCartItemList().add(cartItem);
        currCart.setTotalPrice(price);
        saveCarts();
        return "Successful";
    }

    private int priceCalculation(CartItem cartItem) {
        Optional<Product> optionalProduct = ProductService.getProductById(cartItem.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Not found product");
        }
        int price = optionalProduct.get().getPrice();
        return price * cartItem.getQuantity();
    }

    private CartItem hasCartItem(List<CartItem> cartItemList, CartItem cartItem) {
        return cartItemList.stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst()
                .orElse(null);

//        for (CartItem item : cartItemList) {
//            if (item.getProductId().equals(cartItem.getProductId())) {
//                return item;
//            }
//        }
//        return null;
    }

    public Cart getCartByCartId(UUID cartId) {
        return cartList.stream().filter(cart -> cart.getId().equals(cartId))
                .findFirst().orElse(null);

//        for (Cart cart : cartList) {
//            if (cart.getId().equals(cartId)) {
//                return cart;
//            }
//        }
//        return null;
    }

    public String deletedCart(UUID cartId) {
        Cart cart = getCartByCartId(cartId);
        if (cart == null) {
            return "not found cart";
        }
        cartList.remove(cart);
        saveCarts();
        return "Successful";
    }

    public void addCartToOrders(Cart cart) {
        orderList.add(cart);
        deletedCart(cart.getId());
        saveOrders();
    }

    public List<Cart> getOrdersByUserId(UUID userId) {
        return orderList.stream().filter(cart -> cart.getUserId().equals(userId))
                .collect(Collectors.toList());

//        List<Cart> carts = new ArrayList<>();
//        for (Cart cart : orderList) {
//            if (cart.getUserId().equals(userId)) {
//                carts.add(cart);
//            }
//        }
//        return carts;
    }

    public List<Cart> getAllOrders() {
        return orderList;
    }

    public Cart createCart(UUID cartId, UUID userId) {
        Cart cart = new Cart(userId, cartId);
        cartList.add(cart);
        return cart;
    }
}