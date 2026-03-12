package com.example.warehouse;

import com.example.warehouse.model.entity.Category;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.repository.CategoryRepository;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.repository.SupplierRepository;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (categoryRepository.count() > 0) {
            System.out.println("Данные уже есть в базе, инициализация пропущена");
            return;
        }

        System.out.println("Инициализация тестовых данных...");

        Category tools = new Category("Инструменты", "Ручные и электроинструменты");
        Category fasteners = new Category("Крепеж", "Гвозди, шурупы, болты");
        Category materials = new Category("Материалы", "Расходные материалы");

        tools = categoryRepository.save(tools);
        fasteners = categoryRepository.save(fasteners);
        materials = categoryRepository.save(materials);

        Product hammer = new Product("Молоток", 35.50);
        hammer.setCategory(tools);

        Product screwdriver = new Product("Отвертка", 9.99);
        screwdriver.setCategory(tools);

        Product drill = new Product("Дрель", 24.00);
        drill.setCategory(tools);

        Product nails = new Product("Гвозди 100мм", 4.50);
        nails.setCategory(fasteners);

        Product screws = new Product("Саморезы", 6.30);
        screws.setCategory(fasteners);

        Product paint = new Product("Краска белая", 12.50);
        paint.setCategory(materials);

        Product brush = new Product("Кисть малярная", 8.90);
        brush.setCategory(materials);

        hammer = productRepository.save(hammer);
        screwdriver = productRepository.save(screwdriver);
        drill = productRepository.save(drill);
        nails = productRepository.save(nails);
        screws = productRepository.save(screws);
        paint = productRepository.save(paint);
        brush = productRepository.save(brush);

        Supplier supplier1 = new Supplier(
                "ООО ИнструментСервис",
                "Иванов Иван",
                "+375291234567",
                "info@instrument.by",
                "Минск, ул. Промышленная 5"
        );

        Supplier supplier2 = new Supplier(
                "ЧУП КрепежПро",
                "Петров Петр",
                "+375297654321",
                "sales@krepezh.by",
                "Минск, ул. Заводская 10"
        );

        Supplier supplier3 = new Supplier(
                "ИП Сидоров",
                "Сидоров Сидор",
                "+375293334455",
                "sidorov@tut.by",
                "Минск, ул. Строителей 15"
        );

        supplier1 = supplierRepository.save(supplier1);
        supplier2 = supplierRepository.save(supplier2);
        supplier3 = supplierRepository.save(supplier3);

        hammer.setSuppliers(new HashSet<>());
        hammer.getSuppliers().add(supplier1);

        screwdriver.setSuppliers(new HashSet<>());
        screwdriver.getSuppliers().add(supplier1);

        drill.setSuppliers(new HashSet<>());
        drill.getSuppliers().add(supplier1);

        nails.setSuppliers(new HashSet<>());
        nails.getSuppliers().add(supplier2);

        screws.setSuppliers(new HashSet<>());
        screws.getSuppliers().add(supplier2);

        paint.setSuppliers(new HashSet<>());
        paint.getSuppliers().add(supplier3);

        brush.setSuppliers(new HashSet<>());
        brush.getSuppliers().add(supplier3);

        productRepository.save(hammer);
        productRepository.save(screwdriver);
        productRepository.save(drill);
        productRepository.save(nails);
        productRepository.save(screws);
        productRepository.save(paint);
        productRepository.save(brush);

        Warehouse mainWarehouse = new Warehouse(
                "Главный склад",
                "Минск, ул. Логойский тракт 15",
                "+375172223344"
        );

        Warehouse secondWarehouse = new Warehouse(
                "Дополнительный склад",
                "Минск, ул. Тимирязева 70",
                "+375172233445"
        );

        Warehouse regionalWarehouse = new Warehouse(
                "Региональный склад",
                "Гродно, ул. Советская 10",
                "+375152445566"
        );

        mainWarehouse = warehouseRepository.save(mainWarehouse);
        secondWarehouse = warehouseRepository.save(secondWarehouse);
        regionalWarehouse = warehouseRepository.save(regionalWarehouse);

        Stock stock1 = new Stock(hammer, mainWarehouse, 50, 10, 200);
        Stock stock2 = new Stock(screwdriver, mainWarehouse, 120, 20, 300);
        Stock stock3 = new Stock(drill, mainWarehouse, 30, 5, 100);
        Stock stock4 = new Stock(nails, mainWarehouse, 200, 50, 500);
        Stock stock5 = new Stock(screws, mainWarehouse, 150, 30, 400);

        Stock stock6 = new Stock(hammer, secondWarehouse, 25, 5, 100);
        Stock stock7 = new Stock(screwdriver, secondWarehouse, 60, 10, 150);
        Stock stock8 = new Stock(paint, secondWarehouse, 80, 20, 200);
        Stock stock9 = new Stock(brush, secondWarehouse, 45, 10, 100);

        Stock stock10 = new Stock(paint, regionalWarehouse, 30, 10, 100);
        Stock stock11 = new Stock(brush, regionalWarehouse, 20, 5, 50);
        Stock stock12 = new Stock(nails, regionalWarehouse, 100, 20, 300);

        stockRepository.save(stock1);
        stockRepository.save(stock2);
        stockRepository.save(stock3);
        stockRepository.save(stock4);
        stockRepository.save(stock5);
        stockRepository.save(stock6);
        stockRepository.save(stock7);
        stockRepository.save(stock8);
        stockRepository.save(stock9);
        stockRepository.save(stock10);
        stockRepository.save(stock11);
        stockRepository.save(stock12);

        System.out.println(" Тестовые данные успешно загружены!");
        System.out.println(" Категорий: " + categoryRepository.count());
        System.out.println(" Товаров: " + productRepository.count());
        System.out.println(" Поставщиков: " + supplierRepository.count());
        System.out.println(" Складов: " + warehouseRepository.count());
        System.out.println(" Остатков: " + stockRepository.count());
    }
}