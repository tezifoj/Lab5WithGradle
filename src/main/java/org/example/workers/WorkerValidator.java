package org.example.workers;

import org.example.models.Address;
import org.example.models.Organization;
import org.example.models.Worker;

import java.util.*;

public class WorkerValidator {

    public ValidationResult validateWorker(Worker worker, int position) {
        List<String> errors = new ArrayList<>();

        if (worker == null) {
            errors.add("  Работник null (позиция " + position + ")");
            return new ValidationResult(false, errors);
        }

        validateId(worker, position, errors);
        validateName(worker, position, errors);
        validateCoordinates(worker, position, errors);
        validateCreationDate(worker, position, errors);
        validateSalary(worker, position, errors);
        validateStartDate(worker, position, errors);
        validateStatus(worker, position, errors);
        validateOrganization(worker, position, errors);

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private void validateId(Worker worker, int position, List<String> errors) {
        if (worker.getId() == null) {
            errors.add("  ID не может быть null (позиция " + position + ")");
        } else if (worker.getId() <= 0) {
            errors.add("  ID должен быть положительным числом (текущий: " + worker.getId() +
                    ", позиция " + position + ")");
        }
    }

    private void validateName(Worker worker, int position, List<String> errors) {
        if (worker.getName() == null) {
            errors.add("  Имя не может быть null (позиция " + position + ")");
        } else if (worker.getName().trim().isEmpty()) {
            errors.add("  Имя не может быть пустым (позиция " + position + ")");
        } else if (worker.getName().length() > 256) {
            errors.add("  Имя слишком длинное (максимум 255, текущее: " +
                    worker.getName().length() + ", позиция " + position + ")");
        }
    }

    private void validateCoordinates(Worker worker, int position, List<String> errors) {
        if (worker.getCoordinates() == null) {
            errors.add("  Coordinates не могут быть null (позиция " + position + ")");
        } else {
            if (worker.getCoordinates().getX() <= -912) {
                errors.add("  x должен быть > -912 (текущий: " + worker.getCoordinates().getX() +
                        ", позиция " + position + ")");
            }
            if (worker.getCoordinates().getY() > 139) {
                errors.add("  y должен быть ≤ 139 (текущий: " + worker.getCoordinates().getY() +
                        ", позиция " + position + ")");
            }
        }
    }

    private void validateCreationDate(Worker worker, int position, List<String> errors) {
        if (worker.getCreationDate() == null) {
            errors.add("  creationDate не может быть null (позиция " + position + ")");
        }
    }

    private void validateSalary(Worker worker, int position, List<String> errors) {
        if (worker.getSalary() != null) {
            if (worker.getSalary() <= 0) {
                errors.add("  salary должен быть > 0 (текущий: " + worker.getSalary() +
                        ", позиция " + position + ")");
            }
            if (worker.getSalary() > 1_000_000_000) {
                errors.add("  salary слишком большой (максимум 1e9, текущий: " +
                        worker.getSalary() + ", позиция " + position + ")");
            }
        }
    }

    private void validateStartDate(Worker worker, int position, List<String> errors) {
        if (worker.getStartDate() == null) {
            errors.add("  startDate не может быть null (позиция " + position + ")");
        } else if (worker.getStartDate().after(new Date())) {
            errors.add("  startDate не может быть в будущем (текущий: " +
                    worker.getStartDate() + ", позиция " + position + ")");
        }
    }

    private void validateStatus(Worker worker, int position, List<String> errors) {
        if (worker.getStatus() == null) {
            errors.add("  status не может быть null (позиция " + position + ")");
        }
    }

    private void validateOrganization(Worker worker, int position, List<String> errors) {
        if (worker.getOrganization() != null) {
            Organization org = worker.getOrganization();
            validateOrganizationEmployeesCount(org, position, errors);
            validateOrganizationType(org, position, errors);
            validateOrganizationAddress(org, position, errors);
        }
    }

    private void validateOrganizationEmployeesCount(Organization org, int position, List<String> errors) {
        if (org.getEmployeesCount() == null) {
            errors.add("  organization.employeesCount не может быть null (позиция " + position + ")");
        } else if (org.getEmployeesCount() <= 0) {
            errors.add("  organization.employeesCount должен быть > 0 (текущий: " +
                    org.getEmployeesCount() + ", позиция " + position + ")");
        }
    }

    private void validateOrganizationType(Organization org, int position, List<String> errors) {
        if (org.getType() == null) {
            errors.add("  organization.type не может быть null (позиция " + position + ")");
        }
    }

    private void validateOrganizationAddress(Organization org, int position, List<String> errors) {
        if (org.getOfficialAddress() == null) {
            errors.add("  organization.officialAddress не может быть null (позиция " + position + ")");
        } else {
            Address addr = org.getOfficialAddress();
            validateAddressStreet(addr, position, errors);
            validateAddressZipCode(addr, position, errors);
        }
    }

    private void validateAddressStreet(Address addr, int position, List<String> errors) {
        if (addr.getStreet() == null) {
            errors.add("  address.street не может быть null (позиция " + position + ")");
        } else if (addr.getStreet().trim().isEmpty()) {
            errors.add("  address.street не может быть пустым (позиция " + position + ")");
        } else if (addr.getStreet().length() > 193) {
            errors.add("  address.street слишком длинная (максимум 193, текущая: " +
                    addr.getStreet().length() + ", позиция " + position + ")");
        }
    }

    private void validateAddressZipCode(Address addr, int position, List<String> errors) {
        if (addr.getZipCode() != null && addr.getZipCode().length() > 18) {
            errors.add("  address.zipCode слишком длинный (максимум 18, текущий: " +
                    addr.getZipCode().length() + ", позиция " + position + ")");
        }
    }

    public CollectionValidationResult validateCollection(Vector<Worker> workers) {
        List<String> allErrors = new ArrayList<>();
        List<Worker> validWorkers = new ArrayList<>();
        Set<Long> ids = new HashSet<>();

        for (int i = 0; i < workers.size(); i++) {
            Worker worker = workers.get(i);
            ValidationResult result = validateWorker(worker, i + 1);

            if (result.isValid()) {
                if (ids.add(worker.getId())) {
                    validWorkers.add(worker);
                } else {
                    allErrors.add("Обнаружен дубликат ID " + worker.getId() +
                            " (позиция " + (i + 1) + ")");
                }
            } else {
                allErrors.addAll(result.getErrors());
            }
        }

        return new CollectionValidationResult(validWorkers, allErrors);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static class CollectionValidationResult {
        private final List<Worker> validWorkers;
        private final List<String> errors;

        public CollectionValidationResult(List<Worker> validWorkers, List<String> errors) {
            this.validWorkers = validWorkers;
            this.errors = errors;
        }

        public List<Worker> getValidWorkers() {
            return validWorkers;
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public int getValidCount() {
            return validWorkers.size();
        }
    }
}