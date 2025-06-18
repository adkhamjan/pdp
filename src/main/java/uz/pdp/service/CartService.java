package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.Cart;
import uz.pdp.model.FileUtil;
import uz.pdp.model.Product;

import java.util.*;

public class CartService {
    private final String fileName = "carts.json";
    private List<Cart> cartList;
    private Map<UUID, List<Cart>> cartMapByCartId;
    private Map<UUID, Set<UUID>> cartMapByUserId;

    @SneakyThrows
    public CartService() {
        cartList = FileUtil.read(fileName, Cart.class);
        cartMapByCartId = new HashMap<>();
        cartMapByUserId = new HashMap<>();

        for (Cart cart : cartList) {
            List<Cart> carts = cartMapByCartId.get(cart.getCartId());
            if (carts == null) {
                carts = new ArrayList<>();
            }
            carts.add(cart);
            cartMapByCartId.put(cart.getCartId(), carts);
        }

        for (Cart cart : cartList) {
            Set<UUID> cartIds = cartMapByUserId.get(cart.getUserId());
            if (cartIds == null) {
                cartIds = new HashSet<>();
            }
            cartIds.add(cart.getCartId());
            cartMapByUserId.put(cart.getUserId(), cartIds);
        }
    }

    private List<Cart> getCartListByCartId(UUID cartId) {
        return cartMapByCartId.get(cartId);
    }

    @SneakyThrows
    private void saveCarts() {
        FileUtil.write(fileName, cartList);
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

    public UUID createCart(){
        return UUID.randomUUID();
    }

    public String addProductToCart(UUID productId, UUID userId, UUID cartId, int quantity) {
        Product product = ProductService.getProductById(productId);
        if (product != null && product.isActive()) {
            Cart newCart = new Cart(cartId, userId, productId, quantity);
            List<Cart> carts = cartMapByCartId.get(cartId);
            if (carts == null) {
                carts = new ArrayList<>();
                carts.add(newCart);
                cartMapByCartId.put(cartId, carts);

                Set<UUID> cartIds = cartMapByUserId.get(userId);
                if (cartIds == null) {
                    cartIds = new HashSet<>();
                }
                cartIds.add(cartId);
                cartMapByUserId.put(userId, cartIds);

                cartList.add(newCart);
                saveCarts();
                return "Successful \n";
            }
            for (Cart cart : carts) {
                if (cart.getProductId().equals(productId)) {
                    cart.setQuantity(cart.getQuantity() + quantity);
                    saveCarts();
                    return "Successful \n";
                }
            }
            cartList.add(newCart);
            carts.add(newCart);
            saveCarts();
            return "Successful \n";
        }
        return "not found product \n";
    }
}
