package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    private final static User USER = new User("test1", "test1@mail.ru", "123");

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    /* Тестируем getRegistrationPage() */
    @Test
    public void whenRequestRegistrationPageThenGetRegistrationPage() {

        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    /* Тестируем register() */
    @Test
    public void whenRegisterNewUserThenSameDataAndRedirectToLoginPage() {
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.saveUser(userArgumentCaptor.capture())).thenReturn(Optional.of(USER));

        var model = new ConcurrentModel();
        var view = userController.register(model, USER);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/users/login");
        assertThat(USER).isEqualTo(actualUser);
    }

    @Test
    public void whenRegisterNotNewUserThenGetErrorPageWithMessage() {
        when(userService.saveUser(any(User.class))).thenReturn(Optional.empty());
        String expectedMessage = "Пользователь с такой почтой уже существует";

        var model = new ConcurrentModel();
        var view = userController.register(model, USER);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("messages/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedMessage);
    }

    /* Тестируем getLoginPage() */
    @Test
    public void whenRequestLoginPageThenGetLoginPage() {

        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    /* Тестируем loginUser() */
    @Test
    public void whenLoginUserThenSameDataAndRedirectToMoviesPage() {
        when(userService.findUserByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.of(USER));
        var request = new MockHttpServletRequest();

        var model = new ConcurrentModel();
        var view = userController.loginUser(USER, model, request);
        var session = request.getSession();
        var returnedUser = session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/movies");
        assertThat(USER).isEqualTo(returnedUser);
    }

    @Test
    public void whenLoginUserThenSameDataAndRedirectToCorrectMoviePage() {
        when(userService.findUserByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.of(USER));
        var request = new MockHttpServletRequest();
        var filmSessionId = 352;
        request.getSession().setAttribute("filmSessionId", filmSessionId);

        var model = new ConcurrentModel();
        var view = userController.loginUser(USER, model, request);
        var session = request.getSession();
        var returnedUser = session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/tickets/" + session.getAttribute("filmSessionId"));
        assertThat(USER).isEqualTo(returnedUser);
    }

    @Test
    public void whenUsingWrongEmailOrPasswordToLoginThenGetErrorPageWithMessage() {
        when(userService.findUserByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.empty());
        var request = new MockHttpServletRequest();
        String expectedMessage = "Почта или пароль введены неверно";

        var model = new ConcurrentModel();
        var view = userController.loginUser(USER, model, request);
        var actualExceptionMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualExceptionMessage).isEqualTo(expectedMessage);
    }

    /* Тестируем logout() */
    @Test
    public void whenUseLogoutThenSessionDestroyAndRedirectToLoginPage() {
        var request = new MockHttpServletRequest();
        var session = request.getSession();
        session.setAttribute("user", USER);

        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
        assertThat(request.getSession(false)).isNull();
    }
}