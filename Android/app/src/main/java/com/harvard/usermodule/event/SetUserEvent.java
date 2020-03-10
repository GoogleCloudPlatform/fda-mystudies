package com.harvard.usermodule.event;

import com.harvard.usermodule.model.User;

/**
 * Created by Rohit on 2/17/2017.
 */

public class SetUserEvent {
    private User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
