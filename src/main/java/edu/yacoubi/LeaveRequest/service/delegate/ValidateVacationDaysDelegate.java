package edu.yacoubi.LeaveRequest.service.delegate;


import edu.yacoubi.LeaveRequest.util.VacationValidator;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static edu.yacoubi.LeaveRequest.util.ValidationConstants.VACATION_DAYS_INVALID_TYPE;
import static edu.yacoubi.LeaveRequest.util.ValidationConstants.VALIDATION_ERROR;


@Slf4j
@Component("validateVacationDaysDelegate")
public class ValidateVacationDaysDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logExecutionDetails(execution);
        validateVacationDays(execution);
    }

    private void validateVacationDays(DelegateExecution execution) {
        Long requestedVacationDays = (Long) execution.getVariable("requestedVacationDays");
        Long remainingVacationDays = (Long) execution.getVariable("remainingVacationDays");

        VacationValidator.validate(requestedVacationDays, remainingVacationDays);

        log.info("Vacation days validated successfully for processInstanceId={}, requestedVacationDays={}, remainingVacationDays={}",
                execution.getProcessInstanceId(), requestedVacationDays, remainingVacationDays);
    }

    private Integer getRequiredIntegerVariable(DelegateExecution execution, String variableName) {
        Object variable = execution.getVariable(variableName);
        if (variable == null) {
            throw new BpmnError(VALIDATION_ERROR, variableName + " is not set!");
        }
        if (!(variable instanceof Integer)) {
            throw new BpmnError(VALIDATION_ERROR, variableName + " is not an integer!");
        }
        return (Integer) variable;
    }

    private void logExecutionDetails(DelegateExecution execution) {
        log.info("Process details: activityName='{}', activityId='{}', processInstanceId='{}', executionId='{}', variables='{}'",
                execution.getCurrentActivityName(),
                execution.getCurrentActivityId(),
                execution.getProcessInstanceId(),
                execution.getId(),
                execution.getVariables()
        );
    }
}