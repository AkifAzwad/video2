package com.example.video2.security;


import com.example.video2.model.User;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
@SuppressWarnings("java:S2160")
public class SecurityUserDetails extends org.springframework.security.core.userdetails.User {
  private final User user;

  public SecurityUserDetails(final User user) {
    super(
      user.getUsername(),
      StringUtils.EMPTY,
      List.of(new SimpleGrantedAuthority(user.getRole().name()))
    );
    this.user = user;
  }
}
