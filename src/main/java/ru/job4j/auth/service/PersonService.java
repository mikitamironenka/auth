package ru.job4j.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    public List<Person> findAll() {
        return StreamSupport.stream(
            this.personRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    public ResponseEntity<Person> findById(int id) {
        var person = this.personRepository.findById(id);
        return new ResponseEntity<Person>(
            person.orElse(new Person()),
            person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    public ResponseEntity<Person> save(Person person) {
        return new ResponseEntity<Person>(
            this.personRepository.save(person),
            HttpStatus.CREATED
        );
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }
}
