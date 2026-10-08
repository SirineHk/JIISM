# JIISM Frontend

Frontend Angular/Tailwind basé sur le template Tailwind Admin Free et adapté au projet JIISM.

## Stack
- Angular 21
- Tailwind CSS 4
- Angular Material
- ApexCharts / composants du template conservés
- Backend Spring Boot attendu sur http://localhost:9090

## Pages JIISM
- /dashboard
- /articles
- /articles/submit
- /assignments
- /announcements
- /users
- /profile
- /publications
- /authentication/login
- /authentication/register

## Backend déjà ciblé
- POST /auth/login
- GET /articles
- GET /article-assignments
- GET /announcements
- POST /files/upload-pdf (à brancher dans le formulaire PDF)

## Lancement
npm install
npm start

Puis ouvrir http://localhost:4200

Le frontend utilise des données de secours lorsque les endpoints backend ne répondent pas, ce qui permet d'afficher les pages pendant le développement.
