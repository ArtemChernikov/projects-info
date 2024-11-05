package ru.projects.view.employees;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.projects.model.Employee;
import ru.projects.model.Specialization;
import ru.projects.service.SpecializationService;

import java.util.ArrayList;
import java.util.List;

public class EmployeeFilter extends Div implements Specification<Employee> {

    private final TextField name = new TextField("Name");
    private final TextField phone = new TextField("Phone");
    private final DatePicker startDate = new DatePicker("Date of Birth");
    private final DatePicker endDate = new DatePicker();
    private final MultiSelectComboBox<String> specializations = new MultiSelectComboBox<>("Specialization");

    public EmployeeFilter(SpecializationService specializationService, Runnable onSearch) {
        setWidthFull();
        addClassName("filter-layout");
        addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BoxSizing.BORDER);

        configureFilters(specializationService);
        addComponents(onSearch);
    }

    private void configureFilters(SpecializationService specializationService) {
        name.setPlaceholder("First or last name");
        List<String> allSpecializationsNames = specializationService.getAllSpecializationsNames();
        this.specializations.setItems(allSpecializationsNames);
    }

    private void addComponents(Runnable onSearch) {
        Button resetBtn = new Button("Reset", e -> resetFilters(onSearch));
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button searchBtn = new Button("Search", e -> onSearch.run());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Div actions = new Div(resetBtn, searchBtn);
        actions.addClassName(LumoUtility.Gap.SMALL);
        actions.addClassName("actions");

        add(name, phone, createDateRangeFilter(), specializations, actions);
    }

    private Component createDateRangeFilter() {
        startDate.setPlaceholder("From");
        endDate.setPlaceholder("To");
        startDate.setAriaLabel("From date");
        endDate.setAriaLabel("To date");
        FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
        dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);
        return dateRangeComponent;
    }

    private void resetFilters(Runnable onSearch) {
        name.clear();
        phone.clear();
        startDate.clear();
        endDate.clear();
        specializations.clear();
        onSearch.run();
    }

    @Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!name.isEmpty()) {
            String lowerCaseFilter = name.getValue().toLowerCase();
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), lowerCaseFilter + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), lowerCaseFilter + "%")
            ));
        }
        if (!phone.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")),
                    "%" + phone.getValue().toLowerCase() + "%"));
        }
        if (startDate.getValue() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfBirth"), startDate.getValue()));
        }
        if (endDate.getValue() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), endDate.getValue()));
        }
        if (!specializations.isEmpty()) {
            Join<Employee, Specialization> specializationJoin = root.join("specialization");
            List<Predicate> specializationPredicates = new ArrayList<>();
            for (String specializationName : specializations.getValue()) {
                specializationPredicates.add(
                        criteriaBuilder.equal(specializationJoin.get("specializationName"),
                                specializationName)
                );
            }
            predicates.add(criteriaBuilder.or(specializationPredicates.toArray(Predicate[]::new)));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}