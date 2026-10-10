import React, { useEffect, useState } from 'react';
import { Product } from './types/product';
import { ProductService } from './services/api';

export function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState<number | ''>('');
  const [stock, setStock] = useState<number | ''>('');
  const [category, setCategory] = useState('Computadores');

  const loadProducts = async () => {
    try {
      setLoading(true);
      const data = await ProductService.getAll();
      setProducts(data);
      setError(null);
    } catch (err: any) {
      setError('No se pudo conectar con el Backend. Verifique que product-api esté corriendo.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || price === '' || stock === '') return;

    try {
      await ProductService.create({
        name,
        description,
        price: Number(price),
        stock: Number(stock),
        category
      });
      setName('');
      setDescription('');
      setPrice('');
      setStock('');
      loadProducts();
    } catch (err: any) {
      alert('Error creando producto: ' + err.message);
    }
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    if (confirm('¿Desea eliminar este producto?')) {
      try {
        await ProductService.delete(id);
        loadProducts();
      } catch (err: any) {
        alert('Error al eliminar: ' + err.message);
      }
    }
  };

  return (
    <div className="container">
      <header className="header">
        <div>
          <h1 style={{ margin: 0, color: '#0B3C5D' }}>Catálogo de Inventario</h1>
          <p style={{ margin: 0, color: '#718096' }}>Ingeniería de Software V · Universidad ICESI</p>
        </div>
        <span className="badge-devops">CI/CD Enabled</span>
      </header>

      {error && (
        <div style={{ background: '#FED7D7', color: '#9B2C2C', padding: '1rem', borderRadius: '8px', marginBottom: '1.5rem' }}>
          ⚠️ {error}
        </div>
      )}

      <div className="grid-layout">
        {/* Formulario de Creación */}
        <div className="card">
          <h2 style={{ marginTop: 0, fontSize: '1.25rem', color: '#2D3748' }}>Nuevo Producto</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Nombre:</label>
              <input className="form-input" value={name} onChange={e => setName(e.target.value)} required placeholder="Ej. Teclado Mecánico" />
            </div>

            <div className="form-group">
              <label>Categoría:</label>
              <select className="form-input" value={category} onChange={e => setCategory(e.target.value)}>
                <option value="Computadores">Computadores</option>
                <option value="Monitores">Monitores</option>
                <option value="Audio">Audio</option>
                <option value="Accesorios">Accesorios</option>
              </select>
            </div>

            <div className="form-group">
              <label>Precio (COP):</label>
              <input type="number" className="form-input" value={price} onChange={e => setPrice(Number(e.target.value))} required placeholder="Ej. 150000" min="1" />
            </div>

            <div className="form-group">
              <label>Stock:</label>
              <input type="number" className="form-input" value={stock} onChange={e => setStock(Number(e.target.value))} required placeholder="Ej. 10" min="0" />
            </div>

            <div className="form-group">
              <label>Descripción:</label>
              <textarea className="form-input" rows={2} value={description} onChange={e => setDescription(e.target.value)} placeholder="Detalles del producto" />
            </div>

            <button type="submit" className="btn">+ Registrar Producto</button>
          </form>
        </div>

        {/* Tabla de Productos */}
        <div className="card">
          <h2 style={{ marginTop: 0, fontSize: '1.25rem', color: '#2D3748' }}>Productos Registrados ({products.length})</h2>
          {loading ? (
            <p>Cargando catálogo...</p>
          ) : products.length === 0 ? (
            <p style={{ color: '#718096' }}>No hay productos registrados en el inventario.</p>
          ) : (
            <table className="table-custom">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Categoría</th>
                  <th>Precio</th>
                  <th>Stock</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {products.map(p => (
                  <tr key={p.id}>
                    <td>#{p.id}</td>
                    <td>
                      <strong>{p.name}</strong>
                      <br />
                      <small style={{ color: '#718096' }}>{p.description}</small>
                    </td>
                    <td>{p.category}</td>
                    <td>${Number(p.price).toLocaleString('es-CO')}</td>
                    <td>{p.stock} unid.</td>
                    <td>
                      <button onClick={() => handleDelete(p.id)} className="btn-delete">Eliminar</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
