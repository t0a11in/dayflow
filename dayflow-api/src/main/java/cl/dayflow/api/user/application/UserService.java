package cl.dayflow.api.user.application;

import cl.dayflow.api.user.application.dto.ChangePasswordRequest;
import cl.dayflow.api.user.application.dto.ChangeUserStatusRequest;
import cl.dayflow.api.user.application.dto.CreateUserRequest;
import cl.dayflow.api.user.application.dto.UpdateUserRequest;
import cl.dayflow.api.user.application.dto.UserResponse;
import cl.dayflow.api.user.domain.RoleCode;
import cl.dayflow.api.user.infrastructure.persistence.RoleEntity;
import cl.dayflow.api.user.infrastructure.persistence.RoleJpaRepository;
import cl.dayflow.api.user.infrastructure.persistence.UserEntity;
import cl.dayflow.api.user.infrastructure.persistence.UserJpaRepository;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserJpaRepository userRepository,
            RoleJpaRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserEmailException(email);
        }
        UserEntity user = UserEntity.create(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                resolveRoles(request.roles()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(String email, Boolean active, Pageable pageable) {
        Page<UserEntity> users = active == null
                ? userRepository.findByEmailContainingIgnoreCase(email == null ? "" : email.trim(), pageable)
                : userRepository.findByEmailContainingIgnoreCaseAndActive(email == null ? "" : email.trim(), active,
                        pageable);
        return users.map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        UserEntity user = findUser(id);
        String email = normalizeEmail(request.email());
        userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new DuplicateUserEmailException(email);
                });
        user.update(email, request.firstName(), request.lastName(), resolveRoles(request.roles()));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse changeStatus(Long id, ChangeUserStatusRequest request) {
        UserEntity user = findUser(id);
        user.changeActiveStatus(request.active());
        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        findUser(id).changePassword(passwordEncoder.encode(request.password()));
    }

    @Transactional
    public void deactivate(Long id) {
        findUser(id).changeActiveStatus(false);
    }

    private UserEntity findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private Set<RoleEntity> resolveRoles(Set<RoleCode> roleCodes) {
        List<RoleEntity> roles = roleRepository.findByCodeIn(roleCodes);
        if (roles.size() != roleCodes.size()) {
            throw new InvalidRoleAssignmentException();
        }
        return Set.copyOf(roles);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
