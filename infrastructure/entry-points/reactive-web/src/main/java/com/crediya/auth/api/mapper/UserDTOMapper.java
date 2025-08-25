package com.crediya.auth.api.mapper;

import com.crediya.auth.api.dto.UserCreateDTO;
import com.crediya.auth.api.dto.UserReponseDTO;
import com.crediya.auth.model.usuario.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    UserReponseDTO toResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(UserCreateDTO userCreateDTO);
}
