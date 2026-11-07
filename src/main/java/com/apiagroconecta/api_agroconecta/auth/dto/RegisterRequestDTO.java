package com.apiagroconecta.api_agroconecta.auth.dto;


import com.apiagroconecta.api_agroconecta.auth.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// RegisterRequestDTO: los datos que el cliente debe enviar para registrarse.
// Separar este DTO del modelo Usuario evita exponer campos internos
// como el id o la contraseña hasheada en la respuesta.
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    private Rol rol;

    /**
     * Teléfono de contacto.
     * Obligatorio para CLIENTE, opcional para ADMIN.
     * La validación de obligatoriedad según rol se realiza en AuthController.
     */
    @Pattern(
        regexp = "^[+]?[0-9]{7,15}$",
        message = "El teléfono debe contener solo dígitos (7-15), con '+' opcional al inicio"
    )
    private String telefono;

    public RegisterRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
