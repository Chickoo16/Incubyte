package com.acme.salary.employee;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    private Employee sampleEmployee() {
        return new Employee(
                "ACME-000001", "Ada", "Lovelace", "ada@acme.example",
                "Engineering", "Software Engineer", "IN", "INR",
                LocalDate.of(2020, 1, 15),
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));
    }

    @Test
    void getByIdReturnsTheEmployee() throws Exception {
        when(employeeService.getById(1L)).thenReturn(sampleEmployee());

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode").value("ACME-000001"))
                .andExpect(jsonPath("$.grossAmount").value(62000.0));
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        when(employeeService.getById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/api/employees/99")).andExpect(status().isNotFound());
    }

    @Test
    void searchReturnsAPageOfEmployees() throws Exception {
        when(employeeService.search(any(EmployeeSearchCriteria.class), any()))
                .thenReturn(new PageImpl<>(List.of(sampleEmployee()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/employees")
                        .param("department", "Engineering")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].employeeCode").value("ACME-000001"));
    }

    @Test
    void createReturns201WithTheCreatedEmployee() throws Exception {
        when(employeeService.create(any(NewEmployee.class))).thenReturn(sampleEmployee());

        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "Ada", "Lovelace", "ada@acme.example", "Engineering", "Software Engineer",
                "IN", "INR", LocalDate.of(2020, 1, 15),
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode").value("ACME-000001"));
    }

    @Test
    void createRejectsInvalidPayloadWith400() throws Exception {
        String invalidJson = """
                {"firstName": "", "lastName": "Lovelace"}
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSalaryReturnsTheUpdatedEmployee() throws Exception {
        when(employeeService.updateSalary(eq(1L), any(), any(), any())).thenReturn(sampleEmployee());

        UpdateSalaryRequest request = new UpdateSalaryRequest(
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));

        mockMvc.perform(put("/api/employees/1/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netAmount").value(59000.0));
    }

    @Test
    void deactivateReturnsTheDeactivatedEmployee() throws Exception {
        Employee employee = sampleEmployee();
        employee.deactivate();
        when(employeeService.deactivate(1L)).thenReturn(employee);

        mockMvc.perform(post("/api/employees/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(employeeService).deactivate(1L);
    }

    @Test
    void reactivateReturnsTheReactivatedEmployee() throws Exception {
        when(employeeService.reactivate(1L)).thenReturn(sampleEmployee());

        mockMvc.perform(post("/api/employees/1/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void filtersReturnsDropdownOptions() throws Exception {
        when(employeeService.filterOptions()).thenReturn(
                new FilterOptions(List.of("Engineering"), List.of("IN"), List.of("Engineer")));

        mockMvc.perform(get("/api/employees/filters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departments[0]").value("Engineering"));
    }
}
