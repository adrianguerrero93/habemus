# Flujo de trabajo

## Branches

- **`main`**: Código estable, deployado en producción (GitHub Pages)
- **`develop`**: Rama de desarrollo, donde se integran features

## Workflow

```
1. Crear rama feature desde develop
   $ git checkout develop
   $ git checkout -b feature/mi-feature

2. Desarrollar y commitear cambios

3. Push a remote
   $ git push origin feature/mi-feature

4. Crear Pull Request hacia develop en GitHub

5. Review y merge a develop

6. Cuando esté listo para production:
   $ git checkout main
   $ git merge develop
   $ git tag -a vX.X-web -m "descripción"
   $ git push origin main --tags
   
   GitHub Pages automáticamente despliega main
```

## Reglas para `main`

- ✅ Código probado y funcional
- ✅ Todos los tests pasando
- ✅ Creado tag con versión
- ❌ No commits directos (siempre via PR desde develop)

