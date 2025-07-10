package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.util.FileUtil;
import uz.pdp.model.User;
import uz.pdp.wrapper.UserListWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private static final String fileName = "usersBot.xml";
    private final List<User> users;
    @SneakyThrows
    public UserService() {
        UserListWrapper wrapper = FileUtil.readFromXml(fileName, UserListWrapper.class);
        users = wrapper.getUsers() != null ? wrapper.getUsers() : new ArrayList<>();
    }

    @SneakyThrows
    public void updateUser(User user, UUID userId) {
        Optional<User> optionalUser = getByUserId(userId);
        if (optionalUser.isPresent()) {
            User user1 = optionalUser.get();
            user1.setPhoneNumber(user.getPhoneNumber());
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
        return users.stream().filter(u -> u.getUserName().equals(userName) && u.getPhoneNumber().equals(password))
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


    public void registerUser(Long userId, String username,String name) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println("ID: " + userId + " | Username: @" + username+" name : "+name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public User getByChatId(Long userId) {
        return users.stream()
                  .filter(user -> user.getId().equals(userId)).
                  findFirst().
                  orElse(null);
    }
}
