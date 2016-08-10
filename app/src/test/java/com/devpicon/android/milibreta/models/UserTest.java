package com.devpicon.android.milibreta.models;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by armando on 8/10/16.
 */
public class UserTest {

    @Test
    public void createInstance() {
        User user = new User("apiconz", "apiconz@demo.com", "Armando", "123456789");
        Assert.assertEquals("apiconz", user.getUsername());
    }

}
