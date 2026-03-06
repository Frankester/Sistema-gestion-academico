package com.francocallero.sistema.de.gestion.academico.config.filtros;

import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import com.francocallero.sistema.de.gestion.academico.repositorios.RepoUsuarios;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FiltroValidacionCambioContrasenia extends OncePerRequestFilter {


    private final RepoUsuarios repoUsuarios;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                auth.getPrincipal() instanceof Usuario usuario
        ) {

            Long versionContraseniaLogin = usuario.getPasswordVersion();
            Long versionContraseniaAlmacenada = repoUsuarios
                    .findPasswordVersionById(usuario.getUsuarioId());

            if (!versionContraseniaLogin.equals(versionContraseniaAlmacenada)) {

                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                response.sendRedirect("/login?expired");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
