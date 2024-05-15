package com.github.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.github.dto.UserBalanceDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;
import com.github.enums.UserType;
import com.github.exception.BusinessException;
import com.github.exception.UserNotFoundException;
import com.github.model.User;
import com.github.repository.UserRepository;
import com.github.service.UserService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

	@Mock
	private UserRepository repository;

	private UserService service;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new UserServiceImpl(repository);
	}
	
	Long id = 1L;
	String name = "João Paulo";
	String document = "000.000.000-00";
	String email = "joaopaulo@gmail.com";
	String password = "1234";
	UserType userType = UserType.CONSUMER;
	BigDecimal balance = BigDecimal.TEN;
	
    @Test
    @DisplayName("Deve criar usuário")
    public void createUserTest() {
        UserDto user = createUserDto();
        when(repository.existsByDocument(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(user.toUser());

        UserDto savedUser = service.createUser(user);

        assertNotNull(savedUser);
        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getDocument(), savedUser.getDocument());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getPassword(), savedUser.getPassword());
        assertEquals(user.getUserType(), savedUser.getUserType());
        assertEquals(user.getBalance(), savedUser.getBalance());     
    }
    
    @Test
    @DisplayName("Deve validar usuário com CPF/CNPJ já existente")
    public void createUserDuplicateDocumentTest() {
        UserDto user = createUserDto();
        when(repository.existsByDocument(anyString())).thenReturn(true);

        assertThrows(BusinessException.DocumentOrEmailAlreadyExistsException.class, () -> service.createUser(user));
    }
    
    @Test
    @DisplayName("Deve validar usuário com e-mail já existente")
    public void createUserDuplicateEmailTest() {
        UserDto user = createUserDto();
        when(repository.existsByDocument(anyString())).thenReturn(false);
        when(repository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BusinessException.DocumentOrEmailAlreadyExistsException.class, () -> service.createUser(user));
    }
    
    @Test
    @DisplayName("Deve retornar uma lista de usuários")
    public void findUsersTest() {
        Pageable pageable = mock(Pageable.class);
        Page<User> userPage = new PageImpl<>(createUserList());
        when(repository.findAll(pageable)).thenReturn(userPage);

        Page<UserDto> users = service.findUsers(pageable);

        assertNotNull(users);
        assertEquals(userPage.getTotalElements(), users.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar usuário por id")
    public void findUserByIdTest() {
        User user = createUserDto().toUser();
        when(repository.findById(id)).thenReturn(Optional.of(user));

        Optional<UserDto> foundUser = service.findUserById(id);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
    }
    
    @Test
    @DisplayName("Deve retornar usuário não encontrado")
    public void userNotFoundTest() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findUserById(id));
    }

    @Test
    @DisplayName("Deve creditar ao saldo do usuário")
    public void updatedUserBalanceCreditTest() {
        User user = createUserDto().toUser();

        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(user.getId());
            return savedUser;
        });

        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserBalanceDto updatedUserBalance = 
        		service.updateUserBalance(user.getId(), BigDecimal.TEN, TransactionType.CREDIT);
        
        User updatedUser = repository.findById(id).orElse(null);
        assertNotNull(updatedUser);
        assertNotNull(updatedUserBalance);
        assertEquals(updatedUser.getId(), updatedUserBalance.getUser());
        assertEquals(updatedUser.getBalance(), new BigDecimal(20));
        assertEquals(updatedUserBalance.getAmount(), BigDecimal.TEN);
    }
    
    @Test
    @DisplayName("Deve debitar do saldo do usuário")
    public void updatedUserBalanceDebitTest() {
        User user = createUserDto().toUser();

        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(user.getId());
            return savedUser;
        });

        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserBalanceDto updatedUserBalance = 
                service.updateUserBalance(user.getId(), BigDecimal.TEN, TransactionType.DEBIT);

        User updatedUser = repository.findById(id).orElse(null);
        assertNotNull(updatedUser);
        assertNotNull(updatedUserBalance);
        assertEquals(updatedUser.getId(), updatedUserBalance.getUser());
        assertEquals(updatedUser.getBalance(), BigDecimal.ZERO);
        assertEquals(updatedUserBalance.getAmount(), BigDecimal.TEN);
    }
    
    private UserDto createUserDto() {
        UserDto userDto = new UserDto(id, name, document, email, password, userType, balance);
        return userDto;
    }

    private User createUser() {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setDocument(document);
        user.setBalance(balance);
        user.setUserType(userType);
        user.setPassword(password);
        return user;
    }
    
    private List<User> createUserList() {
        List<User> userList = new ArrayList<>();
        userList.add(createUser());
        return userList;
    }

}
