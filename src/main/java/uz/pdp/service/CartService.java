package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Cart;
import uz.pdp.model.CartItem;
import uz.pdp.util.FileUtil;
import uz.pdp.model.Product;

import java.util.*;

public class CartService {
    private final String fileName = "orders.json";
    private List<Cart> orderList;
    private List<Cart> cartList;

    @SneakyThrows
    public CartService() {
        orderList = FileUtil.read(fileName, Cart.class);
    }

    private List<Cart> getCartListByCartId(UUID cartId) {
        return cartMapByCartId.get(cartId);
    }

    @SneakyThrows
    private void saveCarts() {
        FileUtil.write(fileName, orderList);
    }

    public String addProductToCart(UUID cartId, CartItem cartItem) {
        for (Cart cart : cartList) {
            if (cart.getId().equals(cartId)) {
                cart.getCartList().add(cartItem);
                return "Successful";
            }
        }
        Cart cart = new Cart(cartId);
        createCart(cart);
        cart.getCartList().add(cartItem);
        return "Successful";
    }

    public List<List<Cart>> getCartByUserId(UUID userId) {
        Set<UUID> cartIds = cartMapByUserId.get(userId);
        List<List<Cart>> list = new ArrayList<>(new ArrayList<>());
        if (cartIds == null) {
            return list;
        }
        for (UUID cartId : cartIds) {
            list.add(getCartListByCartId(cartId));
        }
        return list;
    }

    public List<Cart> getAllCarts() {
        return cartList;
    }

    public void createCart(Cart cart){
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