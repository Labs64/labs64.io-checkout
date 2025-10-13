package io.labs64.checkout.rules;

import io.labs64.checkout.v1.entity.BillingInfoEntity;

public record BillingInfoLockedSnapshot(String name,String email,String phone,String country,String city,String address1,String address2,String postalCode,String state){

public static BillingInfoLockedSnapshot from(final BillingInfoEntity e){return new BillingInfoLockedSnapshot(e.getName(),e.getEmail(),e.getPhone(),e.getCountry(),e.getCity(),e.getAddress1(),e.getAddress2(),e.getPostalCode(),e.getState());}

}
