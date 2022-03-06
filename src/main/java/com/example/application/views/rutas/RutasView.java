package com.example.application.views.rutas;

import com.example.application.data.entity.Ruta;
import com.example.application.data.service.RutaService;
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

@PageTitle("Rutas")
@Route(value = "ruta/:rutaID?/:action?(edit)", layout = MainLayout.class)
public class RutasView extends Div implements BeforeEnterObserver {

    private final String RUTA_ID = "rutaID";
    private final String RUTA_EDIT_ROUTE_TEMPLATE = "ruta/%s/edit";

    private Grid<Ruta> grid = new Grid<>(Ruta.class, false);

    private TextField id_ruta;
    private TextField destino;
    private TextField valor_pasaje;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Ruta> binder;

    private Ruta ruta;

    private RutaService rutaService;

    public RutasView(@Autowired RutaService rutaService) {
        this.rutaService = rutaService;
        addClassNames("rutas-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id_ruta").setAutoWidth(true);
        grid.addColumn("destino").setAutoWidth(true);
        grid.addColumn("valor_pasaje").setAutoWidth(true);
        grid.setItems(query -> rutaService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(RUTA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(RutasView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Ruta.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(id_ruta).withConverter(new StringToUuidConverter("Invalid UUID")).bind("id_ruta");
        binder.forField(valor_pasaje).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("valor_pasaje");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.ruta == null) {
                    this.ruta = new Ruta();
                }
                binder.writeBean(this.ruta);

                rutaService.update(this.ruta);
                clearForm();
                refreshGrid();
                Notification.show("Ruta details stored.");
                UI.getCurrent().navigate(RutasView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the ruta details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> rutaId = event.getRouteParameters().get(RUTA_ID).map(UUID::fromString);
        if (rutaId.isPresent()) {
            Optional<Ruta> rutaFromBackend = rutaService.get(rutaId.get());
            if (rutaFromBackend.isPresent()) {
                populateForm(rutaFromBackend.get());
            } else {
                Notification.show(String.format("The requested ruta was not found, ID = %s", rutaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(RutasView.class);
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
        id_ruta = new TextField("Id_ruta");
        destino = new TextField("Destino");
        valor_pasaje = new TextField("Valor_pasaje");
        Component[] fields = new Component[]{id_ruta, destino, valor_pasaje};

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

    private void populateForm(Ruta value) {
        this.ruta = value;
        binder.readBean(this.ruta);

    }
}
