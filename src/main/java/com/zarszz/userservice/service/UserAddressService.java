package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.UserAddress;
import com.zarszz.userservice.requests.v1.useraddress.CreateUserAddressDto;
import com.zarszz.userservice.requests.v1.useraddress.UpdateUserAddressDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface UserAddressService {
    UserAddress create(CreateUserAddressDto createUserAddressDto) throws Exception;
    List<UserAddress> get();
    UserAddress getById(Long id) throws NoSuchElementException;
    void update(UpdateUserAddressDto updateUserAddressDto, Long id) throws NoSuchElementException;
    void delete(Long id) throws NoSuchElementException;
}
