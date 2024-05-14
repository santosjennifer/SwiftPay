package com.github.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.model.User;
import com.github.model.enums.UserType;

import jakarta.validation.ConstraintViolationException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository repository;
    
	String name = "Giovanni Bertelli";
	String document = "807.819.370-00";
	String email = "giovanni.bertelli@gmail.com";
	String password = "123456";
	UserType userType = UserType.CONSUMER;
	BigDecimal balance = new BigDecimal(10);

    @Test
	@DisplayName("Deve salvar um usuário tipo CONSUMER com sucesso")
    public void saveUserConsumerTest() {
        User user = new User();
        user.setName(name);
        user.setDocument(document);
        user.setEmail(email);
        user.setPassword(password);
        user.setBalance(balance);
        user.setUserType(userType);
        
        User saveUser = repository.save(user);
        
        assertNotNull(saveUser);
        assertNotNull(saveUser.getId());
        assertEquals(name, saveUser.getName());
        assertEquals(document, saveUser.getDocument());
        assertEquals(email, saveUser.getEmail());
        assertEquals(password, saveUser.getPassword());
        assertEquals(balance, saveUser.getBalance());
        assertEquals(userType, saveUser.getUserType());
    }
    
    @Test
    @DisplayName("Deve lançar uma exceção ao salvar um usuário com CPF inválido")
    public void saveUserWithInvalidCPFTest() {
        String invalidCPF = "000.000.000-00";

        User user = new User();
        user.setName(name);
        user.setDocument(invalidCPF);
        user.setEmail(email);
        user.setPassword(password);
        user.setBalance(balance);
        user.setUserType(userType);

        ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class, () -> repository.save(user)
        );

        assertTrue(exception.getMessage().contains("O CPF deve ser válido."));
    }
    
    @Test
    @DisplayName("Deve retornar verdadeiro ao pesquisar se o documento já existe")
    public void existsByDocumentTest() {
    	Long id = 10L;
        String cpf = "49297899072";
        String email = "emailteste@yahoo.com.br";
        User user = new User(id, name, cpf, email, password, userType, balance);
        
        repository.save(user);

        boolean result = repository.existsByDocument(cpf);

        assertTrue(result);
    }
    
    @Test
    @DisplayName("Deve retornar falso ao pesquisar se o documento já existe")
    public void notExistsByDocumentTest() {
        String cpf = "87645182059";

        boolean result = repository.existsByDocument(cpf);

        assertFalse(result);
    }
    
    @Test
    @DisplayName("Deve retornar verdadeiro ao pesquisar se o e-mail já existe")
    public void existsByEmailTest() {
    	Long id = 11L;
        String cpf = "80283673052";
        String email = "email.teste@yahoo.com";
        User user = new User(id, name, cpf, email, password, userType, balance);

        repository.save(user);
        
        boolean result = repository.existsByEmail(email);

        assertTrue(result);
    }
    
    @Test
    @DisplayName("Deve retornar falso ao pesquisar se o e-mail já existe")
    public void notExistsByEmailTest() {
        String email = "teste.email@bol.com";

        boolean result = repository.existsByEmail(email);

        assertFalse(result);
    }
    
    @Test
	@DisplayName("Deve salvar um usuário tipo SELLER com sucesso")
    public void saveUserSellerTest() {
        String document = "44.208.349/0001-99";
        String email = "seller.email@gmail.com";
        UserType userType = UserType.SELLER;
        
        User user = new User();
        user.setName(name);
        user.setDocument(document);
        user.setEmail(email);
        user.setPassword(password);
        user.setBalance(balance);
        user.setUserType(userType);
        
        User saveUser = repository.save(user);
        
        User findUser = repository.findById(saveUser.getId()).orElse(null);
        assertNotNull(findUser);
        assertEquals(name, findUser.getName());
        assertEquals(document, findUser.getDocument());
        assertEquals(email, findUser.getEmail());
        assertEquals(password, findUser.getPassword());
        assertEquals(balance, findUser.getBalance());
        assertEquals(userType, findUser.getUserType());
    }
    
    @Test
    @DisplayName("Deve lançar uma exceção ao salvar um usuário com CNPJ inválido")
    public void saveUserWithInvalidCNPJTest() {
        String invalidCNPJ = "00.000.000/0001-00";
        UserType userType = UserType.SELLER;

        User user = new User();
        user.setName(name);
        user.setDocument(invalidCNPJ);
        user.setEmail(email);
        user.setPassword(password);
        user.setBalance(balance);
        user.setUserType(userType);

        ConstraintViolationException exception = assertThrows(
            ConstraintViolationException.class, () -> repository.save(user)
        );

        assertTrue(exception.getMessage().contains("O CNPJ deve ser válido."));
    }
    
}
