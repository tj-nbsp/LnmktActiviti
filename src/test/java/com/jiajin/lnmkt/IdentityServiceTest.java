package com.jiajin.lnmkt;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.junit.Test;

public class IdentityServiceTest extends SpringTestCase {
    
    @Resource
    IdentityService identityService;
    
    @Test
    public void testAddUser() {
        User user = identityService.newUser("1001");
        user.setFirstName("Lani");
        user.setLastName("Robert");
        user.setEmail("18827529536@163.com");
        user.setPassword("lani123");
        identityService.saveUser(user);
    }

}
