package ru.job4j.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Employee;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.EmployeeRepository;
import ru.job4j.auth.repository.PersonRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;

    public PersonService(final PersonRepository personRepository, EmployeeRepository employeeRepository) {
        this.personRepository = personRepository;
        this.employeeRepository = employeeRepository;
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

    public List<Employee> findAllEmployees() {
        return StreamSupport.stream(
            this.employeeRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    public ResponseEntity<Employee> findEmployeeById(int id) {
        var employee = this.employeeRepository.findById(id);
        return new ResponseEntity<Employee>(
            employee.orElse(new Employee()),
            employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    public ResponseEntity<Employee> saveEmployee(Employee employee) {
        employee.setHiringDate(new Timestamp(new Date().getTime()));
        return new ResponseEntity<Employee>(
            this.employeeRepository.save(employee),
            HttpStatus.CREATED
        );
    }

    public void updateEmployee(Employee employee) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());
        if (employeeOptional.isPresent()){
            Employee emplTemp = employeeOptional.get();
            if (employee.getFirstName() != null)
                emplTemp.setFirstName(employee.getFirstName());
            if (employee.getLastName() != null)
                emplTemp.setLastName(employee.getLastName());
            this.employeeRepository.save(emplTemp);
        }
    }

    public void deleteEmployee(Employee employee) {
        employeeRepository.delete(employee);
    }
}
