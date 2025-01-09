package org.vaadin.example;

import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

import static com.vaadin.flow.component.notification.Notification.Position.BOTTOM_STRETCH;

@Route("")
public class PinCodeView extends VerticalLayout {

    private final PasswordField pinField;
    private final StringBuilder pinBuilder;

    public PinCodeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        pinBuilder = new StringBuilder();

        add(new H1("Введите PIN-код"));

        pinField = new PasswordField();
        pinField.setLabel("PIN-код");
        pinField.setPlaceholder("****");
        pinField.setWidth("320px");
        add(pinField);

        VerticalLayout keypadLayout = new VerticalLayout();
        for (int row = 0; row < 4; row++) {
            HorizontalLayout rowLayout = new HorizontalLayout();
            for (int col = 0; col < 3; col++) {
                int digit = row * 3 + col + 1;

                if (digit > 9) {
                    if (row == 3 && col == 1) digit = 0;
                    else {
                        Button digit0 = new Button("0");
                        digit0.addClickListener(e -> appendDigit(0));
                        digit0.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                        digit0.setHeight("100px");
                        digit0.setWidth("100px");
                        rowLayout.add(digit0);
                    }break;
                }
                final int buttonDigit = digit;
                Button digitButton = new Button(String.valueOf(digit));
                digitButton.addClickListener(e -> appendDigit(buttonDigit));
                digitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                digitButton.setWidth("100px");
                digitButton.setHeight("100px");
                rowLayout.add(digitButton);
            }
            keypadLayout.add(rowLayout);
            keypadLayout.setAlignItems(Alignment.CENTER);
        }
        HorizontalLayout rowLayout = new HorizontalLayout();

        add(keypadLayout);

        Button clearButton = new Button("Очистить");
        clearButton.addClickListener(e -> clearPin());
        clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.setWidth("150px");
        clearButton.setHeight("50px");

        Button submitButton = new Button("Подтвердить");
        submitButton.addClickListener(e -> validatePin());
        submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        submitButton.setWidth("150px");
        submitButton.setHeight("50px");

        add(new HorizontalLayout(clearButton, submitButton));
    }

    private void appendDigit(int digit) {
        if (pinBuilder.length() < 4) {
            pinBuilder.append(digit);
            updatePinField();
        }else if (pinBuilder.length() == 4) {
            validatePin();
            clearPin();
        }
    }

    private void clearPin() {
        pinBuilder.setLength(0);
        updatePinField();
    }

    private void validatePin() {
        String pin = pinBuilder.toString();
        if ("1234".equals(pin)) {
            Notification notification = new Notification();
            notification.setPosition(BOTTOM_STRETCH);
            Text text = new Text("Доступ разрешён");
            VerticalLayout layout = new VerticalLayout();
            layout.add(text);
            layout.setAlignItems(Alignment.CENTER);
            notification.add(layout);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.open();
            notification.setDuration(1000);

        } else {
            Notification notification = new Notification();
            notification.setPosition(BOTTOM_STRETCH);
            Text text = new Text("Неверный ПИН-КОД");
            VerticalLayout layout = new VerticalLayout();
            layout.add(text);
            layout.setAlignItems(Alignment.CENTER);
            notification.add(layout);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            notification.setDuration(1000);
            clearPin();
        }
    }

    private void updatePinField() {
        pinField.setValue("*".repeat(pinBuilder.length()));
    }
}

