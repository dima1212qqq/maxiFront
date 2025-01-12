package ru.dovakun.dovapay.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dovakun.dovapay.model.Category;
import ru.dovakun.dovapay.model.Feature;
import ru.dovakun.dovapay.model.Product;
import ru.dovakun.dovapay.service.CategoryService;
import ru.dovakun.dovapay.service.FeatureService;
import ru.dovakun.dovapay.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route("assortment")
public class Assortment extends AppDrawer {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final FeatureService featureService;
    private List<Product> products;
    private final FlexLayout categoryLayout = new FlexLayout();
    private Category currentCategory = null;

    @Autowired()
    public Assortment(CategoryService categoryService, ProductService productService, FeatureService featureService) {
        this.categoryService = categoryService;
        this.productService = productService;
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("Ассортимент");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
//        Button button = new Button();
//        button.setIcon(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
//        button.addClickListener(event -> {
//            categoryLayout.removeAll();
//            Optional<Category> categoryOptional = Optional.ofNullable(currentCategory);
//            if (categoryOptional.isEmpty()) {
//                categoryLayout.add(createContentView());
//            } else {
//                Optional<Category> categoryOptional1 = Optional.ofNullable(currentCategory.getParent());
//                if (categoryOptional1.isPresent()) {
//                    refreshCategoryView(categoryOptional1.get().getParent());
//                }else {
//                    categoryLayout.add(createContentView());
//                }
//            }
//        });
        setDrawerOpened(false);
        addToDrawer(AppDrawer.createDrawer());
        addToNavbar(toggle, title);
        setContent(createMainView());
        this.featureService = featureService;
    }

    public VerticalLayout createMainView() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(createBreadcrumbView());
        layout.add(createButtonView());
        layout.add(createContentView());
        layout.getStyle().set("overflow", "hidden");
        return layout;
    }

    public FlexLayout createButtonView() {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.ROW);
        layout.setAlignContent(FlexLayout.ContentAlignment.CENTER);
        layout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
        layout.getStyle().set("gap", "15px");
        layout.getStyle().set("padding", "15px");
        layout.setWidthFull();
        Button createCategory = new Button("Создать категорию", event -> createCategoryDialog());
        Button createGood = new Button("Создать товар", event -> createProductDialog());
        layout.add(createCategory, createGood);
        return layout;
    }

    private void createProductDialog() {
        Dialog dialog = new Dialog();
        dialog.open();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout layout = new VerticalLayout();
        TextField nameField = new TextField("Имя категории");
        TextField imageUrlField = new TextField("URL изображения");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Product product = new Product();
        Button save = new Button("Сохранить", event -> {
            product.setName(nameField.getValue());
            product.setImageUrl(imageUrlField.getValue());
            if (currentCategory != null) {
                product.setCategory(currentCategory);
            }
            productService.save(product);
            refreshView();
            dialog.close();
        });
        Button close = new Button("Отменить", event -> dialog.close());
        buttonLayout.add(save, close);
        layout.add(nameField, imageUrlField, buttonLayout);
        dialog.add(layout);
    }

    private void createCategoryDialog() {
        Dialog dialog = new Dialog();
        dialog.open();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout layout = new VerticalLayout();
        TextField nameField = new TextField("Имя категории");
        TextField imageUrlField = new TextField("URL изображения");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Category category = new Category();
        Button save = new Button("Сохранить", event -> {
            category.setName(nameField.getValue());
            category.setImageUrl(imageUrlField.getValue());
            if (currentCategory != null) {
                category.setParent(currentCategory);
            }
            categoryService.save(category);
            refreshView();
            dialog.close();
        });
        Button close = new Button("Отменить", event -> dialog.close());
        buttonLayout.add(save, close);
        layout.add(nameField, imageUrlField, buttonLayout);
        dialog.add(layout);
    }

    public FlexLayout createContentView() {
        categoryLayout.removeAll();
        List<Category> categories = categoryService.findCategoryWhereParentIsNull();
        for (Category category : categories) {
            categoryLayout.add(createCategoryCard(category));
        }
        products = productService.findByCategoryIsNull();
        for (Product product : products) {
            categoryLayout.add(createCategoryCard(product));
        }
        categoryLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        categoryLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
        categoryLayout.setSizeFull();

        return categoryLayout;
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
            openProductDialog(product);
        });
        HorizontalLayout leftButtons = new HorizontalLayout();
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addClickListener(event -> {
            // Логика изменения
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        editButton.getStyle().set("border-radius", "100%");
        editButton.getStyle().set("background", "none");
        leftButtons.add(editButton);
        leftButtons.getStyle().set("position", "absolute");
        leftButtons.getStyle().set("top", "5px");
        leftButtons.getStyle().set("left", "5px");

        HorizontalLayout rightButtons = new HorizontalLayout();
        Icon delete = new Icon(VaadinIcon.TRASH);
        delete.setColor("red");
        Button deleteButton = new Button(delete);
        deleteButton.addClickListener(event -> {
            productService.delete(product);
            refreshView();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        deleteButton.getStyle().set("border-radius", "100%");
        deleteButton.getStyle().set("background", "none");
        rightButtons.add(deleteButton);
        rightButtons.getStyle().set("position", "absolute");
        rightButtons.getStyle().set("top", "5px");
        rightButtons.getStyle().set("right", "5px");

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
        card.add(leftButtons, rightButtons, avatar, name);

        return card;
    }

    private void openProductDialog(Product product) {
        Dialog dialog = new Dialog();
        dialog.open();
        dialog.setCloseOnEsc(true);
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout nameLayout = new HorizontalLayout();
        ComboBox<Feature> featureComboBox = new ComboBox<>();
        featureComboBox.setLabel("Характеристика");
        featureComboBox.setItems(featureService.getFeaturesByProduct(product));
        featureComboBox.setItemLabelGenerator(Feature::getName);
        NumberField priceField = new NumberField();
        featureComboBox.addValueChangeListener(event -> {
            priceField.setValue(featureComboBox.getValue().getPrice());
        });
        Button addFeature = new Button(new Icon(VaadinIcon.PLUS));
        addFeature.addClickListener(event -> {
            openDialogFeatureCreate(product,featureComboBox);
        });
        nameLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        nameLayout.add(featureComboBox, addFeature);
        layout.add(nameLayout);
        Optional<Feature> feature = Optional.ofNullable(featureComboBox.getValue());
        Double price = feature.isPresent() ? feature.get().getPrice() : 0;
        priceField.setValue(price);
        priceField.setLabel("Стоимость");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = new Button("Сохранить", event -> {
            Feature feature1 = featureComboBox.getValue();
            feature1.setPrice(priceField.getValue());
            featureService.save(feature1);
            dialog.close();
        });
        Button cancel = new Button("Отменить", event -> dialog.close());
        buttonLayout.add(save, cancel);
        layout.add(priceField,buttonLayout);
        dialog.add(layout);

    }

    private void openDialogFeatureCreate(Product product, ComboBox comboBox) {
        Dialog dialog = new Dialog();
        dialog.open();
        VerticalLayout layout = new VerticalLayout();
        TextField nameField = new TextField("Название характеристики");
        NumberField priceField = new NumberField("Стоимость");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = new Button("Сохранить",buttonClickEvent ->{
            Feature feature = new Feature();
            feature.setName(nameField.getValue());
            feature.setPrice(priceField.getValue());
            feature.setProduct(product);
            featureService.save(feature);
            comboBox.setItems(featureService.getFeaturesByProduct(product));
            dialog.close();
        } );
        Button cansel = new Button("Отменить",event -> dialog.close());
        buttonLayout.add(save,cansel);
        layout.add(nameField,priceField,buttonLayout);
        dialog.add(layout);

    }

    private VerticalLayout createCategoryCard(Category category) {
        VerticalLayout card = new VerticalLayout();
        card.setId("category-card");
        card.setWidth("150px");
        card.setHeight("175px");
        card.getStyle().set("border-radius", "30px");
        card.getStyle().set("margin", "10px");
        card.getStyle().set("position", "relative");
        HorizontalLayout leftButtons = new HorizontalLayout();
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addClickListener(event -> {
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        editButton.getStyle().set("border-radius", "100%");
        editButton.getStyle().set("background", "none");
        leftButtons.add(editButton);
        leftButtons.getStyle().set("position", "absolute");
        leftButtons.getStyle().set("top", "5px");
        leftButtons.getStyle().set("left", "5px");

        HorizontalLayout rightButtons = new HorizontalLayout();
        Icon delete = new Icon(VaadinIcon.TRASH);
        delete.setColor("red");
        Button deleteButton = new Button(delete);
        deleteButton.addClickListener(event -> {
            categoryService.delete(category);
            refreshView();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        deleteButton.getStyle().set("border-radius", "100%");
        deleteButton.getStyle().set("background", "none");
        rightButtons.add(deleteButton);
        rightButtons.getStyle().set("position", "absolute");
        rightButtons.getStyle().set("top", "5px");
        rightButtons.getStyle().set("right", "5px");
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
        card.add(leftButtons,rightButtons,avatar, name);

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

    private void refreshView() {
        categoryLayout.removeAll();
        if (currentCategory != null) {
            List<Category> categories = categoryService.findByParentId(currentCategory.getId());
            List<Product> products = productService.findByCategory(currentCategory);
            refreshBreadcrumbs();
            for (Category subcategory : categories) {
                categoryLayout.add(createCategoryCard(subcategory));
            }
            for (Product product : products) {
                categoryLayout.add(createCategoryCard(product));
            }
        } else {
            List<Category> categories = categoryService.findCategoryWhereParentIsNull();
            List<Product> products = productService.findByCategoryIsNull();
            refreshBreadcrumbs();
            for (Category category : categories) {
                categoryLayout.add(createCategoryCard(category));
            }
            for (Product product : products) {
                categoryLayout.add(createCategoryCard(product));
            }
        }

    }

    private final HorizontalLayout breadcrumbLayout = new HorizontalLayout();


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

}
