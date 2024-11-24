package edu.yacoubi.LeaveRequest.service.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("updateLeaveDelegate")
public class UpdateLeaveDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logExecutionDetails(execution);
        updateLeave(execution);
    }

    private void updateLeave(DelegateExecution execution) {
        Long requestedVacationDays = (Long) execution.getVariable("requestedVacationDays");
        Long remainingVacationDays = (Long) execution.getVariable("remainingVacationDays");
        String leaveStatus = (String) execution.getVariable("leaveStatus");

        if (requestedVacationDays == null) {
            log.error("Missing required variable: 'requestedVacationDays'.");
            throw new IllegalArgumentException("Required process variable 'requestedVacationDays' is missing.");
        }

        if (remainingVacationDays == null) {
            log.error("Missing required variable: 'remainingVacationDays'.");
            throw new IllegalArgumentException("Required process variable 'remainingVacationDays' is missing.");
        }

        if (leaveStatus == null) {
            log.error("Missing required variable: 'leaveStatus'.");
            throw new IllegalArgumentException("Required process variable 'leaveStatus' is missing.");
        }


        if (remainingVacationDays < 0 || requestedVacationDays < 0) {
            log.error("Vacation days cannot be negative: remainingVacationDays={}, requestedVacationDays={}",
                    remainingVacationDays, requestedVacationDays);
            throw new IllegalArgumentException("Vacation days must be non-negative.");
        }

        int newRemainingDays = remainingVacationDays.intValue() - requestedVacationDays.intValue();

        execution.setVariable("remainingVacationDays", newRemainingDays);
        execution.setVariable("leaveStatus", "Updated");

        log.info("Leave updated successfully. remainingVacationDays={}, leaveStatus={}", newRemainingDays, "Updated");
    }

    private void logExecutionDetails(DelegateExecution execution) {
        log.info("Update Leave Process details: activityName='{}', activityId='{}', processInstanceId='{}', executionId='{}', variables='{}'",
                execution.getCurrentActivityName(),
                execution.getCurrentActivityId(),
                execution.getProcessInstanceId(),
                execution.getId(),
                execution.getVariables()
        );
    }
}
