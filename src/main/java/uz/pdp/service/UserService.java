package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.FileUtil;
import uz.pdp.model.User;
import uz.pdp.model.UserListWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final String fileName = "users.xml";
    private List<User> users;

    public UserService() {
        users = new ArrayList<>();
        try {
            users = FileUtil.readFromXml(fileName, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public String add(User user)  {
        for (User user1 : users) {
            if(user.getUserName().equals(user1.getUserName())){
                return "Unsuccessful \n";
            }
        }
        users.add(user);
        FileUtil.writeToXml(fileName,new UserListWrapper(users));
        return "successful \n";
    }

    public User login(String userName, String password){
        for (User user : users) {
            if (user.getUserName().equals(userName) && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;

    }

    public List<User> getAllUsers() throws IOException{
        return FileUtil.readFromXml(fileName, User.class);
    }
}
