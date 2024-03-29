package net.notjustanna.users.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import net.notjustanna.users.dto.FindUserRequestDto;
import net.notjustanna.users.dto.FindUsersRequestDto;
import net.notjustanna.users.model.QUser;
import net.notjustanna.users.model.User;
import net.notjustanna.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        return userRepository.save(user);
    }

    public Page<User> findUsers(FindUserRequestDto user, int page, int size, List<FindUsersRequestDto.OrderBy> orderBy) {
        final var sort = Sort.by(
            orderBy.stream().map(it -> new Sort.Order(it.getDirection(), it.getProperty())).toList()
        );
        final var pageRequest = PageRequest.of(page, size, sort);
        if (user == null) {
            return userRepository.findAll(pageRequest);
        }

        QUser q = new QUser("user");

        BooleanExpression expression = null;
        if (user.getId() != null) {
            expression = q.id.eq(user.getId());
        }
        if (user.getNome() != null) {
            var eq = q.nome.eq(user.getNome());
            expression = expression == null ? eq : expression.and(eq);
        }
        if (user.getEmail() != null) {
            var eq = q.email.eq(user.getEmail());
            expression = expression == null ? eq : expression.and(eq);
        }
        if (user.getEndereco() != null) {
            var eq = q.endereco.eq(user.getEndereco());
            expression = expression == null ? eq : expression.and(eq);
        }
        if (user.getTelefone() != null) {
            var eq = q.telefone.eq(user.getTelefone());
            expression = expression == null ? eq : expression.and(eq);
        }
        if (user.getPerfil() != null) {
            var eq = q.perfil.eq(user.getPerfil());
            expression = expression == null ? eq : expression.and(eq);
        }
        if (expression == null) {
            return userRepository.findAll(pageRequest);
        }
        return userRepository.findAll(expression, pageRequest);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public User getById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}
