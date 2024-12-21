package com.sopotek.aipower.routes.api.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubCallbackRequest {

    private Long id;


        private String username;
        private  String email;
        private  String name;

}
