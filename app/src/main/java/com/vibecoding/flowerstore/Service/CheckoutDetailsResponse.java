package com.vibecoding.flowerstore.Service;

import com.google.gson.annotations.SerializedName;
import com.vibecoding.flowerstore.Model.AddressDTO;
import com.vibecoding.flowerstore.Model.CartDTO;

import java.util.List;

public class CheckoutDetailsResponse {
    @SerializedName("cart")
    private CartDTO cart;

    @SerializedName("addresses")
    private List<AddressDTO> addresses;

    @SerializedName("shippingCarriers")
    private List<ShippingCarrier> shippingCarriers;

    public CartDTO getCart() { return cart; }
    public List<AddressDTO> getAddresses() { return addresses; }
    public List<ShippingCarrier> getShippingCarriers() { return shippingCarriers; }

    public static class ShippingCarrier {
        private int id;
        private String name;
        private double shippingFee;

        public int getId() { return id; }
        public String getName() { return name; }
        public double getShippingFee() { return shippingFee; }
    }
}
