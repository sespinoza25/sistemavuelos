package com.example.application.views.aviones;

import com.example.application.data.entity.Avion;
import com.example.application.data.service.AvionService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Aviones")
@Route(value = "avion/:avionID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class AvionesView extends Div implements BeforeEnterObserver {

    private final String AVION_ID = "avionID";
    private final String AVION_EDIT_ROUTE_TEMPLATE = "avion/%s/edit";

    private Grid<Avion> grid = new Grid<>(Avion.class, false);

    private TextField id_avion;
    private TextField descripcion;
    private TextField capacidad;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Avion> binder;

    private Avion avion;

    private AvionService avionService;

    public AvionesView(@Autowired AvionService avionService) {
        this.avionService = avionService;
        addClassNames("aviones-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id_avion").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("capacidad").setAutoWidth(true);
        grid.setItems(query -> avionService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(AVION_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AvionesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Avion.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(id_avion).withConverter(new StringToUuidConverter("Invalid UUID")).bind("id_avion");
        binder.forField(capacidad).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("capacidad");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.avion == null) {
                    this.avion = new Avion();
                }
                binder.writeBean(this.avion);

                avionService.update(this.avion);
                clearForm();
                refreshGrid();
                Notification.show("Avion details stored.");
                UI.getCurrent().navigate(AvionesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the avion details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> avionId = event.getRouteParameters().get(AVION_ID).map(UUID::fromString);
        if (avionId.isPresent()) {
            Optional<Avion> avionFromBackend = avionService.get(avionId.get());
            if (avionFromBackend.isPresent()) {
                populateForm(avionFromBackend.get());
            } else {
                Notification.show(String.format("The requested avion was not found, ID = %s", avionId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AvionesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        id_avion = new TextField("Id_avion");
        descripcion = new TextField("Descripcion");
        capacidad = new TextField("Capacidad");
        Component[] fields = new Component[]{id_avion, descripcion, capacidad};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Avion value) {
        this.avion = value;
        binder.readBean(this.avion);

    }
}
