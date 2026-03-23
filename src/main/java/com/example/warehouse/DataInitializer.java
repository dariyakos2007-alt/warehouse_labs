package com.example.warehouse;

import com.example.warehouse.model.entity.Category;
import com.example.warehouse.model.entity.Product;
import com.example.warehouse.model.entity.Stock;
import com.example.warehouse.model.entity.Supplier;
import com.example.warehouse.model.entity.Warehouse;
import com.example.warehouse.repository.CategoryRepository;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.repository.StockRepository;
import com.example.warehouse.repository.SupplierRepository;
import com.example.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;

@Slf4j
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
    public void run(String... args) {

        if (categoryRepository.count() > 0) {
            log.info("Данные уже есть в базе, инициализация пропущена");
            return;
        }

        log.info("Инициализация тестовых данных...");

        Category tools = categoryRepository.save(
                Category.builder().name("Инструменты").description("Ручные и электроинструменты").build()
        );
        Category fasteners = categoryRepository.save(
                Category.builder().name("Крепеж").description("Гвозди, шурупы, болты").build()
        );
        Category materials = categoryRepository.save(
                Category.builder().name("Материалы").description("Расходные материалы").build()
        );

        Product hammer = productRepository.save(
                Product.builder().name("Молоток").price(35.50).category(tools).build()
        );
        Product screwdriver = productRepository.save(
                Product.builder().name("Отвертка").price(9.99).category(tools).build()
        );
        Product drill = productRepository.save(
                Product.builder().name("Дрель").price(24.00).category(tools).build()
        );
        Product nails = productRepository.save(
                Product.builder().name("Гвозди 100мм").price(4.50).category(fasteners).build()
        );
        Product screws = productRepository.save(
                Product.builder().name("Саморезы").price(6.30).category(fasteners).build()
        );
        Product paint = productRepository.save(
                Product.builder().name("Краска белая").price(12.50).category(materials).build()
        );
        Product brush = productRepository.save(
                Product.builder().name("Кисть малярная").price(8.90).category(materials).build()
        );

        Supplier supplier1 = supplierRepository.save(
                Supplier.builder()
                        .name("ООО ИнструментСервис")
                        .contactPerson("Иванов Иван")
                        .phone("+375291234567")
                        .email("info@instrument.by")
                        .address("Минск, ул. Промышленная 5")
                        .build()
        );
        Supplier supplier2 = supplierRepository.save(
                Supplier.builder()
                        .name("ЧУП КрепежПро")
                        .contactPerson("Петров Петр")
                        .phone("+375297654321")
                        .email("sales@krepezh.by")
                        .address("Минск, ул. Заводская 10")
                        .build()
        );
        Supplier supplier3 = supplierRepository.save(
                Supplier.builder()
                        .name("ИП Сидоров")
                        .contactPerson("Сидоров Сидор")
                        .phone("+375293334455")
                        .email("sidorov@tut.by")
                        .address("Минск, ул. Строителей 15")
                        .build()
        );

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

        Warehouse mainWarehouse = warehouseRepository.save(
                Warehouse.builder()
                        .name("Главный склад")
                        .address("Минск, ул. Логойский тракт 15")
                        .phone("+375172223344")
                        .build()
        );
        Warehouse secondWarehouse = warehouseRepository.save(
                Warehouse.builder()
                        .name("Дополнительный склад")
                        .address("Минск, ул. Тимирязева 70")
                        .phone("+375172233445")
                        .build()
        );
        Warehouse regionalWarehouse = warehouseRepository.save(
                Warehouse.builder()
                        .name("Региональный склад")
                        .address("Гродно, ул. Советская 10")
                        .phone("+375152445566")
                        .build()
        );

        List<Stock> stocks = List.of(
                new Stock(hammer, mainWarehouse, 50, 200),
                new Stock(screwdriver, mainWarehouse, 120, 300),
                new Stock(drill, mainWarehouse, 30, 100),
                new Stock(nails, mainWarehouse, 200, 500),
                new Stock(screws, mainWarehouse, 150, 400),
                new Stock(hammer, secondWarehouse, 25, 100),
                new Stock(screwdriver, secondWarehouse, 60, 150),
                new Stock(paint, secondWarehouse, 80, 200),
                new Stock(brush, secondWarehouse, 45, 100),
                new Stock(paint, regionalWarehouse, 30, 100),
                new Stock(brush, regionalWarehouse, 20, 50),
                new Stock(nails, regionalWarehouse, 100, 300)
        );
        stockRepository.saveAll(stocks);

        log.info("✅ Тестовые данные успешно загружены!");
        log.info("📊 Категорий: {}", categoryRepository.count());
        log.info("📦 Товаров: {}", productRepository.count());
        log.info("🏭 Поставщиков: {}", supplierRepository.count());
        log.info("🏢 Складов: {}", warehouseRepository.count());
        log.info("📋 Остатков: {}", stockRepository.count());
    }
}