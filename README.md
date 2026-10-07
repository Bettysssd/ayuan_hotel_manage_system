# Comfort Hotel Management System

<!-- EN -->

A full-stack hotel management system built for **hotel front-desk operators, supervisors and guests**. It covers room reservations, dining/canteen booking, goods (mini-market), parking, user accounts, order management and department-based staff coordination.

> 🌐 Select language — 语言: **[English (EN)](#english) · [Français (FR)](./README.fr.md)**
>
> 🏕 More versions: [README.fr.md](./README.fr.md)

---

## 🇬🇧 English <a id="english"></a>

<table><tr><td><b>Frontend</b></td><td>Vue 3 · Vite · Element Plus · ECharts · Vue Router · Axios</td></tr>
<tr><td><b>Backend</b></td><td>Spring Boot 2.6.13 · MyBatis · Redis · JWT · Knife4j (Swagger)</td></tr>
<tr><td><b>Database</b></td><td>MySQL</td></tr>
<tr><td><b>Ops</b></td><td>Docker · Nginx</td></tr></table>

### ✨ Features
- **Room management** — browse, book and manage rooms.
- **Dining / canteen** — book meals, manage canteen.
- **Goods (mini-market)** — book and manage goods.
- **Parking** — book and manage parking spaces.
- **Users & roles** — guest accounts, supervisor and waiter roles, staff departments.
- **Orders** — place and review orders.
- **Supervisor dashboard** — management UI with graphical statistics (ECharts).
- **Auth & security** — JWT-based login, Redis caching, OSS upload.

### 📁 Repository layout
This repository bundles the whole system in one place, split across branches:

| Branch | Purpose |
|--------|---------|
| `dev` | Consolidated codebase from both parts |
| `dev_back` | Backend only — Spring Boot / Java (Java 8, Maven `pom.xml`) |
| `dev_front` | Frontend only — Vue 3 / Vite (`package.json`) |

### 🚀 Getting started
**Backend** (`dev_back` or `dev` `pom.xml`, Java 8 + Maven):
```bash
# configure MySQL + Redis in application*.yml first
mvn spring-boot:run
```

**Frontend** (`dev_front`):
```bash
npm install
npm run dev      # development
npm run build    # production build
```

### 🗺️ More
See branch-specific READMEs and the in-code comments for detailed API documentation.

---

### Genealogy — this repo
[README.fr.md](./README.fr.md) · [English (top)](#english) · [Français](./README.fr.md#français)