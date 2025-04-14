package services.user;

import models.User;

public interface ISignInService {
    public User authenticate(String email, String password);
}
