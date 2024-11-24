# **LeaveRequestProcess**

Dieses Dokument beschreibt die Implementierung und den Aufbau des **LeaveRequestProcess**. Ziel ist es, die Genehmigung von Urlaubsanträgen innerhalb eines Geschäftsprozesses zu validieren und zu aktualisieren. Die Implementierung wurde mit **Camunda BPM** und **Spring Boot** realisiert.

---

## **Inhaltsverzeichnis**
1. [Prozessbeschreibung](#prozessbeschreibung)
2. [BPMN-Modell](#bpmn-modell)
3. [Java-Delegates](#java-delegates)
    - [ValidateVacationDaysDelegate](#validatevacationdaysdelegate)
    - [UpdateLeaveDelegate](#updateleavedelegate)
4. [Hilfsklassen](#hilfsklassen)
    - [VacationValidator](#vacationvalidator)
5. [Testfälle](#testfälle)
6. [Startparameter](#startparameter)
7. [Protokollierung](#protokollierung)

---

## **Prozessbeschreibung**
Der Prozess beginnt mit der Überprüfung der eingegebenen Urlaubsdaten und validiert, ob der beantragte Urlaub den verfügbaren Urlaubstagen entspricht. Anschließend wird der Status aktualisiert.

### **Schritte:**
1. **StartEvent**: Initialisiert den Prozess mit den Startvariablen.
2. **Validate Balance**: Überprüft die Eingabedaten (z. B. beantragte und verbleibende Urlaubstage).
3. **Update Leave**: Aktualisiert die verbleibenden Urlaubstage und den Status des Antrags.
4. **EndEvent**: Beendet den Prozess.

---

## **BPMN-Modell**
Das BPMN-Modell enthält folgende wichtige Variablen:
- `leaveType` (String): Art des Urlaubs.
- `requestedVacationDays` (Integer): Anzahl der beantragten Urlaubstage.
- `remainingVacationDays` (Integer): Verfügbare Urlaubstage.
- `leaveStatus` (String): Status des Urlaubsantrags (z. B. „pending“, „approved“, „denied“).
- `approve` (Boolean): Gibt an, ob der Antrag genehmigt wurde.

---

## **Java-Delegates**

### **ValidateVacationDaysDelegate**
- **Paket:** `edu.yacoubi.InvoiceAutomation.service.delegate`
- **Zweck:** Validiert die Urlaubsdaten und wirft bei Fehlern eine BPMN-Fehlermeldung.
- **Hauptmethoden:**
    - `validateVacationDays`: Prüft, ob die eingegebenen Daten korrekt sind.
    - `getRequiredIntegerVariable`: Extrahiert Prozessvariablen und stellt sicher, dass sie Integer-Werte sind.

```java
@Override
public void execute(DelegateExecution execution) throws Exception {
    logExecutionDetails(execution);
    validateVacationDays(execution);
}
```

### **UpdateLeaveDelegate**
- **Paket:** `edu.yacoubi.InvoiceAutomation.service.delegate`
- **Zweck:** Aktualisiert die verbleibenden Urlaubstage und den Status basierend auf der Genehmigung.
- **Hauptmethoden:**
    - `execute`: Berechnet neue Werte für `remainingVacationDays` und `leaveStatus`.

---

## **Hilfsklassen**

### **VacationValidator**
- **Paket:** `edu.yacoubi.InvoiceAutomation.util`
- **Zweck:** Führt Validierungen für die eingegebenen Urlaubsdaten durch.
- **Methoden:**
    - `validate`: Validiert, ob die Daten sinnvoll sind (z. B. keine negativen Werte).
- **Konstanten:**
    - `VALIDATION_ERROR`
    - `VACATION_DAYS_NOT_SET`
    - `NEGATIVE_VACATION_DAYS`
    - `EXCEEDS_REMAINING_DAYS`

```java
if (requestedVacationDays > remainingVacationDays) {
    throw new BpmnError(VALIDATION_ERROR, EXCEEDS_REMAINING_DAYS);
}
```

---

## **Testfälle**

Die folgenden Testfälle wurden implementiert:
1. **Positive Tests:**
    - Validierung von korrekt eingegebenen Urlaubsdaten.
    - Aktualisierung der verbleibenden Urlaubstage.
2. **Negative Tests:**
    - Beantragte Urlaubstage überschreiten die verbleibenden Urlaubstage.
    - Fehlende oder ungültige Variablen.

Testbeispiel für überzogene Urlaubstage:
```java
@Test
void shouldThrowErrorForExceedingVacationDays() {
    when(execution.getVariable("requestedVacationDays")).thenReturn(15);
    when(execution.getVariable("remainingVacationDays")).thenReturn(10);

    Exception exception = assertThrows(BpmnError.class, () -> delegate.execute(execution));
    assertTrue(exception.getMessage().contains("Requested vacation days exceed remaining vacation days"));
}
```

---

## **Startparameter**

Die folgenden Parameter können beim Start des Prozesses gesetzt werden:
- `leaveType`: Art des Urlaubs (z. B. „Erholungsurlaub“).
- `requestedVacationDays`: Anzahl der beantragten Urlaubstage.
- `remainingVacationDays`: Verfügbare Urlaubstage.
- `leaveStatus`: Status des Urlaubs (z. B. „pending“).

Beispiel:
```json
{
  "leaveType": "Erholungsurlaub",
  "requestedVacationDays": 5,
  "remainingVacationDays": 20,
  "leaveStatus": "pending"
}
```

---

## **Protokollierung**

Die Protokollierung erfolgt über `Slf4j`. Hier ein Beispiel eines Log-Eintrags:
```plaintext
INFO  UpdateLeaveDelegate - Leave updated successfully. remainingVacationDays=15, leaveStatus=Updated
```

---

Dieses Dokument bietet eine Übersicht über die aktuelle Implementierung. Weitere Optimierungen können in zukünftigen Iterationen erfolgen.