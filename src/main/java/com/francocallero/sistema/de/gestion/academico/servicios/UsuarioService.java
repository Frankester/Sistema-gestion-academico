package com.francocallero.sistema.de.gestion.academico.servicios;

import com.francocallero.sistema.de.gestion.academico.controllers.DTOs.persona.UsuarioDTO;
import com.francocallero.sistema.de.gestion.academico.exceptions.BusinessException;
import com.francocallero.sistema.de.gestion.academico.exceptions.ErrorCode;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Alumno;
import com.francocallero.sistema.de.gestion.academico.gestion_de_alumnos_y_docentes.Docente;
import com.francocallero.sistema.de.gestion.academico.persona.Persona;
import com.francocallero.sistema.de.gestion.academico.persona.Rol;
import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;


public class UsuarioService implements UserDetailsService {

    private final RepoUsuarios repoUsuarios;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private PlanService planService;


    public UsuarioService(RepoUsuarios repoUsuarios, PasswordEncoder passwordEncoder) {
        this.repoUsuarios = repoUsuarios;
        this.passwordEncoder = passwordEncoder;
    }


    public boolean existeUsuarioConPersonaMail(String mail){
        Optional<Usuario> opUser = this.repoUsuarios.findByPersonaMail(mail);
        return opUser.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.encontrarUsuarioPorUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("user.not-found"));
    }

    public Usuario encontrarUsuarioPorMail(String mail){
        return this.repoUsuarios.findByPersonaMail(mail)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public void crearUser(Usuario nuevoUsuario) {

        String username = nuevoUsuario.getUsername();

        if(this.existeUsuarioConUsername(username)){
            throw new BusinessException(ErrorCode.USER_USERNAME_ALREADY_EXIST, username);
        }

        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        this.repoUsuarios.save(nuevoUsuario);
    }

    public void crearUserDeForm(UsuarioDTO form)  {

        String documento = form.getDocumento();

        Optional<Usuario> opUsuario = repoUsuarios.findByDocumento(documento);

        if(opUsuario.isPresent()){
            throw new BusinessException(ErrorCode.USER_DOCUMENTO_ALREADY_EXIST,documento);
        }else {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(form.getUsername());
            String rolAuthority = form.getAuthority();
            nuevoUsuario.agregarRol(rolAuthority);
            nuevoUsuario.setPassword(form.getPassword());

            Persona persona = this.crearPersona(form);
            nuevoUsuario.setPersona(persona);

            this.crearUser(nuevoUsuario);
        }
    }

    public Set<String> obtenerRolesDe(String username) {
        if(!this.existeUsuarioConUsername(username)){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return this.repoUsuarios.findRolesByUsername(username);
    }

    public Persona obtenerPersonaDeUsuario(Usuario usuario){
        return repoUsuarios.findPersonaByUsuarioId(usuario.getUsuarioId())
                    .orElseThrow(()->new BusinessException(ErrorCode.PERSONA_NO_EXISTE));
    }


    private Persona crearPersona(UsuarioDTO form){
        Persona persona;
        String rolAuthority = form.getAuthority();
        if(rolAuthority.equalsIgnoreCase("alumno")){
            persona = new Alumno();
            ((Alumno)persona).setPlanInscripto(
                    planService.obtenerPlan(form.getIdPlan())
            );

        } else if(rolAuthority.equalsIgnoreCase("docente")){
            persona = new Docente();
        } else {
            persona = new Persona();
            persona.setRol(Rol.ADMIN);
        }
        persona.setDocumento(form.getDocumento());
        persona.setNombre(form.getNombre());
        persona.setApellido(form.getApellido());
        return persona;
    }

    private Optional<Usuario> encontrarUsuarioPorUsername(String username){
        return this.repoUsuarios.findByUsername(username);
    }

    private boolean existeUsuarioConUsername(String username){
        Optional<Usuario> opUser =this.encontrarUsuarioPorUsername(username);

        return opUser.isPresent();
    }

    public void guardarUsuario(Usuario usuario) {
        repoUsuarios.updateUsuario(usuario);
    }
}
