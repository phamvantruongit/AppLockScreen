package vn.com.it.truongpham.lockscreen;

public class User {
    public String key_pass;
    public String device_id;
    public String device_name;

    public User() {

    }

    public User(String key_pass, String device_id, String device_name) {
        this.key_pass = key_pass;
        this.device_id = device_id;
        this.device_name = device_name;
    }
}
