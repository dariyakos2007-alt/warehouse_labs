import { useEffect, useMemo, useState } from 'react'
import {
  createCategory,
  createProduct,
  createSupplier,
  createStock,
  createWarehouse,
  deleteCategory,
  deleteProduct,
  deleteSupplier,
  deleteStock,
  deleteWarehouse,
  getCategories,
  getProducts,
  getProductsWithDetails,
  getStocks,
  getSuppliers,
  getWarehouses,
  updateCategory,
  updateProduct,
  updateSupplier,
  updateStock,
  updateWarehouse,
} from './services/api'
import './App.css'

const PAGE_SIZE = 6

const emptyProduct = {
  name: '',
  price: '',
  categoryId: '',
  supplierIds: [],
  photoUrl: '',
}

const emptyCategory = {
  name: '',
  description: '',
}

const emptySupplier = {
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
}

const emptyWarehouse = {
  name: '',
  address: '',
  phone: '',
}

const emptyStock = {
  productId: '',
  warehouseId: '',
  quantity: '',
  maxQuantity: '',
}

const pages = [
  { id: 'products', label: 'Товары', icon: '📦' },
  { id: 'categories', label: 'Категории', icon: '🏷️' },
  { id: 'suppliers', label: 'Поставщики', icon: '🚚' },
  { id: 'stocks', label: 'Остатки', icon: '📊' },
  { id: 'warehouses', label: 'Склады', icon: '🏭' },
]

const productImages = [
  'https://images.unsplash.com/photo-1586864387967-d02ef85d93e8?auto=format&fit=crop&w=240&q=80',
  'https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=240&q=80',
  'https://images.unsplash.com/photo-1518709268805-4e9042af2176?auto=format&fit=crop&w=240&q=80',
  'https://images.unsplash.com/photo-1609205807107-e8ec2120f9de?auto=format&fit=crop&w=240&q=80',
  'https://images.unsplash.com/photo-1530124566582-a618bc2615dc?auto=format&fit=crop&w=240&q=80',
  'https://images.unsplash.com/photo-1562259949-e8e7689d7828?auto=format&fit=crop&w=240&q=80',
]

const supplierImages = [
  'https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=420&q=80',
  'https://images.unsplash.com/photo-1573164713714-d95e436ab8d6?auto=format&fit=crop&w=420&q=80',
  'https://images.unsplash.com/photo-1556157382-97eda2d62296?auto=format&fit=crop&w=420&q=80',
  'https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&w=420&q=80',
]

function getInitialPage() {
  const page = window.location.hash.replace('#/', '') || 'products'
  return pages.some((item) => item.id === page) ? page : 'products'
}

function getImageById(id, images) {
  return images[Math.abs(Number(id) || 0) % images.length]
}

function usePagination(items, pageSize = PAGE_SIZE) {
  const [page, setPage] = useState(1)
  const pageCount = Math.max(1, Math.ceil(items.length / pageSize))
  const safePage = Math.min(page, pageCount)
  const start = (safePage - 1) * pageSize

  return {
    page: safePage,
    pageCount,
    pageItems: items.slice(start, start + pageSize),
    setPage,
  }
}

function App() {
  const [activePage, setActivePage] = useState(getInitialPage)
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [suppliers, setSuppliers] = useState([])
  const [stocks, setStocks] = useState([])
  const [warehouses, setWarehouses] = useState([])
  const [filters, setFilters] = useState({
    categoryName: '',
    maxPrice: '',
    productName: '',
  })
  const [productForm, setProductForm] = useState(null)
  const [categoryForm, setCategoryForm] = useState(null)
  const [supplierForm, setSupplierForm] = useState(null)
  const [stockForm, setStockForm] = useState(null)
  const [warehouseForm, setWarehouseForm] = useState(null)
  const [confirmDialog, setConfirmDialog] = useState(null)
  const [supplierModal, setSupplierModal] = useState(null)
  const [categoryModal, setCategoryModal] = useState(null)
  const [imageModal, setImageModal] = useState(null)
  const [supplierPhotos, setSupplierPhotos] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('supplierPhotos') ?? '{}')
    } catch {
      return {}
    }
  })
  const [productPhotos, setProductPhotos] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('productPhotos') ?? '{}')
    } catch {
      return {}
    }
  })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const suppliersById = useMemo(
    () => new Map(suppliers.map((supplier) => [supplier.id, supplier])),
    [suppliers],
  )

  const productTotalStocks = useMemo(() => {
    const map = new Map()
    stocks.forEach((stock) => {
      const current = map.get(stock.productId) || 0
      map.set(stock.productId, current + stock.quantity)
    })
    return map
  }, [stocks])

  function navigate(page) {
    setActivePage(page)
    window.history.pushState(null, '', `#/${page}`)
  }

  async function loadProducts(nextFilters = filters) {
    setLoading(true)
    setError('')

    try {
      setProducts(await getProducts(nextFilters))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function loadAll(nextFilters = filters) {
    try {
      const [categoryData, supplierData, stockData, warehouseData] =
        await Promise.all([
          getCategories(),
          getSuppliers(),
          getStocks(),
          getWarehouses(),
        ])

      setCategories(categoryData)
      setSuppliers(supplierData)
      setStocks(stockData)
      setWarehouses(warehouseData)
      await loadProducts(nextFilters)
    } catch (err) {
      setError(err.message)
      setLoading(false)
    }
  }

  useEffect(() => {
    function syncPage() {
      setActivePage(getInitialPage())
    }

    window.addEventListener('hashchange', syncPage)
    window.addEventListener('popstate', syncPage)
    return () => {
      window.removeEventListener('hashchange', syncPage)
      window.removeEventListener('popstate', syncPage)
    }
  }, [])

  useEffect(() => {
    let cancelled = false

    Promise.all([getCategories(), getSuppliers(), getStocks(), getWarehouses()])
      .then(([categoryData, supplierData, stockData, warehouseData]) => {
        if (cancelled) {
          return
        }

        setCategories(categoryData)
        setSuppliers(supplierData)
        setStocks(stockData)
        setWarehouses(warehouseData)
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err.message)
        }
      })

    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    let cancelled = false
    const nextFilters = {
      categoryName: filters.categoryName,
      maxPrice: filters.maxPrice,
      productName: filters.productName,
    }

    const timeout = window.setTimeout(() => {
      setLoading(true)
      setError('')

      getProducts(nextFilters)
        .then((data) => {
          if (!cancelled) {
            setProducts(data)
          }
        })
        .catch((err) => {
          if (!cancelled) {
            setError(err.message)
          }
        })
        .finally(() => {
          if (!cancelled) {
            setLoading(false)
          }
        })
    }, 150)

    return () => {
      cancelled = true
      window.clearTimeout(timeout)
    }
  }, [filters.categoryName, filters.maxPrice, filters.productName])

  function showError(err) {
    setError(err.message)
  }

  async function submitProduct(values) {
    const payload = {
      name: values.name.trim(),
      price: Number(values.price),
      categoryId: Number(values.categoryId),
      supplierIds: values.supplierIds.map(Number),
    }

    let savedProduct
    if (productForm.mode === 'edit') {
      savedProduct = await updateProduct(productForm.id, payload)
    } else {
      savedProduct = await createProduct(payload)
    }

    const productId = savedProduct?.id ?? productForm.id
    const nextPhotos = { ...productPhotos }
    if (values.photoUrl?.trim()) {
      nextPhotos[productId] = values.photoUrl.trim()
    } else {
      delete nextPhotos[productId]
    }
    setProductPhotos(nextPhotos)
    localStorage.setItem('productPhotos', JSON.stringify(nextPhotos))

    setProductForm(null)
    await loadAll(filters)
  }

  async function submitCategory(values) {
    const payload = {
      name: values.name.trim(),
      description: values.description.trim(),
    }

    if (categoryForm.mode === 'edit') {
      await updateCategory(categoryForm.id, payload)
    } else {
      await createCategory(payload)
    }

    setCategoryForm(null)
    await loadAll(filters)
  }

  async function submitSupplier(values) {
    const { photoUrl, ...supplierValues } = values
    const payload = {
      name: supplierValues.name.trim(),
      contactPerson: supplierValues.contactPerson.trim(),
      phone: supplierValues.phone.trim(),
      email: supplierValues.email.trim(),
      address: supplierValues.address.trim(),
    }

    let savedSupplier
    if (supplierForm.mode === 'edit') {
      savedSupplier = await updateSupplier(supplierForm.id, payload)
    } else {
      savedSupplier = await createSupplier(payload)
    }

    const supplierId = savedSupplier?.id ?? supplierForm.id
    const nextPhotos = { ...supplierPhotos }
    if (photoUrl?.trim()) {
      nextPhotos[supplierId] = photoUrl.trim()
    } else {
      delete nextPhotos[supplierId]
    }
    setSupplierPhotos(nextPhotos)
    localStorage.setItem('supplierPhotos', JSON.stringify(nextPhotos))

    setSupplierForm(null)
    await loadAll(filters)
  }

  async function submitStock(values) {
    const payload = {
      productId: Number(values.productId),
      warehouseId: Number(values.warehouseId),
      quantity: Number(values.quantity),
      maxQuantity: Number(values.maxQuantity),
    }

    if (stockForm.mode === 'edit') {
      await updateStock(stockForm.id, payload)
    } else {
      await createStock(payload)
    }

    setStockForm(null)
    await loadAll(filters)
  }

  async function submitWarehouse(values) {
    const payload = {
      name: values.name.trim(),
      address: values.address.trim(),
      phone: values.phone.trim(),
    }

    if (warehouseForm.mode === 'edit') {
      await updateWarehouse(warehouseForm.id, payload)
    } else {
      await createWarehouse(payload)
    }

    setWarehouseForm(null)
    await loadAll(filters)
  }

  function confirmDelete(title, text, action) {
    setConfirmDialog({ title, text, action })
  }

  async function openSupplierProducts(supplier) {
    setSupplierModal({ supplier, products: [], loading: true })

    try {
      const allProducts = await getProductsWithDetails()
      setSupplierModal({
        supplier,
        loading: false,
        products: allProducts.filter((product) =>
          (product.supplierIds ?? []).map(Number).includes(Number(supplier.id)),
        ),
      })
    } catch (err) {
      showError(err)
      setSupplierModal(null)
    }
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">H</span>
          <div>
            <p className="eyebrow">Warehouse</p>
            <h1>Homeberries</h1>
          </div>
        </div>

        <nav className="nav">
          {pages.map((page) => (
            <button
              className={activePage === page.id ? 'nav-link active' : 'nav-link'}
              key={page.id}
              onClick={() => navigate(page.id)}
              type="button"
            >
              <span className="nav-icon">{page.icon}</span> {page.label}
            </button>
          ))}
        </nav>
      </aside>

      <main className="content">
        {error && <div className="alert">{error}</div>}

        {activePage === 'products' && (
          <ProductsPage
            categories={categories}
            filters={filters}
            loading={loading}
            onAdd={() => setProductForm({ mode: 'create', values: emptyProduct })}
            onDelete={(product) =>
              confirmDelete(
                'Удалить товар?',
                `Товар "${product.name}" исчезнет из списка.`,
                async () => {
                  try {
                    await deleteProduct(product.id)
                    await loadAll(filters)
                  } catch {
                    setError('Нельзя удалить товар, у которого есть остатки. Сначала удалите остатки.')
                  }
                },
              )
            }
            onEdit={(product) =>
              setProductForm({
                mode: 'edit',
                id: product.id,
                values: {
                  name: product.name ?? '',
                  price: product.price ?? '',
                  categoryId: product.categoryId ?? '',
                  supplierIds: product.supplierIds ?? [],
                  photoUrl: productPhotos[product.id] ?? '',
                },
              })
            }
            onFiltersChange={setFilters}
            onImageClick={setImageModal}
            productPhotos={productPhotos}
            productTotalStocks={productTotalStocks}
            products={products}
            suppliersById={suppliersById}
          />
        )}

        {activePage === 'categories' && (
          <CategoriesPage
            categories={categories}
            onAdd={() => setCategoryForm({ mode: 'create', values: emptyCategory })}
            onDelete={(category) =>
              confirmDelete(
                'Удалить категорию?',
                `Категория "${category.name}" будет удалена, если к ней не привязаны товары.`,
                async () => {
                  try {
                    await deleteCategory(category.id)
                    await loadAll(filters)
                  } catch {
                    setError('Нельзя удалить категорию, у которой есть товары. Сначала удалите товары или измените их категорию.')
                  }
                },
              )
            }
            onEdit={(category) =>
              setCategoryForm({
                mode: 'edit',
                id: category.id,
                values: {
                  name: category.name ?? '',
                  description: category.description ?? '',
                },
              })
            }
            onShowProducts={(category) => {
              setCategoryModal({ category, products: [], loading: true })
              getProductsWithDetails().then((allProducts) => {
                setCategoryModal({
                  category,
                  loading: false,
                  products: allProducts.filter((product) => product.categoryId === category.id),
                })
              }).catch((err) => {
                showError(err)
                setCategoryModal(null)
              })
            }}
          />
        )}

        {activePage === 'suppliers' && (
          <SuppliersPage
            onAdd={() => setSupplierForm({ mode: 'create', values: emptySupplier })}
            onDelete={(supplier) =>
              confirmDelete(
                'Удалить поставщика?',
                `Поставщик "${supplier.name}" будет удален, если нет связанных товаров.`,
                async () => {
                  try {
                    await deleteSupplier(supplier.id)
                    await loadAll(filters)
                  } catch {
                    setError('Нельзя удалить поставщика, у которого есть товары. Сначала удалите товары или уберите этого поставщика из товаров.')
                  }
                },
              )
            }
            onEdit={(supplier) =>
              setSupplierForm({
                mode: 'edit',
                id: supplier.id,
                values: {
                  name: supplier.name ?? '',
                  contactPerson: supplier.contactPerson ?? '',
                  phone: supplier.phone ?? '',
                  email: supplier.email ?? '',
                  address: supplier.address ?? '',
                  photoUrl: supplierPhotos[supplier.id] ?? '',
                },
              })
            }
            onImageClick={setImageModal}
            onShowProducts={openSupplierProducts}
            supplierPhotos={supplierPhotos}
            suppliers={suppliers}
          />
        )}

        {activePage === 'stocks' && (
          <StocksPage
            onAdd={() => setStockForm({ mode: 'create', values: emptyStock })}
            onDelete={(stock) =>
              confirmDelete(
                'Удалить остаток?',
                `Остаток товара "${stock.productName}" на складе "${stock.warehouseName}" будет удален.`,
                async () => {
                  try {
                    await deleteStock(stock.id)
                    await loadAll(filters)
                  } catch {
                    setError('Не удалось удалить остаток. Попробуйте снова.')
                  }
                },
              )
            }
            onEdit={(stock) =>
              setStockForm({
                mode: 'edit',
                id: stock.id,
                values: {
                  productId: stock.productId ?? '',
                  warehouseId: stock.warehouseId ?? '',
                  quantity: stock.quantity ?? '',
                  maxQuantity: stock.maxQuantity ?? '',
                },
              })
            }
            onImageClick={setImageModal}
            productPhotos={productPhotos}
            stocks={stocks}
          />
        )}

        {activePage === 'warehouses' && (
          <WarehousesPage
            onAdd={() => setWarehouseForm({ mode: 'create', values: emptyWarehouse })}
            onDelete={(warehouse) =>
              confirmDelete(
                'Удалить склад?',
                `Склад "${warehouse.name}" будет удален, если нет связанных остатков.`,
                async () => {
                  try {
                    await deleteWarehouse(warehouse.id)
                    await loadAll(filters)
                  } catch {
                    setError('Нельзя удалить склад, у которого есть остатки. Сначала удалите остатки.')
                  }
                },
              )
            }
            onEdit={(warehouse) =>
              setWarehouseForm({
                mode: 'edit',
                id: warehouse.id,
                values: {
                  name: warehouse.name ?? '',
                  address: warehouse.address ?? '',
                  phone: warehouse.phone ?? '',
                },
              })
            }
            stocks={stocks}
            warehouses={warehouses}
          />
        )}
      </main>

      {productForm && (
        <ProductForm
          categories={categories}
          initialValues={productForm.values}
          mode={productForm.mode}
          onCancel={() => setProductForm(null)}
          onSubmit={submitProduct}
          suppliers={suppliers}
        />
      )}

      {categoryForm && (
        <CategoryForm
          initialValues={categoryForm.values}
          mode={categoryForm.mode}
          onCancel={() => setCategoryForm(null)}
          onSubmit={submitCategory}
        />
      )}

      {supplierForm && (
        <SupplierForm
          initialValues={supplierForm.values}
          mode={supplierForm.mode}
          onCancel={() => setSupplierForm(null)}
          onSubmit={submitSupplier}
        />
      )}

      {warehouseForm && (
        <WarehouseForm
          initialValues={warehouseForm.values}
          mode={warehouseForm.mode}
          onCancel={() => setWarehouseForm(null)}
          onSubmit={submitWarehouse}
        />
      )}

      {stockForm && (
        <StockForm
          initialValues={stockForm.values}
          mode={stockForm.mode}
          onCancel={() => setStockForm(null)}
          onSubmit={submitStock}
          products={products}
          warehouses={warehouses}
        />
      )}

      {confirmDialog && (
        <ConfirmModal
          dialog={confirmDialog}
          onClose={() => setConfirmDialog(null)}
          onError={showError}
        />
      )}

      {supplierModal && (
        <SupplierProductsModal
          modal={supplierModal}
          onClose={() => setSupplierModal(null)}
          onImageClick={setImageModal}
          productPhotos={productPhotos}
          productTotalStocks={productTotalStocks}
        />
      )}

      {categoryModal && (
        <CategoryProductsModal
          modal={categoryModal}
          onClose={() => setCategoryModal(null)}
          onImageClick={setImageModal}
          productPhotos={productPhotos}
          productTotalStocks={productTotalStocks}
          suppliersById={suppliersById}
        />
      )}

      {imageModal && (
        <ImageModal
          src={imageModal}
          onClose={() => setImageModal(null)}
        />
      )}
    </div>
  )
}

function ProductsPage({
  categories,
  filters,
  loading,
  onAdd,
  onDelete,
  onEdit,
  onFiltersChange,
  onImageClick,
  productPhotos,
  productTotalStocks,
  products,
  suppliersById,
}) {
  const pagination = usePagination(products)

  function updateFilter(key, value) {
    pagination.setPage(1)
    onFiltersChange((current) => ({ ...current, [key]: value }))
  }

  return (
    <section className="page">
      <div className="page-header">
        <h2>Товары склада</h2>
        <button className="primary-button" onClick={onAdd} type="button">
          Добавить
        </button>
      </div>

      <div className="toolbar">
        <label>
          Поиск
          <input
            onChange={(event) => updateFilter('productName', event.target.value)}
            placeholder="Название товара"
            type="search"
            value={filters.productName}
          />
        </label>

        <label>
          Максимальная цена
          <input
            min="0"
            onChange={(event) => updateFilter('maxPrice', event.target.value)}
            placeholder="Без ограничения"
            type="number"
            value={filters.maxPrice}
          />
        </label>

        <div className="category-filter">
          <label>
            Категория
            <select
              onChange={(event) => updateFilter('categoryName', event.target.value)}
              value={filters.categoryName}
            >
              <option value="">Все категории</option>
              {categories.map((category) => (
                <option key={category.id} value={category.name}>
                  {category.name} ({category.productCount ?? 0})
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>

      <ProductsTable
        loading={loading}
        onDelete={onDelete}
        onEdit={onEdit}
        onImageClick={onImageClick}
        productPhotos={productPhotos}
        productTotalStocks={productTotalStocks}
        products={pagination.pageItems}
        suppliersById={suppliersById}
      />

      {!loading && products.length > 0 && (
        <Pagination
          page={pagination.page}
          pageCount={pagination.pageCount}
          setPage={pagination.setPage}
        />
      )}
    </section>
  )
}

function ProductsTable({ hideActions, loading, onDelete, onEdit, onImageClick, productPhotos, productTotalStocks, products, suppliersById }) {
  if (loading) {
    return <div className="empty-state">Загружаю товары...</div>
  }

  if (!products.length) {
    return <div className="empty-state">Товаров по выбранным условиям нет.</div>
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Товар</th>
            <th>Цена</th>
            <th>Категория</th>
            <th>Поставщики</th>
            <th>Остаток</th>
            {!hideActions && <th>Действия</th>}
          </tr>
        </thead>
        <tbody>
          {products.map((product) => {
            const supplierNames = (product.supplierIds ?? [])
              .map((id) => suppliersById.get(id)?.name)
              .filter(Boolean)

            return (
              <tr key={product.id}>
                <td>
                  <div className="product-cell">
                    <img
                      alt=""
                      className="product-thumb"
                      src={productPhotos[product.id] || getImageById(product.id, productImages)}
                      onClick={() => onImageClick(productPhotos[product.id] || getImageById(product.id, productImages))}
                    />
                    <span className="strong">{product.name}</span>
                  </div>
                </td>
                <td>{Number(product.price).toLocaleString('ru-RU')} Br</td>
                <td>
                  <span className="pill">{product.categoryName || 'Без категории'}</span>
                </td>
                <td>
                  <div className="chips">
                    {supplierNames.length ? (
                      supplierNames.map((name) => (
                        <span className="chip" key={name}>{name}</span>
                      ))
                    ) : (
                      <span className="muted">Не выбраны</span>
                    )}
                  </div>
                </td>
                <td>{productTotalStocks.get(product.id) || 0}</td>
                {!hideActions && (
                  <td>
                    <div className="actions">
                      <button onClick={() => onEdit(product)} type="button">
                        Редактировать
                      </button>
                      <button
                        className="danger"
                        onClick={() => onDelete(product)}
                        type="button"
                      >
                        Удалить
                      </button>
                    </div>
                  </td>
                )}
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}

function CategoriesPage({ categories, onAdd, onDelete, onEdit, onShowProducts }) {
  const pagination = usePagination(categories)

  return (
    <section className="page">
      <div className="page-header">
        <h2>Категории</h2>
        <button className="primary-button" onClick={onAdd} type="button">
          Добавить
        </button>
      </div>

      <div className="category-list">
        {pagination.pageItems.map((category) => (
          <article className="category-card" key={category.id}>
            <div>
              <h3>{category.name}</h3>
              <p>{category.description || 'Описание не указано'}</p>
            </div>
            <button className="pill" onClick={() => onShowProducts(category)} type="button">
              Просмотр ({category.productCount ?? 0})
            </button>
            <div className="actions">
              <button onClick={() => onEdit(category)} type="button">
                Редактировать
              </button>
              <button className="danger" onClick={() => onDelete(category)} type="button">
                Удалить
              </button>
            </div>
          </article>
        ))}
      </div>

      <Pagination
        page={pagination.page}
        pageCount={pagination.pageCount}
        setPage={pagination.setPage}
      />
    </section>
  )
}

function ProductForm({ categories, initialValues, mode, onCancel, onSubmit, suppliers }) {
  const [values, setValues] = useState(initialValues)
  const [saving, setSaving] = useState(false)
  const [errors, setErrors] = useState({})

  function toggleSupplier(id) {
    setValues((current) => {
      const hasSupplier = current.supplierIds.includes(id)
      return {
        ...current,
        supplierIds: hasSupplier
          ? current.supplierIds.filter((supplierId) => supplierId !== id)
          : [...current.supplierIds, id],
      }
    })
  }

  function validate() {
    const newErrors = {}

    if (!values.name.trim()) {
      newErrors.name = 'Название товара обязательно'
    }

    const price = parseFloat(values.price)
    if (!values.price || isNaN(price) || price <= 0) {
      newErrors.price = 'Цена должна быть положительным числом'
    }

    if (!values.categoryId) {
      newErrors.categoryId = 'Выберите категорию'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!validate()) {
      return
    }

    setSaving(true)

    try {
      await onSubmit(values)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={handleSubmit}>
        <ModalHeader
          onCancel={onCancel}
          title={mode === 'edit' ? 'Редактирование товара' : 'Создание товара'}
        />

        <label>
          Название
          <input
            onChange={(event) =>
              setValues((current) => ({ ...current, name: event.target.value }))
            }
            required
            type="text"
            value={values.name}
          />
          {errors.name && <span className="error">{errors.name}</span>}
        </label>

        <label>
          Цена
          <input
            min="0"
            onChange={(event) =>
              setValues((current) => ({ ...current, price: event.target.value }))
            }
            required
            step="0.01"
            type="number"
            value={values.price}
          />
          {errors.price && <span className="error">{errors.price}</span>}
        </label>

        <label>
          Категория
          <select
            onChange={(event) =>
              setValues((current) => ({ ...current, categoryId: event.target.value }))
            }
            required
            value={values.categoryId}
          >
            <option value="" disabled>Выберите категорию</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name} ({category.productCount ?? 0})
              </option>
            ))}
          </select>
          {errors.categoryId && <span className="error">{errors.categoryId}</span>}
        </label>

        <fieldset>
          <legend>Поставщики</legend>
          <div className="supplier-grid">
            {suppliers.map((supplier) => (
              <label className="check-card" key={supplier.id}>
                <input
                  checked={values.supplierIds.includes(supplier.id)}
                  onChange={() => toggleSupplier(supplier.id)}
                  type="checkbox"
                />
                <span>{supplier.name}</span>
              </label>
            ))}
          </div>
        </fieldset>

        <label>
          Фото
          <input
            onChange={(event) => {
              const file = event.target.files[0]
              if (file) {
                const reader = new FileReader()
                reader.onload = (e) => {
                  setValues((current) => ({ ...current, photoUrl: e.target.result }))
                }
                reader.readAsDataURL(file)
              }
            }}
            type="file"
            accept="image/*"
          />
        </label>

        <FormActions onCancel={onCancel} saving={saving} />
      </form>
    </div>
  )
}

function CategoryForm({ initialValues, mode, onCancel, onSubmit }) {
  const [values, setValues] = useState(initialValues)
  const [saving, setSaving] = useState(false)
  const [errors, setErrors] = useState({})

  function validate() {
    const newErrors = {}

    if (!values.name.trim()) {
      newErrors.name = 'Название категории обязательно'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!validate()) {
      return
    }

    setSaving(true)

    try {
      await onSubmit(values)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={handleSubmit}>
        <ModalHeader
          onCancel={onCancel}
          title={mode === 'edit' ? 'Редактирование категории' : 'Создание категории'}
        />

        <label>
          Название
          <input
            onChange={(event) =>
              setValues((current) => ({ ...current, name: event.target.value }))
            }
            required
            type="text"
            value={values.name}
          />
          {errors.name && <span className="error">{errors.name}</span>}
        </label>

        <label>
          Описание
          <input
            onChange={(event) =>
              setValues((current) => ({ ...current, description: event.target.value }))
            }
            type="text"
            value={values.description}
          />
        </label>

        <FormActions onCancel={onCancel} saving={saving} />
      </form>
    </div>
  )
}

function SuppliersPage({ onAdd, onDelete, onEdit, onImageClick, onShowProducts, supplierPhotos, suppliers }) {
  const pagination = usePagination(suppliers)

  return (
    <section className="page">
      <div className="page-header">
        <h2>Поставщики</h2>
        <button className="primary-button" onClick={onAdd} type="button">
          Добавить
        </button>
      </div>

      <div className="supplier-cards">
        {pagination.pageItems.map((supplier) => (
          <article className="supplier-card" key={supplier.id}>
            <div className="supplier-photo-container">
              <img
                alt=""
                className="supplier-photo"
                src={supplierPhotos[supplier.id] || getImageById(supplier.id, supplierImages)}
                onClick={() => onImageClick(supplierPhotos[supplier.id] || getImageById(supplier.id, supplierImages))}
              />
              <button className="danger supplier-delete-btn" onClick={() => onDelete(supplier)} type="button">
                ×
              </button>
            </div>
            <div className="supplier-body">
              <div>
                <h3>{supplier.name}</h3>
                <p>{supplier.contactPerson || 'Контакт не указан'}</p>
              </div>
              <div className="supplier-meta">
                <span>{supplier.phone || 'Телефон не указан'}</span>
                <span>{supplier.email || 'Email не указан'}</span>
                <span>{supplier.address || 'Адрес не указан'}</span>
              </div>
              <button className="pill" onClick={() => onShowProducts(supplier)} type="button">{supplier.productCount ?? 0} товаров</button>
              <div className="actions">
                <button onClick={() => onEdit(supplier)} type="button">
                  Редактировать
                </button>
              </div>
            </div>
          </article>
        ))}
      </div>

      <Pagination
        page={pagination.page}
        pageCount={pagination.pageCount}
        setPage={pagination.setPage}
      />
    </section>
  )
}

function SupplierForm({ initialValues, mode, onCancel, onSubmit }) {
  const [values, setValues] = useState(initialValues)
  const [saving, setSaving] = useState(false)
  const [errors, setErrors] = useState({})

  function updateField(key, value) {
    setValues((current) => ({ ...current, [key]: value }))
  }

  function validate() {
    const newErrors = {}

    if (!values.name.trim()) {
      newErrors.name = 'Название поставщика обязательно'
    }

    if (values.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
      newErrors.email = 'Введите корректный email адрес'
    }

    if (values.phone && !/^[\d\s\-+()]+$/.test(values.phone)) {
      newErrors.phone = 'Телефон может содержать только цифры, пробелы, дефисы, плюсы и скобки'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!validate()) {
      return
    }

    setSaving(true)

    try {
      await onSubmit(values)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={handleSubmit}>
        <ModalHeader
          onCancel={onCancel}
          title={mode === 'edit' ? 'Редактирование поставщика' : 'Создание поставщика'}
        />

        <label>
          Название
          <input
            onChange={(event) => updateField('name', event.target.value)}
            required
            type="text"
            value={values.name}
          />
          {errors.name && <span className="error">{errors.name}</span>}
        </label>
        <label>
          Контактное лицо
          <input
            onChange={(event) => updateField('contactPerson', event.target.value)}
            type="text"
            value={values.contactPerson}
          />
        </label>
        <label>
          Телефон
          <input
            onChange={(event) => updateField('phone', event.target.value)}
            type="text"
            value={values.phone}
          />
          {errors.phone && <span className="error">{errors.phone}</span>}
        </label>
        <label>
          Email
          <input
            onChange={(event) => updateField('email', event.target.value)}
            type="email"
            value={values.email}
          />
          {errors.email && <span className="error">{errors.email}</span>}
        </label>
        <label>
          Адрес
          <input
            onChange={(event) => updateField('address', event.target.value)}
            type="text"
            value={values.address}
          />
        </label>

        <label>
          Фото
          <input
            onChange={(event) => {
              const file = event.target.files[0]
              if (file) {
                const reader = new FileReader()
                reader.onload = (e) => {
                  updateField('photoUrl', e.target.result)
                }
                reader.readAsDataURL(file)
              }
            }}
            type="file"
            accept="image/*"
          />
        </label>

        <FormActions onCancel={onCancel} saving={saving} />
      </form>
    </div>
  )
}

function StockForm({ initialValues, mode, onCancel, onSubmit, products, warehouses }) {
  const [values, setValues] = useState(initialValues)
  const [saving, setSaving] = useState(false)
  const [errors, setErrors] = useState({})

  function updateField(key, value) {
    setValues((current) => ({ ...current, [key]: value }))
  }

  function validate() {
    const newErrors = {}

    if (!values.productId) {
      newErrors.productId = 'Выберите товар'
    }

    if (!values.warehouseId) {
      newErrors.warehouseId = 'Выберите склад'
    }

    const quantity = parseInt(values.quantity, 10)
    if (!values.quantity || isNaN(quantity) || quantity < 0) {
      newErrors.quantity = 'Количество должно быть неотрицательным целым числом'
    }

    const maxQuantity = parseInt(values.maxQuantity, 10)
    if (!values.maxQuantity || isNaN(maxQuantity) || maxQuantity < 0) {
      newErrors.maxQuantity = 'Максимальное количество должно быть неотрицательным целым числом'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!validate()) {
      return
    }

    setSaving(true)

    try {
      await onSubmit(values)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={handleSubmit}>
        <ModalHeader
          onCancel={onCancel}
          title={mode === 'edit' ? 'Редактирование остатка' : 'Создание остатка'}
        />

        <label>
          Товар
          <select
            onChange={(event) => updateField('productId', event.target.value)}
            required
            value={values.productId}
          >
            <option value="" disabled>Выберите товар</option>
            {products.map((product) => (
              <option key={product.id} value={product.id}>
                {product.name}
              </option>
            ))}
          </select>
          {errors.productId && <span className="error">{errors.productId}</span>}
        </label>

        <label>
          Склад
          <select
            onChange={(event) => updateField('warehouseId', event.target.value)}
            required
            value={values.warehouseId}
          >
            <option value="" disabled>Выберите склад</option>
            {warehouses.map((warehouse) => (
              <option key={warehouse.id} value={warehouse.id}>
                {warehouse.name}
              </option>
            ))}
          </select>
          {errors.warehouseId && <span className="error">{errors.warehouseId}</span>}
        </label>

        <label>
          Количество
          <input
            min="0"
            onChange={(event) => updateField('quantity', event.target.value)}
            required
            step="1"
            type="number"
            value={values.quantity}
          />
          {errors.quantity && <span className="error">{errors.quantity}</span>}
        </label>

        <label>
          Максимум
          <input
            min="0"
            onChange={(event) => updateField('maxQuantity', event.target.value)}
            required
            step="1"
            type="number"
            value={values.maxQuantity}
          />
          {errors.maxQuantity && <span className="error">{errors.maxQuantity}</span>}
        </label>

        <FormActions onCancel={onCancel} saving={saving} />
      </form>
    </div>
  )
}

function ImageModal({ src, onClose }) {
  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="image-modal" onClick={(e) => e.stopPropagation()}>
        <button className="close-button" onClick={onClose} type="button">×</button>
        <img alt="" src={src} />
      </div>
    </div>
  )
}

function CategoryProductsModal({ modal, onClose, onImageClick, productPhotos, productTotalStocks, suppliersById }) {
  const pagination = usePagination(modal?.products ?? [], 5)

  return (
    <div className="modal-backdrop">
      <div className="modal wide-modal">
        <ModalHeader onCancel={onClose} title={`Товары в категории "${modal.category.name}"`} />

        {modal.loading ? (
          <div className="empty-state">Загружаю товары категории...</div>
        ) : modal.products.length ? (
          <>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Товар</th>
                    <th>Цена</th>
                    <th>Поставщики</th>
                    <th>Остаток</th>
                  </tr>
                </thead>
                <tbody>
                  {pagination.pageItems.map((product) => (
                    <tr key={product.id}>
                      <td>
                        <div className="product-cell">
                          <img
                            alt=""
                            className="product-thumb"
                            src={productPhotos[product.id] || getImageById(product.id, productImages)}
                            onClick={() => onImageClick(productPhotos[product.id] || getImageById(product.id, productImages))}
                          />
                          <span className="strong">{product.name}</span>
                        </div>
                      </td>
                      <td>{Number(product.price).toLocaleString('ru-RU')} Br</td>
                      <td>
                        <div className="chips">
                          {(() => {
                            const supplierNames = (product.supplierIds ?? [])
                              .map((id) => suppliersById.get(id)?.name)
                              .filter(Boolean)
                            return supplierNames.length ? (
                              supplierNames.map((name) => (
                                <span className="chip" key={name}>{name}</span>
                              ))
                            ) : (
                              <span className="muted">Не выбраны</span>
                            )
                          })()}
                        </div>
                      </td>
                      <td>{productTotalStocks.get(product.id) || 0}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <Pagination
              page={pagination.page}
              pageCount={pagination.pageCount}
              setPage={pagination.setPage}
            />
          </>
        ) : (
          <div className="empty-state">В этой категории нет товаров.</div>
        )}
      </div>
    </div>
  )
}

function SupplierProductsModal({ modal, onClose, onImageClick, productPhotos, productTotalStocks }) {
  const pagination = usePagination(modal?.products ?? [], 5)

  if (!modal || !modal.supplier || !modal.products) {
    return null
  }

  return (
    <div className="modal-backdrop">
      <div className="modal wide-modal">
        <ModalHeader onCancel={onClose} title={modal.supplier.name} />

        {modal.loading ? (
          <div className="empty-state">Загружаю товары поставщика...</div>
        ) : modal.products.length ? (
          <>
            <ProductsTable
              hideActions={true}
              loading={false}
              onDelete={() => {}}
              onEdit={() => {}}
              onImageClick={onImageClick}
              productPhotos={productPhotos}
              productTotalStocks={productTotalStocks}
              products={pagination.pageItems}
              suppliersById={new Map([[modal.supplier.id, modal.supplier]])}
            />
            <Pagination
              page={pagination.page}
              pageCount={pagination.pageCount}
              setPage={pagination.setPage}
            />
          </>
        ) : (
          <div className="empty-state">У поставщика пока нет товаров.</div>
        )}
      </div>
    </div>
  )
}

function StocksPage({ onAdd, onDelete, onEdit, onImageClick, productPhotos, stocks }) {
  const pagination = usePagination(stocks)

  return (
    <section className="page">
      <div className="page-header">
        <h2>Остатки</h2>
        <button className="primary-button" onClick={onAdd} type="button">
          Добавить
        </button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Товар</th>
              <th>Склад</th>
              <th>Количество</th>
              <th>Максимум</th>
              <th>Статус</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {pagination.pageItems.map((stock) => (
              <tr key={stock.id}>
                <td>
                  <div className="product-cell">
                    <img
                      alt=""
                      className="product-thumb"
                      src={productPhotos[stock.productId] || getImageById(stock.productId, productImages)}
                      onClick={() => onImageClick(productPhotos[stock.productId] || getImageById(stock.productId, productImages))}
                    />
                    <span className="strong">{stock.productName}</span>
                  </div>
                </td>
                <td>{stock.warehouseName}</td>
                <td>{stock.quantity}</td>
                <td>{stock.maxQuantity}</td>
                <td>
                  <span className={stock.overStock ? 'pill warning' : 'pill success'}>
                    {stock.overStock ? 'Превышение' : 'Норма'}
                  </span>
                </td>
                <td>
                  <div className="actions">
                    <button onClick={() => onEdit(stock)} type="button">
                      Редактировать
                    </button>
                    <button
                      className="danger"
                      onClick={() => onDelete(stock)}
                      type="button"
                    >
                      Удалить
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        page={pagination.page}
        pageCount={pagination.pageCount}
        setPage={pagination.setPage}
      />
    </section>
  )
}

function WarehousesPage({ onAdd, onDelete, onEdit, stocks, warehouses }) {
  const pagination = usePagination(warehouses)

  function countProducts(warehouse) {
    const stockProducts = new Set(
      stocks
        .filter((stock) => stock.warehouseId === warehouse.id)
        .map((stock) => stock.productId),
    )

    return warehouse.totalProducts ?? stockProducts.size
  }

  return (
    <section className="page">
      <div className="page-header">
        <h2>Склады</h2>
        <button className="primary-button" onClick={onAdd} type="button">
          Добавить
        </button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Название</th>
              <th>Адрес</th>
              <th>Телефон</th>
              <th>Товаров</th>
              <th>Количество товаров</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {pagination.pageItems.map((warehouse) => {
              const warehouseStocks = stocks.filter(
                (stock) => stock.warehouseId === warehouse.id,
              )

              return (
                <tr key={warehouse.id}>
                  <td className="strong">{warehouse.name}</td>
                  <td>{warehouse.address}</td>
                  <td>{warehouse.phone}</td>
                  <td>{countProducts(warehouse)}</td>
                  <td>
                    {warehouseStocks.reduce(
                      (sum, stock) => sum + (stock.quantity ?? 0),
                      0,
                    )}
                  </td>
                  <td>
                    <div className="actions">
                      <button onClick={() => onEdit(warehouse)} type="button">
                        Редактировать
                      </button>
                      <button
                        className="danger"
                        onClick={() => onDelete(warehouse)}
                        type="button"
                      >
                        Удалить
                      </button>
                    </div>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>

      <Pagination
        page={pagination.page}
        pageCount={pagination.pageCount}
        setPage={pagination.setPage}
      />
    </section>
  )
}

function WarehouseForm({ initialValues, mode, onCancel, onSubmit }) {
  const [values, setValues] = useState(initialValues)
  const [saving, setSaving] = useState(false)
  const [errors, setErrors] = useState({})

  function updateField(key, value) {
    setValues((current) => ({ ...current, [key]: value }))
  }

  function validate() {
    const newErrors = {}

    if (!values.name.trim()) {
      newErrors.name = 'Название склада обязательно'
    }

    if (values.phone && !/^[\d\s\-+()]+$/.test(values.phone)) {
      newErrors.phone = 'Телефон может содержать только цифры, пробелы, дефисы, плюсы и скобки'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  async function handleSubmit(event) {
    event.preventDefault()

    if (!validate()) {
      return
    }

    setSaving(true)

    try {
      await onSubmit(values)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <form className="modal" onSubmit={handleSubmit}>
        <ModalHeader
          onCancel={onCancel}
          title={mode === 'edit' ? 'Редактирование склада' : 'Создание склада'}
        />

        <label>
          Название
          <input
            onChange={(event) => updateField('name', event.target.value)}
            required
            type="text"
            value={values.name}
          />
          {errors.name && <span className="error">{errors.name}</span>}
        </label>
        <label>
          Адрес
          <input
            onChange={(event) => updateField('address', event.target.value)}
            type="text"
            value={values.address}
          />
        </label>
        <label>
          Телефон
          <input
            onChange={(event) => updateField('phone', event.target.value)}
            type="text"
            value={values.phone}
          />
          {errors.phone && <span className="error">{errors.phone}</span>}
        </label>

        <FormActions onCancel={onCancel} saving={saving} />
      </form>
    </div>
  )
}

function ConfirmModal({ dialog, onClose, onError }) {
  const [saving, setSaving] = useState(false)

  async function handleConfirm() {
    setSaving(true)

    try {
      await dialog.action()
      onClose()
    } catch (err) {
      onError(err)
      onClose()
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <div className="modal confirm-modal">
        <h2>{dialog.title}</h2>
        <p>{dialog.text}</p>
        <div className="modal-actions">
          <button className="ghost-button" onClick={onClose} type="button">
            Отмена
          </button>
          <button
            className="primary-button danger-button"
            disabled={saving}
            onClick={handleConfirm}
            type="button"
          >
            {saving ? 'Удаляю...' : 'Удалить'}
          </button>
        </div>
      </div>
    </div>
  )
}

function ModalHeader({ onCancel, title }) {
  return (
    <div className="modal-header">
      <h2>{title}</h2>
      <button className="ghost-button" onClick={onCancel} type="button">
        Закрыть
      </button>
    </div>
  )
}

function FormActions({ onCancel, saving }) {
  return (
    <div className="modal-actions">
      <button className="ghost-button" onClick={onCancel} type="button">
        Отмена
      </button>
      <button className="primary-button" disabled={saving} type="submit">
        {saving ? 'Сохраняю...' : 'Сохранить'}
      </button>
    </div>
  )
}

function Pagination({ page, pageCount, setPage }) {
  if (pageCount <= 1) {
    return null
  }

  return (
    <div className="pagination">
      <span>
        Страница {page} из {pageCount}
      </span>
      <div className="page-buttons">
        <button disabled={page === 1} onClick={() => setPage(page - 1)} type="button">
          Назад
        </button>
        {Array.from({ length: pageCount }, (_, index) => index + 1).map((item) => (
          <button
            className={item === page ? 'active' : ''}
            key={item}
            onClick={() => setPage(item)}
            type="button"
          >
            {item}
          </button>
        ))}
        <button
          disabled={page === pageCount}
          onClick={() => setPage(page + 1)}
          type="button"
        >
          Вперед
        </button>
      </div>
    </div>
  )
}

export default App
