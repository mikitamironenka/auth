package ru.job4j.auth.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.model.Employee;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.model.Report;
import ru.job4j.auth.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


//1. Добавьте модель данных Employee - сотрудник компании.
//2. Модель Employee содержит обязательно данные: имя и фамилия, ИНН, дата найма,
// а также ссылку на список его аккаунтов (в качестве аккаунтов используйте модель Person,
// в котором используются поля login и пароль), которыми сотрудник пользуется
// для доступа к ресурсам корпоративной площадки.
//3. Добавьте контроллер EmployeeController,  в котором добавьте метод получения всех сотрудников
// со всеми их аккаунтами, добавление нового аккаунта, изменение и удаление существующих аккаунтов.


@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private RestTemplate rest;
    private static final String API = "http://localhost:8080/person/";
    private static final String API_ID = "http://localhost:8080/person/{id}";

    private final PersonService personService;

    public EmployeeController(final PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/")
    public List<Employee> findAll() {
        List<Employee> rsl = new ArrayList<>();
        personService.findAllEmployees().forEach(rsl :: add);
        return rsl;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        return personService.findEmployeeById(id);
    }

    @PostMapping(value = "/", produces = { "application/json", "application/xml" })
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return personService.saveEmployee(employee);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Employee employee) {
        this.personService.updateEmployee(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Employee employee = new Employee();
        employee.setId(id);
        this.personService.deleteEmployee(employee);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/account/{id}")
    public ResponseEntity<Person> createAccount(@RequestBody Person person, @PathVariable int id) {
        Employee employee = personService.findEmployeeById(id)
            .getBody();
        employee.getPersons() //
            .add(person);
        personService.saveEmployee(employee);
        return new ResponseEntity<>(
            employee != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND
        );
    }

    @PutMapping("/updateaccount/")
    public ResponseEntity<Void> updateAccount(@RequestBody Person person) {
        rest.put(API, person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id, @PathVariable Person person) {
        Employee employee = personService.findEmployeeById(id)
            .getBody();
        employee.getPersons() //
            .remove(person);
        personService.saveEmployee(employee);
        rest.delete(API_ID, person.getId());
        return new ResponseEntity<>(
            employee != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND
        );
    }

}
