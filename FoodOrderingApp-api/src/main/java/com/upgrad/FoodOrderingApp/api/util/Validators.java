package com.upgrad.FoodOrderingApp.api.util;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import org.springframework.stereotype.Service;

@Service
public class Validators {

    public static boolean checkForEmptyEntityField(SaveAddressRequest saveAddressRequest){

        boolean empty = false;

        if(saveAddressRequest.getStateUuid().isEmpty()
                || saveAddressRequest.getPincode().isEmpty()
                || saveAddressRequest.getFlatBuildingName().isEmpty()
                || saveAddressRequest.getLocality().isEmpty()
                || saveAddressRequest.getCity().isEmpty())
            empty = true;

        return empty;
    }
}
