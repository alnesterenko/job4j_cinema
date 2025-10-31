package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.user.Sql2oUserRepository;
import ru.job4j.cinema.repository.user.UserRepository;
import ru.job4j.cinema.service.user.SimpleUserService;
import ru.job4j.cinema.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SimpleUserServiceTest {

    private static UserService userService;

    private static User firstTestUser;

    private static User secondTestUser;

    private static UserRepository mockUserRepository;

    @BeforeAll
    public static void initTestRepository() {
        mockUserRepository = mock(Sql2oUserRepository.class);
        firstTestUser = new User(1, "Филип Морис", "fil@gmail.com", "1234");
        secondTestUser = new User(2, "John Doe", "john@mail.ru", "admin123");
        when(mockUserRepository.findById(1)).thenReturn(Optional.of(firstTestUser));
        when(mockUserRepository.findById(2)).thenReturn(Optional.of(secondTestUser));
        when(mockUserRepository.findAll()).thenReturn(List.of(firstTestUser, secondTestUser));

        userService = new SimpleUserService(mockUserRepository);
    }

    @Test
    public void whenRequestOneUserThenGetSameUser() {
        var optionalFirstUser = userService.findUserById(1);

        assertThat(optionalFirstUser.isPresent()).isTrue();
        assertThat(optionalFirstUser.get()).usingRecursiveComparison().isEqualTo(firstTestUser);
    }

    @Test
    public void whenRequestListOfUsersThenGetCorrectList() {
        var userList = new ArrayList<>(userService.findAllUsers());

        assertThat(userList.size()).isEqualTo(2);
        assertThat(userList.get(0)).usingRecursiveComparison().isEqualTo(firstTestUser);
        assertThat(userList.get(1)).usingRecursiveComparison().isEqualTo(secondTestUser);
    }

    @Test
    public void whenListOfUsersNotContainsWrongUser() {
        var userList = new ArrayList<>(userService.findAllUsers());

        assertThat(userList.contains(new User(2, "Непонятный тип", "sk@gmail.ru", "000"))).isFalse();
    }

    @Test
    public void whenSaveManyTimesOneUser() {
        when(mockUserRepository.save(firstTestUser)).thenReturn(Optional.ofNullable(firstTestUser)).thenReturn(Optional.empty());

        var resultOfFirstTrySaveUser = userService.saveUser(firstTestUser);
        var resultOfSecondTrySaveUser = userService.saveUser(firstTestUser);
        var resultOfThirdTrySaveUser = userService.saveUser(firstTestUser);

        assertThat(resultOfFirstTrySaveUser.isPresent()).isTrue();
        assertThat(resultOfFirstTrySaveUser.get()).usingRecursiveComparison().isEqualTo(firstTestUser);
        assertThat(resultOfSecondTrySaveUser).isEqualTo(resultOfThirdTrySaveUser).isEmpty();
    }

    @Test
    public void whenFindUserByEmailAndPasswordAndGetCorrectUser() {
        when(mockUserRepository.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.empty());
        when(mockUserRepository.findByEmailAndPassword("fil@gmail.com", "1234")).thenReturn(Optional.ofNullable(firstTestUser));
        when(mockUserRepository.findByEmailAndPassword("john@mail.ru", "admin123")).thenReturn(Optional.ofNullable(secondTestUser));

        var resultOfFirstFindByEmailAndPassword = userService.findUserByEmailAndPassword("fil@gmail.com", "1234");
        var resultOfSecondFindByEmailAndPassword = userService.findUserByEmailAndPassword("john@mail.ru", "admin123");
        var resultOfThirdFindByEmailAndPassword = userService.findUserByEmailAndPassword("a@mail.ru", "123");
        var resultOfFourthFindByEmailAndPassword = userService.findUserByEmailAndPassword("fil@gmail.com", "1234");

        assertThat(resultOfFirstFindByEmailAndPassword.isPresent()).isTrue();
        assertThat(resultOfFirstFindByEmailAndPassword.get()).usingRecursiveComparison().isEqualTo(firstTestUser);
        assertThat(resultOfSecondFindByEmailAndPassword.isPresent()).isTrue();
        assertThat(resultOfSecondFindByEmailAndPassword.get()).usingRecursiveComparison().isEqualTo(secondTestUser);
        assertThat(resultOfThirdFindByEmailAndPassword).isEmpty();
        assertThat(resultOfFourthFindByEmailAndPassword.isPresent()).isTrue();
    }

    @Test
    public void whenManyDeleteUserById() {
        when(mockUserRepository.deleteById(any(Integer.class))).thenReturn(false);
        when(mockUserRepository.deleteById(1)).thenReturn(true);
        when(mockUserRepository.deleteById(2)).thenReturn(true);

        var resultOfFirstDeleteUserById = userService.deleteUserById(1);
        var resultOfSecondDeleteUserById = userService.deleteUserById(2);
        var resultOfThirdDeleteUserById = userService.deleteUserById(3);

        assertThat(resultOfFirstDeleteUserById).isTrue();
        assertThat(resultOfSecondDeleteUserById).isTrue();
        assertThat(resultOfThirdDeleteUserById).isFalse();
    }

    @Test
    public void whenAgainDeleteUserById() {
        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        int[] callCount = {0};
        doAnswer(invocation -> {
                    int param = invocation.getArgument(0);
                    callCount[0]++;
                    if (callCount[0] == 1) {
                        return true;
                    }
                    return false;
                }
        ).when(mockUserRepository).deleteById(1);

        var resultOfFirstTimeDeleteUserById1 = userService.deleteUserById(1);
        var resultOfSecondTimeDeleteUserById1 = userService.deleteUserById(1);

        assertThat(resultOfFirstTimeDeleteUserById1).isTrue();
        assertThat(resultOfSecondTimeDeleteUserById1).isFalse();
    }
}