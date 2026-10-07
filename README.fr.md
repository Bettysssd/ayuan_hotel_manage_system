# Comfort – Système de gestion hôtelière

<!-- FR -->

Un système complet de gestion hôtelière destiné aux **réceptionnistes, superviseurs et clients**. Il couvre les réservations de chambres, la restauration (cantines), les marchandises (supermarché), le stationnement, les comptes utilisateurs, la gestion des commandes et la coordination des équipes par département.

> 🌐 Choisissez la langue — 语言: **[English (EN)](./README.md) · [Français (FR)](#français)**
>
> 🏕 Autres versions: [README.md](./README.md) (EN)

---

## 🇫🇷 Français <a id="français"></a>

<table><tr><td><b>Frontend</b></td><td>Vue 3 · Vite · Element Plus · ECharts · Vue Router · Axios</td></tr>
<tr><td><b>Backend</b></td><td>Spring Boot 2.6.13 · MyBatis · Redis · JWT · Knife4j (Swagger)</td></tr>
<tr><td><b>Base de données</b></td><td>MySQL</td></tr>
<tr><td><b>Exploitation</b></td><td>Docker · Nginx</td></tr></table>

### ✨ Fonctionnalités
- **Gestion des chambres** — consulter, réserver et gérer les chambres.
- **Restauration / cantine** — réserver les repas, gérer la cantine.
- **Marchandises (supermarché)** — réserver et gérer les produits.
- **Stationnement** — réserver et gérer les places de parking.
- **Utilisateurs & rôles** — comptes clients, rôles superviseur et serveur, départements du personnel.
- **Commandes** — passer et consulter les commandes.
- **Tableau de bord superviseur** — interface de gestion avec statistiques graphiques (ECharts).
- **Authentification & sécurité** — connexion par JWT, cache Redis, téléversement OSS.

### 📁 Organisation du dépôt
Le dépôt regroupe l'ensemble du système, réparti sur plusieurs branches :

| Branche | Objectif |
|---------|----------|
| `dev` | Code consolidé des deux parties |
| `dev_back` | Backend uniquement — Spring Boot / Java (Java 8, `pom.xml` Maven) |
| `dev_front` | Frontend uniquement — Vue 3 / Vite (`package.json`) |

### 🚀 Pour commencer
**Backend** (`dev_back` ou `pom.xml` de `dev`, Java 8 + Maven) :
```bash
# configurer MySQL + Redis dans application*.yml d'abord
mvn spring-boot:run
```

**Frontend** (`dev_front`) :
```bash
npm install
npm run dev      # développement
npm run build    # build de production
```

---

### Dépôt — vues
[README.md](./README.md) (EN) · [English (top)](./README.md) · [Français](#français)