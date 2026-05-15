package com.festival.ms_usuario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TokenResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private String email;
    private String rol;

    public TokenResponseDTO(String token, String email, String rol) {
        this.token = token;
        this.email = email;
        this.rol = rol;
    }
}