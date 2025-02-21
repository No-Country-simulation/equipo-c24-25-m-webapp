package com.back.banka.Services.IServices;


import com.back.banka.Dtos.RequestDto.RegisterRequestDto;


    public interface IRegisterService {
        String registerUser(RegisterRequestDto request);
    }

