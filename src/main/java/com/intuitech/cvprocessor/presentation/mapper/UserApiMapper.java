package com.intuitech.cvprocessor.presentation.mapper;

import com.intuitech.cvprocessor.api.UserSummaryDTO;
import com.intuitech.cvprocessor.domain.auth.Role;
import com.intuitech.cvprocessor.domain.auth.User;

import java.util.List;

public final class UserApiMapper {

    private UserApiMapper() {}

    public static UserSummaryDTO toSummaryDto(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        return new UserSummaryDTO()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .roles(roles);
    }
}
