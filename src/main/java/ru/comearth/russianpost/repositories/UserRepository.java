package ru.comearth.russianpost.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.comearth.russianpost.domain.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

        Optional<User> findByLogin(String login);

}
