import { Product } from '../types/product';

const API_BASE_URL = (import.meta as any).env.VITE_API_URL || 'http://localhost:8080/api';

export const ProductService = {
  async getAll(category?: string): Promise<Product[]> {
    const url = category ? `${API_BASE_URL}/products?category=${encodeURIComponent(category)}` : `${API_BASE_URL}/products`;
    const res = await fetch(url);
    if (!res.ok) throw new Error(`Error ${res.status}: ${res.statusText}`);
    return res.json();
  },

  async getById(id: number): Promise<Product> {
    const res = await fetch(`${API_BASE_URL}/products/${id}`);
    if (!res.ok) throw new Error(`Error ${res.status}: ${res.statusText}`);
    return res.json();
  },

  async create(product: Omit<Product, 'id'>): Promise<Product> {
    const res = await fetch(`${API_BASE_URL}/products`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product),
    });
    if (!res.ok) throw new Error(`Error al crear producto: ${res.statusText}`);
    return res.json();
  },

  async update(id: number, product: Partial<Product>): Promise<Product> {
    const res = await fetch(`${API_BASE_URL}/products/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product),
    });
    if (!res.ok) throw new Error(`Error al actualizar producto: ${res.statusText}`);
    return res.json();
  },

  async delete(id: number): Promise<void> {
    const res = await fetch(`${API_BASE_URL}/products/${id}`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error(`Error al eliminar producto: ${res.statusText}`);
  },
};
