package ru.dovakun.dovapay.views;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.Route;
import ru.dovakun.dovapay.model.Category;
import ru.dovakun.dovapay.model.Feature;
import ru.dovakun.dovapay.model.Product;
import ru.dovakun.dovapay.model.ProductCartDto;
import ru.dovakun.dovapay.service.CategoryService;
import ru.dovakun.dovapay.service.FeatureService;
import ru.dovakun.dovapay.service.ProductService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Route("main")
public class MainView extends AppDrawer {
    private final HorizontalLayout breadcrumbLayout = new HorizontalLayout();
    private final CategoryService categoryService;
    private final ProductService productService;
    private final FeatureService featureService;
    private final FlexLayout categoryLayout = new FlexLayout();
    private Category currentCategory = null;
    private final List<ProductCartDto> cart = new ArrayList<>();
    private final Footer footer = new Footer();

    public MainView(CategoryService categoryService, ProductService productService, FeatureService featureService) {
        this.categoryService = categoryService;
        this.productService = productService;
        createFooter();

        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("DovaPay");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        setDrawerOpened(false);
        addToDrawer(AppDrawer.createDrawer());
        addToNavbar(toggle, title);
        setContent(createMainScreen());
        this.featureService = featureService;
    }

    private VerticalLayout createMainScreen() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(createBreadcrumbView());
        layout.add(createContentView());
        layout.add(footer);
        layout.getStyle().set("overflow", "hidden");
        return layout;
    }

    public FlexLayout createContentView() {
        categoryLayout.removeAll();
        List<Category> categories = categoryService.findCategoryWhereParentIsNull();
        for (Category category : categories) {
            categoryLayout.add(createCategoryCard(category));
        }
        List<Product> products = productService.findByCategoryIsNull();
        for (Product product : products) {
            categoryLayout.add(createCategoryCard(product));
        }
        categoryLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        categoryLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
        categoryLayout.setSizeFull();

        return categoryLayout;
    }

    private VerticalLayout createCategoryCard(Category category) {
        VerticalLayout card = new VerticalLayout();
        card.setId("category-card");
        card.setWidth("150px");
        card.setHeight("175px");
        card.getStyle().set("border-radius", "30px");
        card.getStyle().set("margin", "10px");
        card.getStyle().set("position", "relative");
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.addClickListener(event -> {
            currentCategory = category;
            refreshCategoryView(category);
        });
        Avatar avatar = new Avatar();
        avatar.setImage(category.getImageUrl());
        avatar.setWidth("95px");
        avatar.setHeight("95px");
        Span name = new Span(category.getName());
        name.getStyle().set("font-weight", "bold");
        name.getStyle().set("text-align", "center");
        name.getStyle().set("overflow", "hidden");
        name.getStyle().set("text-overflow", "ellipsis");
        name.getStyle().set("white-space", "nowrap");
        name.getStyle().set("width", "100%");
        Tooltip tooltip = Tooltip.forComponent(name).
                withText(category.getName());
        card.add(avatar, name);

        return card;
    }

    public void refreshCategoryView(Category category) {
        if (category == null) {
            categoryLayout.removeAll();
            createContentView();
        } else {
            List<Category> categories = categoryService.findByParentId(category.getId());
            List<Product> products = productService.findByCategory(category);
            categoryLayout.removeAll();
            refreshBreadcrumbs();
            for (Category subcategory : categories) {
                categoryLayout.add(createCategoryCard(subcategory));
            }
            for (Product product : products) {
                categoryLayout.add(createCategoryCard(product));
            }
        }

    }

    private VerticalLayout createCategoryCard(Product product) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("150px");
        card.setHeight("175px");
        card.setId("category-card");
        card.getStyle().set("border-radius", "30px");
        card.getStyle().set("margin", "10px");
        card.getStyle().set("position", "relative");

        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.addClickListener(event -> {
            createDialogChoiceFeature(product);
        });
        Avatar avatar = new Avatar();
        avatar.setImage(product.getImageUrl());
        avatar.setWidth("95px");
        avatar.setHeight("95px");
        Span name = new Span(product.getName());
        name.getStyle().set("font-weight", "bold");
        name.getStyle().set("text-align", "center");
        name.getStyle().set("overflow", "hidden");
        name.getStyle().set("text-overflow", "ellipsis");
        name.getStyle().set("white-space", "nowrap");
        name.getStyle().set("width", "100%");
        Tooltip tooltip = Tooltip.forComponent(name).
                withText(product.getName());
//        Span price = new Span(String.valueOf(product.getPrice()));
//        price.getStyle().set("font-weight", "bold");
        card.add(avatar, name);

        return card;
    }

    private void createDialogChoiceFeature(Product product) {
        Dialog dialog = new Dialog();
        ComboBox<Feature> feature = new ComboBox<>();
        List<Feature> featureList = featureService.getFeaturesByProduct(product);
        feature.setItems(featureList);
        feature.setItemLabelGenerator(Feature::getName);
        ProductCartDto cartDto = new ProductCartDto();
        if (featureService.getFeaturesByProduct(product).size() == 1) {
            cartDto.setProduct(product);
            cartDto.setFeature(featureList.get(0));
            addToCart(cartDto);
            return;
        } else if (featureService.getFeaturesByProduct(product).isEmpty()) {
            Notification notification = new Notification();
            notification.setText("У вас не задана характеристика для товара");
            notification.open();
            notification.setDuration(1000);
            return;
        }
        dialog.open();
        Button ok = new Button("Добавить", event -> {
            if (feature.getValue() == null) {
                Notification.show("Пожалуйста, выберите характеристику перед добавлением в корзину.");
                return;
            }
            cartDto.setProduct(product);
            cartDto.setFeature(feature.getValue());
            addToCart(cartDto);
            dialog.close();
        });
        dialog.add(feature, ok);
    }

    private HorizontalLayout createBreadcrumbView() {
        breadcrumbLayout.setSpacing(true);
        breadcrumbLayout.setPadding(true);
        breadcrumbLayout.getStyle().set("background-color", "#f5f5f5");
        breadcrumbLayout.getStyle().set("border-radius", "10px");
        breadcrumbLayout.getStyle().set("padding", "10px");
        breadcrumbLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        refreshBreadcrumbs();
        return breadcrumbLayout;
    }

    private void refreshBreadcrumbs() {
        breadcrumbLayout.removeAll();
        Button rootButton = new Button("Главная", event -> {
            currentCategory = null;
            refreshCategoryView(null);
            refreshBreadcrumbs();
        });
        rootButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        rootButton.getStyle().set("background", "none");
        rootButton.getStyle().set("font-weight", "bold");
        breadcrumbLayout.add(rootButton);
        breadcrumbLayout.add(new Span(" / "));
        if (currentCategory != null) {
            List<Category> breadcrumbPath = buildBreadcrumbPath(currentCategory);
            for (int i = 0; i < breadcrumbPath.size(); i++) {
                Category category = breadcrumbPath.get(i);
                Button breadcrumbButton = new Button(category.getName(), event -> {
                    currentCategory = category;
                    refreshCategoryView(category);
                    refreshBreadcrumbs();
                });

                breadcrumbButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
                breadcrumbButton.getStyle().set("background", "none");
                breadcrumbButton.getStyle().set("font-weight", "bold");

                breadcrumbLayout.add(breadcrumbButton);
                if (i < breadcrumbPath.size() - 1) {
                    breadcrumbLayout.add(new Span(" / "));
                }
            }
        }
    }

    private List<Category> buildBreadcrumbPath(Category category) {
        List<Category> path = new ArrayList<>();
        Category current = category;
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        return path;
    }

    private void createFooter() {
        footer.getStyle()
                .set("position", "fixed")
                .set("bottom", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("background-color", "#f5f5f5")
                .set("padding", "10px")
                .set("text-align", "center")
                .set("box-shadow", "0 -2px 5px rgba(0,0,0,0.1)");
        updateFooter();
    }

    private void addToCart(ProductCartDto product) {
        cart.add(product);
        updateFooter();
    }

    private void updateFooter() {
        footer.removeAll();
        if (cart.isEmpty()) {
            footer.add(new Span("Корзина пуста"));
        } else {
            Span span = new Span("Корзина: " + cart.size());
            span.addClickListener(event -> {
                createDialogCart();
            });
            footer.add(span);
        }
    }

    private void createDialogCart() {
        Dialog dialog = new Dialog();
        dialog.setWidth("100%");
        dialog.setHeight("70%");
        dialog.getElement().getStyle().set("max-width", "800px");

        Span totalPriceLabel = new Span("Итого: " + calculateTotalPrice());

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        Grid<ProductCartDto> grid = new Grid<>();
        grid.addItemClickListener(event -> {
            Notification.show(event.getItem().getProduct().toString());
        });
        grid.setItems(cart);
        grid.addColumn(productCartDto -> productCartDto.getProduct().getName())
                .setHeader("Название");

        grid.addColumn(productCartDto -> productCartDto.getFeature().getName())
                .setHeader("Характеристика")
                .setAutoWidth(true);

        grid.addColumn(productCartDto -> productCartDto.getFeature().getPrice())
                .setHeader("Цена");

        grid.addComponentColumn(productCartDto -> {
                    Icon icon = new Icon(VaadinIcon.TRASH);
                    icon.setSize("20px");
                    Button delete = new Button(icon);
                    delete.addClickListener(event -> {
                        cart.remove(productCartDto);
                        grid.setItems(cart);
                        totalPriceLabel.setText("Итого: " + calculateTotalPrice());
                        updateFooter();
                    });
                    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    return delete;
                })
                .setHeader("Действие")
                .setAutoWidth(true);
        layout.add(grid);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        Button save = new Button("К оплате", event -> {
            dialog.close();
        });

        Button cancel = new Button("Очистить корзину", event -> {
            cart.clear();
            updateFooter();
            dialog.close();
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, cancel);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.add(totalPriceLabel);
        layout.add(buttonLayout);
        dialog.add(layout);
        dialog.open();
    }

    private double calculateTotalPrice() {
        return cart.stream()
                .mapToDouble(productCartDto -> productCartDto.getFeature().getPrice())
                .sum();
    }
}
