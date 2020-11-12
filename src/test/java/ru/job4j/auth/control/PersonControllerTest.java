package ru.job4j.auth.control;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PersonService personService;
    private List<Person> personList;
    private int personTestId = 4;
    private String personTestLogin = "user4";
    private String personTestPass = "pass4";

    @BeforeEach
    void setUp() {
        this.personList = new ArrayList<>();
        this.personList.add(new Person(1, "User1", "pwd1"));
        this.personList.add(new Person(2, "User2", "pwd2"));
        this.personList.add(new Person(3, "User3", "pwd3"));
    }


    @Test
    public void whenReturnAllPersons() throws Exception {
       given(personService.findAll()).willReturn(personList);
       this.mockMvc.perform(get("/person/"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.size()", is(personList.size())));
    }

    @Test
    public void whenReturnPersonById() throws Exception {
        final int personId = 1;
        final Person person = new Person(1, "User1", "pwd1");
        when(personService.findById(anyInt())).thenReturn(ResponseEntity.of(Optional.of(person)));
        this.mockMvc.perform(get("/person/{id}", personId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login", is(person.getLogin())))
            .andExpect(jsonPath("$.password", is(person.getPassword())));
    }

    @Test
    public void whenCreateNewPerson() throws Exception {
        Person person = new Person(personTestId, personTestLogin, personTestPass);
        when(personService.save(any(Person.class))).thenReturn(ResponseEntity.of(Optional.of(person)));
        this.mockMvc.perform(post("/person/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(person))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login", is(person.getLogin())))
            .andExpect(jsonPath("$.password", is(person.getPassword())));
    }

    @Test
    public void whenDeletePerson() throws Exception {
        Person person = new Person(personTestId, personTestLogin, personTestPass);
        when(personService.findById(anyInt())).thenReturn(ResponseEntity.of(Optional.of(person)));
        doNothing().when(personService).delete(person);
        this.mockMvc.perform(delete("/person/{id}", person.getId())        )
            .andExpect(status().isOk());
    }

    @Test
    public void whenUpdatePerson() throws Exception {
        Person person = new Person(personTestId, personTestLogin, personTestPass);
        mockMvc.perform(put("/person/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(person)))
            .andDo(print())
            .andExpect(status().isOk());
        verify(personService).save(person);
        PersonController controller = new PersonController(personService);
        assertThat(controller.update(person), is(ResponseEntity.ok().build()));
    }
}