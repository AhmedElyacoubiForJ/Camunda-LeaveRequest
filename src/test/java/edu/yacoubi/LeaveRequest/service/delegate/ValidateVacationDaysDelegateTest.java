package edu.yacoubi.LeaveRequest.service.delegate;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidateVacationDaysDelegateTest {

    private ValidateVacationDaysDelegate delegate;
    private DelegateExecution execution;

    @BeforeEach
    void setUp() {
        delegate = new ValidateVacationDaysDelegate();
        execution = mock(DelegateExecution.class);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(execution);
    }

    @Test
    void shouldValidateCorrectVacationDays() throws Exception {
        when(execution.getVariable("requestedVacationDays")).thenReturn(5L);
        when(execution.getVariable("remainingVacationDays")).thenReturn(10L);

        delegate.execute(execution);

        verify(execution, times(1)).getVariable("requestedVacationDays");
        verify(execution, times(1)).getVariable("remainingVacationDays");
    }

    @Test
    void shouldThrowErrorForExceedingVacationDays() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(15L);
        when(execution.getVariable("remainingVacationDays")).thenReturn(10L);

        BpmnError exception = assertThrows(BpmnError.class, () -> delegate.execute(execution));
        assertEquals("Requested vacation days exceed remaining vacation days!", exception.getMessage());
    }

    @Test
    void shouldThrowErrorForNegativeVacationDays() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(-5L);
        when(execution.getVariable("remainingVacationDays")).thenReturn(10L);

        BpmnError exception = assertThrows(BpmnError.class, () -> delegate.execute(execution));
        assertEquals("Requested vacation days cannot be negative!", exception.getMessage());
    }

    @Test
    void shouldThrowErrorWhenRequestedVacationDaysAreNull() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(null);
        when(execution.getVariable("remainingVacationDays")).thenReturn(10L);

        BpmnError exception = assertThrows(BpmnError.class, () -> delegate.execute(execution));
        assertEquals("Vacation days or remaining vacation days are not set!", exception.getMessage());
    }

    @Test
    void shouldThrowErrorWhenRemainingVacationDaysAreNull() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(5L);
        when(execution.getVariable("remainingVacationDays")).thenReturn(null);

        BpmnError exception = assertThrows(BpmnError.class, () -> delegate.execute(execution));
        assertEquals("Vacation days or remaining vacation days are not set!", exception.getMessage());
    }
}
