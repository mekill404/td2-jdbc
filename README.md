# ⚽ Exercice TD2 : Java & PostgreSQL (JDBC)<br>
**Sujet :** Filtre, pagination et insertion de données<br>
**Groupe :** K3<br>
**References:** STD24186<br>
**Formulaire de dépôt :** [Lien Google Form](https://forms.gle/d2xfTimCCQicZkwx8)<br>
---
## Description:<br>
![image de java et psql](image.png)
Ce projet illustre la gestion d’un système simplifié de fédération de football en utilisant **Java (JDBC)** et **PostgreSQL**. 
Il met en œuvre des fonctionnalités essentielles telles que :<br> 
- Connexion Java ↔ PostgreSQL via JDBC <br>
- Création et gestion de tables (`Player`, `Team`) <br>
- Insertion de données de test <br>
- Filtrage et recherche multi-critères <br>
- Pagination des résultats <br>
- Respect du principe d’atomicité lors des insertions<br>
---
## Objectifs:
* Réussir à connecter une base de données PostgreSQL avec Java en utilisant JDBC.<br>
* ​Comprendre comment doit être exploitée les données d’une base de données avec Java (ou tout autre langage orienté objet)<br>
* ​Réussir à implémenter la pagination et les recherches multi-critères.<br>
---
## 🚀 Technologies utilisées 
| Langage                                                      | Base de données              | 
|--------------------------------------------------------------|------------------------------| 
| Java (JDBC)                                                  | PostgreSQL                   |
| SQL                                                          | —                            |
---
## 🗂️ Schéma de la base de données 
### Table **Player** 
- `id` : int 
- `name` : varchar 
- `age` : int 
- `position` : enum `[GK, DEF, MIDF, STR]` - `id_team` : int (nullable)
---
### Table **Team** 
- `id` : int 
- `name` : varchar 
- `continent` : enum `[AFRICA, EUROPA, ASIA, AMERICA]` 
> ℹ️ Le type **ENUM** dans PostgreSQL permet de définir une liste fixe de valeurs, garantissant l’intégrité des données. ---
---
## 🛠️ Étapes du projet 
1. **Création de la base et de l’utilisateur** 
- Base de donnée : `mini_football_db` 
- Utilisateur : `mini_football_db_manager` 
- Scripts : `db.sql` 
2. **Création des tables** 
- Tables `Player` et `Team` 
- Scripts : `schema.sql` 
3. **Insertion des données de test** 
- Données fournies via [hei.school](https://hei.school) 
4. **Implémentation des classes Java** 
- `Team` + `ContinentEnum` 
- `Player` + `PlayerPositionEnum` 
- `DBConnection` (connexion via variables d’environnement : `JDBC_URL`, `USERNAME`, `PASSWORD`) 
- `DataRetriever` (méthodes CRUD et recherche) 
--- 
## 🔎 Méthodes principales (DataRetriever) 
- `Team findTeamById(Integer id)`                       => Récupère une équipe et ses joueurs 
- `List<Player> findPlayers(int page, int size)`        =>Pagination des joueurs 
- `List<Player> createPlayers(List<Player> newPlayers)` =>Insertion atomique de joueurs 
- `Team saveTeam(Team teamToSave)`                      =>Sauvegarde ou mise à jour d’une équipe 
- `List<Team> findTeamsByPlayerName(String playerName)` => Recherche d’équipes par nom de joueur 
- `List<Player> findPlayersByCriteria(...)`             => Recherche multi-critères avec pagination
![image des clubs](image-1.png)
--- 
## ✅ Tests attendus 
| Méthode                                                      | Paramètres        | Résultat attendu                          | 
|--------------------------------------------------------------|-------------------|-------------------------------------------| 
| `findTeamById(1)`                                            | id=1              | Real Madrid avec 3 joueurs                |
| `findTeamById(5)`                                            | id=5              | Inter Miami avec liste vide               | 
| `findPlayers(1,2)`                                           | page=1, size=2    | Thibaut Courtois, Dani Carvajal           | 
| `findPlayers(3,5)`                                           | page=3, size=5    | Liste vide                                | 
| `findTeamsByPlayerName("an")`                                | playerName="an"   | Real Madrid, Atletico Madrid              | 
| `findPlayersByCriteria("ud", MIDF, "Madrid", EUROPA, 1, 10)` |                   | Jude Bellingham                           | 
| `createPlayers([Jude, Pedri])`                               |                   | RuntimeException (atomicité)              | 
| `createPlayers([Vini, Pedri])`                               |                   | Liste comprenant Vini et Pedri            | 
| `saveTeam(1, add Vini)`                                      |                   | Real Madrid avec joueurs existants + Vini | 
| `saveTeam(2, empty list)`                                    |                   | FC Barcelone sans joueurs                 | 
---
## 📌 Notes importantes
- Utiliser **PreparedStatement** pour éviter les injections SQL. 
- Respecter le principe d’**atomicité** : soit toutes les insertions réussissent, soit aucune. 
- Les variables de connexion doivent être stockées dans l’environnement (`System.getenv`). 
--- 
## 👥 Auteurs Projet 
réalisé par **mekill404** dans le cadre du TD2 de Java & PostgreSQL.