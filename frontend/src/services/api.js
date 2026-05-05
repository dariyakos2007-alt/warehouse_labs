const API_BASE = '/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || `Ошибка запроса: ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

function toQuery(params) {
  const search = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) {
      search.set(key, value)
    }
  })

  const query = search.toString()
  return query ? `?${query}` : ''
}

export function getProducts(filters = {}) {
  const maxPrice = filters.maxPrice === '' ? '' : Number(filters.maxPrice)
  const productName = filters.productName?.trim().toLowerCase() ?? ''

  function filterByName(products) {
    if (!productName) {
      return products
    }

    return products.filter((product) =>
      product.name?.toLowerCase().includes(productName),
    )
  }

  if (!filters.categoryName) {
    return request('/products').then((products) => {
      const priceFilteredProducts = maxPrice === ''
        ? products
        : products.filter((product) => Number(product.price) <= maxPrice)

      return filterByName(priceFilteredProducts)
    })
  }

  return request(
    `/products/by-category-cached${toQuery({
      categoryName: filters.categoryName,
      maxPrice: maxPrice === '' ? Number.MAX_SAFE_INTEGER : maxPrice,
      page: 0,
      size: 100,
    })}`,
  ).then((data) => filterByName(data.content ?? data))
}

export function getProductsWithDetails() {
  return request('/products/with-details')
}

export function createProduct(product) {
  return request('/products', {
    method: 'POST',
    body: JSON.stringify(product),
  })
}

export function updateProduct(id, product) {
  return request(`/products/${id}`, {
    method: 'PUT',
    body: JSON.stringify(product),
  })
}

export function deleteProduct(id) {
  return request(`/products/${id}`, {
    method: 'DELETE',
  })
}

export function getCategories() {
  return request('/categories/with-products')
}

export function createCategory(category) {
  return request('/categories', {
    method: 'POST',
    body: JSON.stringify(category),
  })
}

export function updateCategory(id, category) {
  return request(`/categories/${id}`, {
    method: 'PUT',
    body: JSON.stringify(category),
  })
}

export function deleteCategory(id) {
  return request(`/categories/${id}`, {
    method: 'DELETE',
  })
}

export function getSuppliers() {
  return request('/suppliers')
}

export function createSupplier(supplier) {
  return request('/suppliers', {
    method: 'POST',
    body: JSON.stringify(supplier),
  })
}

export function updateSupplier(id, supplier) {
  return request(`/suppliers/${id}`, {
    method: 'PUT',
    body: JSON.stringify(supplier),
  })
}

export function deleteSupplier(id) {
  return request(`/suppliers/${id}`, {
    method: 'DELETE',
  })
}

export function getStocks() {
  return request('/stocks/with-details')
}

export function createStock(stock) {
  return request('/stocks', {
    method: 'POST',
    body: JSON.stringify(stock),
  })
}

export function updateStock(id, stock) {
  return request(`/stocks/${id}`, {
    method: 'PUT',
    body: JSON.stringify(stock),
  })
}

export function deleteStock(id) {
  return request(`/stocks/${id}`, {
    method: 'DELETE',
  })
}

export function getWarehouses() {
  return request('/warehouses')
}

export function createWarehouse(warehouse) {
  return request('/warehouses', {
    method: 'POST',
    body: JSON.stringify(warehouse),
  })
}

export function updateWarehouse(id, warehouse) {
  return request(`/warehouses/${id}`, {
    method: 'PUT',
    body: JSON.stringify(warehouse),
  })
}

export function deleteWarehouse(id) {
  return request(`/warehouses/${id}`, {
    method: 'DELETE',
  })
}
