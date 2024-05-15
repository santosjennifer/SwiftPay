package com.github.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.controller.payload.UserRequest;
import com.github.dto.UserBalanceDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;
import com.github.enums.UserType;
import com.github.exception.BusinessException;
import com.github.exception.UserNotFoundException;
import com.github.service.UserService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;
    
	String USER_API = "/api/user";
	
	Long id = 1L;
	String name = "Maria da Silva";
	String document = "000.000.000-00";
	String email = "maria@gmail.com";
	String password = "123";
	UserType userType = UserType.CONSUMER;
	BigDecimal balance = new BigDecimal(100);
    
	@Test
	@DisplayName("Deve cadastrar um usuário tipo consumer")
	public void createUserCustomerTest() throws Exception {
        UserDto mockUserDto = new UserDto(id, name, document, email, password, userType, balance);
        when(service.createUser(any(UserDto.class))).thenReturn(mockUserDto);
        
        String json = new ObjectMapper().writeValueAsString(createUserRequest());
        
        mockMvc.perform(post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isCreated())
        		.andExpect(jsonPath("id").value(id))
        		.andExpect(jsonPath("name").value(name))
        		.andExpect(jsonPath("document").value(document))
        		.andExpect(jsonPath("email").value(email))
        		.andExpect(jsonPath("userType").value(userType.toString()))
        		.andExpect(jsonPath("balance").value(balance))
        		.andReturn();
       
        verify(service, times(1)).createUser(any(UserDto.class));
	}
	
    @Test
    @DisplayName("Deve retornar todos os usuários")
    public void getAllUsersTest() throws Exception{
        UserDto user = new UserDto(id, name, document, email, password, userType, balance);
        
        when(service.findUsers(Mockito.any(Pageable.class))).thenReturn(
        		new PageImpl<UserDto>(Arrays.asList(user), PageRequest.of(0,10), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(id))
            .andExpect(jsonPath("$.[0].name").value(name))
            .andExpect(jsonPath("$.[0].document").value(document))
            .andExpect(jsonPath("$.[0].email").value(email))
            .andExpect(jsonPath("$.[0].userType").value(userType.toString()))
            .andExpect(jsonPath("$.[0].balance").value(balance));
    }
    
    @Test
    @DisplayName("Deve retornar uma lista vazia quando não existir nenhum usuário")
    public void noUsersTest() throws Exception {
    	when(service.findUsers(Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
    	
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
    
    @Test
    @DisplayName("Deve retornar os dados do usuário tipo seller")
    public void getUserSellerTest() throws Exception{
    	String document = "00.000.000/0001-00";
    	UserType userType = UserType.SELLER;
        UserDto user = new UserDto(id, name, document, email, password, userType, balance);

        BDDMockito.given(service.findUserById(id) ).willReturn(Optional.of(user));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isOk())
    		.andExpect(jsonPath("id").value(id))
    		.andExpect(jsonPath("name").value(name))
    		.andExpect(jsonPath("document").value(document))
    		.andExpect(jsonPath("email").value(email))
    		.andExpect(jsonPath("userType").value(userType.toString()))
    		.andExpect(jsonPath("balance").value(balance));
    }
	
    @Test
    @DisplayName("Deve retornar status not found quando o usuário procurado não existir")
    public void userNotFoundTest() throws Exception {
    	BDDMockito.given(service.findUserById(Mockito.anyLong())).willThrow(new UserNotFoundException(id));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value("Registro não encontrado para o id " + id));
    }
    
    @Test
    @DisplayName("Não deve permitir cadastrar quando e-mail ou CPF/CNPJ já estiverem cadastrados")
    public void documentOrEmailAlreadyExistsTest() throws Exception {
	    when(service.createUser(any(UserDto.class))).thenThrow(new BusinessException.DocumentOrEmailAlreadyExistsException());
		
		String json = new ObjectMapper().writeValueAsString(createUserRequest());

        mockMvc.perform(post(USER_API)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("CPF/CNPJ ou e-mail já cadastrado."))
                .andReturn();
    }
    
	@Test
	@DisplayName("Deve fazer deposito para um usuário")
	public void addCreditTest() throws Exception {
		Long user = 4l;
		BigDecimal amount = BigDecimal.TEN;
		
        UserBalanceDto userBalanceDto = new UserBalanceDto(user, amount);
        when(service.updateUserBalance(anyLong(), any(BigDecimal.class), eq(TransactionType.CREDIT))).thenReturn(userBalanceDto);

        String json = new ObjectMapper().writeValueAsString(userBalanceDto);
        
        mockMvc.perform(post(USER_API.concat("/deposit"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("user").value(user))
        		.andExpect(jsonPath("amount").value(amount))
        		.andReturn();
       
        verify(service, times(1)).updateUserBalance(anyLong(), any(BigDecimal.class), eq(TransactionType.CREDIT));
	}
	
	@Test
	@DisplayName("Deve retornar mensagem ao tentar fazer um deposito com valor inválido")
	public void addCreditWithInvalidAmountTest() throws Exception {
		Long user = 4l;
		BigDecimal amount = BigDecimal.ZERO;
		
        UserBalanceDto userBalanceDto = new UserBalanceDto(user, amount);
        when(service.updateUserBalance(anyLong(), any(BigDecimal.class), eq(TransactionType.CREDIT))).thenReturn(userBalanceDto);

        String json = new ObjectMapper().writeValueAsString(userBalanceDto);
        
        mockMvc.perform(post(USER_API.concat("/deposit"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        		.andExpect(status().isUnprocessableEntity())
        		.andExpect(jsonPath("message").value("O valor deve ser no mínimo igual a 0.01."))
        		.andReturn();
       
        verify(service, times(0)).updateUserBalance(anyLong(), any(BigDecimal.class), eq(TransactionType.CREDIT));
	}
	
	@Test
	@DisplayName("Deve retornar mensagem ao tentar cadastrar usuário com userType inválido")
	public void createUserWithInvalidJsonTest() throws Exception {
		when(service.createUser(any(UserDto.class))).thenAnswer(invocation -> {
		    UserDto argument = invocation.getArgument(0);
		    return argument;
		});

	    String invalidJson = "{\"id\": 1, \"name\": \"Maria da Silva\", \"document\": \"000.000.000-00\", \"userType\": \"ALGO\", \"email\": \"maria@gmail.com\", \"password\": \"123\", \"balance\": 100}";

	    mockMvc.perform(post(USER_API)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidJson))
	            .andExpect(status().isUnprocessableEntity())
	            .andExpect(jsonPath("message").value("Valores aceitos para o tipo de usuário: CONSUMER, SELLER"));
	}
    
    private UserRequest createUserRequest() {
		UserRequest request = new UserRequest();
        request.setName(name);
        request.setDocument(document);
        request.setEmail(email);
        request.setPassword(password);
        request.setUserType(userType);
        request.setBalance(balance);
        
    	return request;
    }
    
}
