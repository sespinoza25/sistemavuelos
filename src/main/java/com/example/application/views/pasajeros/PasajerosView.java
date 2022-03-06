package com.example.application.views.pasajeros;

import com.example.application.data.entity.Pasajeros;
import com.example.application.data.service.PasajerosService;
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
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Pasajeros")
@Route(value = "Pasajero/:pasajerosID?/:action?(edit)", layout = MainLayout.class)
public class PasajerosView extends Div implements BeforeEnterObserver {

    private final String PASAJEROS_ID = "pasajerosID";
    private final String PASAJEROS_EDIT_ROUTE_TEMPLATE = "Pasajero/%s/edit";

    private Grid<Pasajeros> grid = new Grid<>(Pasajeros.class, false);

    private TextField id_Pasajero;
    private TextField nombre;
    private TextField apellido;
    private TextField direccion;
    private TextField telefono;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Pasajeros> binder;

    private Pasajeros pasajeros;

    private PasajerosService pasajerosService;

    public PasajerosView(@Autowired PasajerosService pasajerosService) {
        this.pasajerosService = pasajerosService;
        addClassNames("pasajeros-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id_Pasajero").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("apellido").setAutoWidth(true);
        grid.addColumn("direccion").setAutoWidth(true);
        grid.addColumn("telefono").setAutoWidth(true);
        grid.setItems(query -> pasajerosService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PASAJEROS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PasajerosView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Pasajeros.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(id_Pasajero).withConverter(new StringToUuidConverter("Invalid UUID")).bind("id_Pasajero");
        binder.forField(telefono).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("telefono");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.pasajeros == null) {
                    this.pasajeros = new Pasajeros();
                }
                binder.writeBean(this.pasajeros);

                pasajerosService.update(this.pasajeros);
                clearForm();
                refreshGrid();
                Notification.show("Pasajeros details stored.");
                UI.getCurrent().navigate(PasajerosView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the pasajeros details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> pasajerosId = event.getRouteParameters().get(PASAJEROS_ID).map(UUID::fromString);
        if (pasajerosId.isPresent()) {
            Optional<Pasajeros> pasajerosFromBackend = pasajerosService.get(pasajerosId.get());
            if (pasajerosFromBackend.isPresent()) {
                populateForm(pasajerosFromBackend.get());
            } else {
                Notification.show(String.format("The requested pasajeros was not found, ID = %s", pasajerosId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PasajerosView.class);
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
        id_Pasajero = new TextField("Id_ Pasajero");
        nombre = new TextField("Nombre");
        apellido = new TextField("Apellido");
        direccion = new TextField("Direccion");
        telefono = new TextField("Telefono");
        Component[] fields = new Component[]{id_Pasajero, nombre, apellido, direccion, telefono};

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

    private void populateForm(Pasajeros value) {
        this.pasajeros = value;
        binder.readBean(this.pasajeros);

    }
}
