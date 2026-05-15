package DTO;

import data.User;

public class UserMapper {

    //Из сущности в DTO
    public static UserDTO toDTO(User user) {

        UserDTO userDTO = new UserDTO();
       // userDTO.setUser_id(user.getUser_id());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());

        return userDTO;
    }


}
