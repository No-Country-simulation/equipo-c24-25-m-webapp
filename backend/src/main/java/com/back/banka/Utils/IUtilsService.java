package com.back.banka.Utils;

import com.back.banka.Model.AccountBank;
import com.back.banka.Model.User;

public interface IUtilsService{
    void validateOwnership(AccountBank accountBank, String username);
    void validateAccountStatus(AccountBank accountBank);
    void validateBalanceAccount(AccountBank accountBank);
    String getAuthenticatedUser();
    Long getAuthenticatedUserId();
    void validateUserAuthorization(AccountBank accountBank, Long authenticatedUserId);
    void sendAccountNotification(User user, String subject, String
            templateName, String message);
    void saveUserToken(User user, String token);
}
