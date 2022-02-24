package com.zarszz.userservice.persistence.service;

import com.zarszz.userservice.domain.UserAddress;
import com.zarszz.userservice.persistence.repository.UserAddressRepository;
import com.zarszz.userservice.requests.v1.useraddress.CreateUserAddressDto;
import com.zarszz.userservice.requests.v1.useraddress.UpdateUserAddressDto;
import com.zarszz.userservice.security.entity.AuthenticatedUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserAddressImpl implements UserAddressService {

    @Autowired
    UserAddressRepository userAddressRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    public UserAddress create(CreateUserAddressDto createUserAddressDto) throws Exception {
        var userAddress = modelMapper.map(createUserAddressDto, UserAddress.class);
        userAddress.setUserId(authenticatedUser.getUserId());
        return userAddressRepository.save(userAddress);
    }

    @Override
    public List<UserAddress> get() {
        return userAddressRepository.findByUserId(authenticatedUser.getUserId());
    }

    @Override
    public UserAddress getById(Long id) throws NoSuchElementException {
        return userAddressRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Address Not Found"));
    }

    @Override
    public void update(UpdateUserAddressDto updateUserAddressDto, Long id) throws NoSuchElementException {
        var userAddress = userAddressRepository.findById(id);

        var newUserAddress = userAddress.orElseThrow(() -> new NoSuchElementException("Address Not Found"));

        newUserAddress.setAddress(updateUserAddressDto.getAddress());
        newUserAddress.setAddressName(updateUserAddressDto.getName());
        newUserAddress.setCity(updateUserAddressDto.getCity());
        newUserAddress.setDistrict(updateUserAddressDto.getDistrict());
        newUserAddress.setState(updateUserAddressDto.getState());
        newUserAddress.setZipCode(updateUserAddressDto.getZipCode());

        userAddressRepository.save(newUserAddress);
    }

    @Override
    public void delete(Long id) {
        var userAddress = userAddressRepository.findById(id);
        userAddress.orElseThrow(() -> new NoSuchElementException("Address Not Found"));
        userAddressRepository.deleteById(id);
    }
}
