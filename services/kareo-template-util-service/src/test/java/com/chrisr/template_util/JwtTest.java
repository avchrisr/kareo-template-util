package com.chrisr.template_util;

import com.chrisr.template_util.controller.UserRestControllerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JwtTest {

    @InjectMocks
    private UserRestControllerImpl userRestControllerImpl;

    @Test
    public void createJwtToken() {
        String jwt = userRestControllerImpl.createJwt();
        System.out.println("JWT = " + jwt);
    }

    @Test
    public void decodeJwtToken() {
        String jwt = userRestControllerImpl.createJwt();
        String decodedJwt = userRestControllerImpl.decodeJwt(jwt);
        System.out.println("Decoded JWT = " + decodedJwt);
    }
}
