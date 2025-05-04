package services.user;

import models.User;

import java.util.List;

public interface IUpdateService {
    List<User> getAllUsers();
    void updateUser(User user) throws Exception;
}
