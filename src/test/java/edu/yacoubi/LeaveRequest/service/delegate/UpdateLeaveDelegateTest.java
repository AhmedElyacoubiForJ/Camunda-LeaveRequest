package edu.yacoubi.LeaveRequest.service.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UpdateLeaveDelegateTest {

    private UpdateLeaveDelegate delegate;
    private DelegateExecution execution;

    @BeforeEach
    void setUp() {
        delegate = new UpdateLeaveDelegate();
        execution = mock(DelegateExecution.class);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(execution);
    }

    @Test
    void shouldUpdateLeaveSuccessfully() throws Exception {
        when(execution.getVariable("requestedVacationDays")).thenReturn(5L);
        when(execution.getVariable("remainingVacationDays")).thenReturn(20L);
        when(execution.getVariable("leaveStatus")).thenReturn("Pending");

        delegate.execute(execution);

        verify(execution).setVariable("remainingVacationDays", 15);
        verify(execution).setVariable("leaveStatus", "Updated");
    }

    @Test
    void shouldThrowExceptionWhenRequiredVariablesAreMissing() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> delegate.execute(execution));

        assertEquals("Required process variable 'requestedVacationDays' is missing.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestedVacationDaysIsMissing() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(null);
        when(execution.getVariable("remainingVacationDays")).thenReturn(20L);
        when(execution.getVariable("leaveStatus")).thenReturn("Pending");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> delegate.execute(execution));

        assertEquals("Required process variable 'requestedVacationDays' is missing.", exception.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenVacationDaysAreNegative() {
        when(execution.getVariable("requestedVacationDays")).thenReturn(-5L);
        when(execution.getVariable("remainingVacationDays")).thenReturn(20L);
        when(execution.getVariable("leaveStatus")).thenReturn("Pending");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> delegate.execute(execution));

        assertEquals("Vacation days must be non-negative.", exception.getMessage());
    }


}