package com.crediya.auth.api.mapper;

import com.crediya.auth.api.dto.UserCreateDTO;
import com.crediya.auth.api.dto.UserResponseDTO;
import com.crediya.auth.model.usuario.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    UserResponseDTO toResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(UserCreateDTO userCreateDTO);
}
