package model.people;

import enums.Role;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    private String username;
    private String password;
    private Role role;
    private List<String> permissions = new ArrayList<>();

    public UserAccount(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public UserAccount(UserAccount u) {
        this.username = u.username;
        this.password = u.password;
        this.role = u.role;
    }
    public UserAccount() {
        this.username = "";
        this.password = "";
        this.role = null;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}