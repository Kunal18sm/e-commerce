package com.shopzone.model;

import java.sql.Timestamp;

/**
 * User model combining login credentials and profile details.
 */
public class User implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private int uid;
    private String uname;
    private String upass;
    private String utype;       // admin, mod, user
    private boolean enabled;
    private Timestamp createdAt;

    // Profile details
    private String name;
    private String email;
    private String mobile;
    private String address;

    public User() {}

    public User(int uid, String uname, String utype, boolean enabled) {
        this.uid = uid;
        this.uname = uname;
        this.utype = utype;
        this.enabled = enabled;
    }

    // --- Getters and Setters ---

    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }

    public String getUpass() { return upass; }
    public void setUpass(String upass) { this.upass = upass; }

    public String getUtype() { return utype; }
    public void setUtype(String utype) { this.utype = utype; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /**
     * Convenience: check if user is admin.
     */
    public boolean isAdmin() { return "admin".equals(utype); }

    /**
     * Convenience: check if user is moderator.
     */
    public boolean isModerator() { return "mod".equals(utype); }

    /**
     * Convenience: get display name (profile name or username).
     */
    public String getDisplayName() {
        return (name != null && !name.isEmpty()) ? name : uname;
    }

    @Override
    public String toString() {
        return "User{uid=" + uid + ", uname='" + uname + "', utype='" + utype + "', enabled=" + enabled + "}";
    }
}
