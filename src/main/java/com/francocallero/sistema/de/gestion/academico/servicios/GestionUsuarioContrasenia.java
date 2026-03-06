package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.contrasenias.CambioContraseniaToken;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.contrasenia.ContraseniaNuevaDTO;
import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.contrasenia.OlvidoContraseniaDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoCambioContraseniaToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GestionUsuarioContrasenia {

    private final RepoCambioContraseniaToken repoCambioContraseniaToken;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    private final TokenService tokenService;

    private final UsuarioService usuarioService;

    @Value("${app.base-url:http://localhost:8080}")//desde application.properties
    private String baseUrl;


    @Autowired
    public GestionUsuarioContrasenia(RepoCambioContraseniaToken repoCambioContraseniaToken,
                                     PasswordEncoder passwordEncoder,
                                     MailService mailService,
                                     TokenService tokenService,
                                     UsuarioService usuarioService
    ) {
        this.repoCambioContraseniaToken = repoCambioContraseniaToken;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public void gestionarOlvidoContrasenia(OlvidoContraseniaDTO form){
        String emailUsuario = form.getEmail();
        if(usuarioService.existeUsuarioConPersonaMail(emailUsuario)){
            CambioContraseniaToken cambioContraseniaToken = new CambioContraseniaToken();
            Usuario usuario = usuarioService.encontrarUsuarioPorMail(emailUsuario);
            String token = tokenService.generarToken();
            String tokenHash =tokenService.hash(token);

            cambioContraseniaToken.setTokenHash(tokenHash);
            cambioContraseniaToken.setFechaHoraExpiracion(
                    LocalDateTime.now().plusMinutes(30) // 30 min para cambiar la contraseña
            );

            cambioContraseniaToken.setUsado(false);
            cambioContraseniaToken.setUsuario(usuario);

            repoCambioContraseniaToken.save(cambioContraseniaToken);


            String linkCodificado = baseUrl +"/contrasenias/cambiar?token="+
                    URLEncoder.encode(token, StandardCharsets.UTF_8);

            this.mailService.enviarMailA(form.getEmail(),
                    usuario.getPersona().getNombre(),
                    linkCodificado);
        }
        // si no existe no hacemos nada por seguridad
    }

    @Transactional
    public Long obtenerIdCambioContraseniaConToken(String token){
        Optional<CambioContraseniaToken> opCambioContraseniaToken = repoCambioContraseniaToken
                .findByTokenHash(
                        tokenService.hash(token)
                );

        if(opCambioContraseniaToken.isEmpty()){
            throw new BusinessException(ErrorCode.TOKEN_INVALIDO);
        }else {
            CambioContraseniaToken cambioContraseniaToken = opCambioContraseniaToken.get();

            if(cambioContraseniaToken.isUsado() || cambioContraseniaToken.vencioContrasenia()){
                throw new BusinessException(ErrorCode.TOKEN_INVALIDO);
            }

            return cambioContraseniaToken.getIdCambioContraseniaToken();
        }
    }

    @Transactional
    public void cambiarContrasenia(Long idCambioContrasenia, ContraseniaNuevaDTO form){
        CambioContraseniaToken cambioContraseniaToken = this.obtenerCambioContraseniaTokenConId(
                idCambioContrasenia
        );

        cambioContraseniaToken.setUsado(true);
        Usuario usuario = cambioContraseniaToken.getUsuario();

        usuario.setPassword(passwordEncoder.encode(form.getContraseniaNueva()));
        usuario.setPasswordVersion(usuario.getPasswordVersion() + 1 );//invalidar sessiones activas del usuario
    }


    private CambioContraseniaToken obtenerCambioContraseniaTokenConId(Long idCambioContraseniaToken){
        return  this.repoCambioContraseniaToken
                .findById(idCambioContraseniaToken)
                .orElseThrow(()-> new BusinessException(ErrorCode.CAMBIO_CONTRASENIA_NO_EXISTE));
    }


}
