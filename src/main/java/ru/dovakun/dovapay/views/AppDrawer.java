package ru.dovakun.dovapay.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class AppDrawer extends AppLayout {

    public static Scroller createDrawer() {
        Div customMenu = new Div();
        customMenu.setClassName(LumoUtility.Padding.SMALL);

        Button openMain = new Button("На главную", event -> UI.getCurrent().navigate("main"));
        openMain.setWidthFull();
        openMain.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button openShiftButton = new Button("Открыть смену", event -> openShiftDialog());
        openShiftButton.setWidthFull();
        openShiftButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button openAssortment = new Button("Ассортимент", event -> UI.getCurrent().navigate("assortment"));
        openAssortment.setWidthFull();
        openAssortment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button openSettings = new Button("Настройки", event -> UI.getCurrent().navigate("settings"));
        openSettings.setWidthFull();
        openSettings.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        customMenu.add(openMain,openShiftButton,openAssortment,openSettings);

        Div container = new Div(customMenu);
        Scroller scroller = new Scroller(container);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        return scroller;
    }

    private static void openShiftDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Открытие смены");

        dialog.add("Тут будет форма для открытия смены.");

        Button closeButton = new Button("Закрыть", event -> dialog.close());
        dialog.getFooter().add(closeButton);

        dialog.open();
    }
}
