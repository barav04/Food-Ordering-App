package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.AddressService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.AppUtils;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,
                                                           @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException {

        final String accessToken = AppUtils.getBearerAuthToken(authorization);

        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        AddressEntity address = new AddressEntity();
        address.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        address.setLocality(saveAddressRequest.getLocality());
        address.setCity(saveAddressRequest.getCity());
        address.setPincode(saveAddressRequest.getPincode());
        address.setUuid(UUID.randomUUID().toString());
        address.setActive(1);
        StateEntity state = addressService.getStateByUUID(saveAddressRequest.getStateUuid());
        address.setCustomers(customerEntity);

        AddressEntity savedAddress = addressService.saveAddress(address, state);

        SaveAddressResponse addressResponse = new SaveAddressResponse()
                .id(savedAddress.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SaveAddressResponse>(addressResponse, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddresses(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        final String accessToken = AppUtils.getBearerAuthToken(authorization);

        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        List<AddressEntity> sortedAddress = addressService.getAllAddress(customerEntity);

        List<AddressList> addressesList = new ArrayList<>();

        sortedAddress.forEach(address -> {
            AddressListState addressListState = new AddressListState();
            addressListState.setId(UUID.fromString(address.getState().getUuid()));
            addressListState.setStateName(address.getState().getStateName());

            AddressList addressList = new AddressList()
                    .id(UUID.fromString(address.getUuid()))
                    .flatBuildingName(address.getFlatBuilNo())
                    .city(address.getCity())
                    .locality(address.getLocality())
                    .pincode(address.getPincode())
                    .state(addressListState);
            addressesList.add(addressList);
        });

        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressesList);
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestHeader("authorization") final String authorization,
                                                               @PathVariable(value = "address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        final String accessToken = AppUtils.getBearerAuthToken(authorization);

        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        AddressEntity address = addressService.getAddressByUUID(addressId, customerEntity);

        AddressEntity deletedAddress;

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse();


        if (address.getOrders().isEmpty()) { // If address have no history of orders, delete the address from database
            deletedAddress = addressService.deleteAddress(address);
        } else { // If address have history of orders, soft delete (deactivate) the address from database
            address.setActive(0);
            deletedAddress = addressService.deactivateAddress(address);
        }

        deleteAddressResponse.status("ADDRESS DELETED SUCCESSFULLY");
        deleteAddressResponse.id(UUID.fromString(deletedAddress.getUuid()));
        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }

    /**
     * Method takes no input from customer, returns all states
     *
     * @return ResponseEntity with list of States
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {

        List<StateEntity> states = addressService.getAllStates();

        if (!states.isEmpty()) {
            List<StatesList> statesList = new LinkedList<>();
            states.forEach(state -> {
                StatesList stateList = new StatesList();
                stateList.setId(UUID.fromString(state.getUuid()));
                stateList.setStateName(state.getStateName());

                statesList.add(stateList);
            });
            StatesListResponse statesListResponse = new StatesListResponse().states(statesList);
            return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
        } else
            return new ResponseEntity<StatesListResponse>(new StatesListResponse(), HttpStatus.OK);
    }

}
