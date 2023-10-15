/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.CartDTO;
import housemate.models.CartItemDTO;
import housemate.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Operation(summary = "Get user cart by userId in token")
    @GetMapping("/")
    public ResponseEntity<CartDTO> getCart(HttpServletRequest request) {
        return cartService.getCart(request);
    }

    @Operation(summary = "Add to cart. When cart already exist -> update quantity and period only")
    @PostMapping("/add")
    public ResponseEntity<String> addCart(HttpServletRequest request, @Valid @RequestBody CartItemDTO cartItemDTO) {
        return cartService.addToCart(request, cartItemDTO);
    }

    @Operation(summary = "Update cart.")
    @PutMapping("/update")
    public ResponseEntity<String> updateCart(HttpServletRequest request, @Valid @RequestBody CartItemDTO cartItemDTO) {
        return cartService.updateToCart(request, cartItemDTO);
    }

    @Operation(summary = "Detele cart when cart exist")
    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<String> removeCart(HttpServletRequest request, @PathVariable int cartId) {
        return cartService.removeCart(request, cartId);
    }

    @Operation(summary = "Detele all cart when cart exist (Be careful when doing this)")
    @DeleteMapping("/remove/all")
    public ResponseEntity<String> removeAllCart(HttpServletRequest request) {
        return cartService.removeAllCartByUserId(request);
    }
}
