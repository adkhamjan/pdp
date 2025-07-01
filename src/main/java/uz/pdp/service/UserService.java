package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.util.FileUtil;
import uz.pdp.model.User;
import uz.pdp.wrapper.UserListWrapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private static final String fileName = "users.xml";
    private final List<User> users;

    @SneakyThrows
    public UserService() {
        UserListWrapper wrapper = FileUtil.readFromXml(fileName, UserListWrapper.class);
        users = wrapper.getUsers() != null ? wrapper.getUsers() : new ArrayList<>();
    }

    @SneakyThrows
    public void updateUser(User user, UUID userId) {
        Optional<User> userr = getByUserId(userId);
        if (userr.isPresent()) {
            User user1 = userr.get();
            user1.setPassword(user.getPassword());
            user1.setName(user.getName());
            user1.setUpdateDate(LocalDateTime.now());
            FileUtil.writeToXml(fileName, new UserListWrapper(users));
        }
    }


    @SneakyThrows
    public String add(User user) {
        if (hasUser(user)) {
            return "Unsuccessful";
        }
        users.add(user);
        FileUtil.writeToXml(fileName, new UserListWrapper(users));
        return "successful \n";
    }

    public boolean hasUser(User user) {
//        for (User user1 : users) {
//            if (user.getUserName().equals(user1.getUserName())) {
//                return true;
//            }
//        }
//        return false;
        return users.stream().anyMatch(user1 -> user1.getUserName().equals(user.getUserName()));
    }

    public Optional<User> login(String userName, String password) {
//        for (User user : users) {
//            if (user.getUserName().equals(userName) && user.getPassword().equals(password)) {
//                return user;
//            }
//        }
//        return null;
        return users.stream().filter(u -> u.getUserName().equals(userName) && u.getPassword().equals(password))
                .findFirst();
    }

    private Optional<User> getByUserId(UUID userId) {
//        for (User user : users) {
//            if (user.getId().equals(userId) && user.isActive()) {
//                return user;
//            }
//        }
//        return null;
        return users.stream().filter(u -> u.getId().equals(userId) && u.isActive()).findFirst();
    }
}
