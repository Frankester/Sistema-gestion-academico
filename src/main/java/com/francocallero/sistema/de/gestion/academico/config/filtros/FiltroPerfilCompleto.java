package com.francocallero.sistema.de.gestion.academico.config.filtros;

import com.francocallero.sistema.de.gestion.academico.persona.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroPerfilCompleto  extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // excluir recursos estáticos
        if (esRecursoEstatico(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean esUsuarioLogeado = auth != null &&
                auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken);

        if(esUsuarioLogeado){
            Usuario usuario = (Usuario) auth.getPrincipal();

            if(!usuario.isPerfilCompleto()){

                String rolUsuario = usuario
                        .getPersona()
                        .getRol()
                        .toString()
                        .toLowerCase();

                String pathDatosPersonales = "/"+ rolUsuario+"/datos";

                boolean estaEnPantallaCompletar =
                        path.startsWith(pathDatosPersonales);

                if (!estaEnPantallaCompletar) {
                    response.sendRedirect(
                            request.getContextPath() + pathDatosPersonales
                    );
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public boolean esRecursoEstatico(String path){
       return path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/images") ||
                path.startsWith("/webjars") ||
                path.contains("favicon");
    }

}
