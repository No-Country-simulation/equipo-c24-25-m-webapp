package com.back.banka.Services.IServices;


import com.back.banka.Model.VerificationCode;

public interface IVerificationCodeService {

    VerificationCode generateCode(Long userId, String purpose, String referenceData,
                                  String emailSubject, String emailTemplate);
    VerificationCode validateCode(Long userId, String code, String purpose);

}
