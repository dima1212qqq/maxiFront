package ru.dovakun.dovapay.views;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

@Route("settings")
public class Settings extends AppDrawer{
    public Settings() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("Настройки");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        setDrawerOpened(false);
        addToDrawer(AppDrawer.createDrawer());
        addToNavbar(toggle, title);
    }
}
