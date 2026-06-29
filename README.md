# BadWallet — Architecture microservices (Spring Boot)

Projet pedagogique ISM : deux microservices Spring Boot qui communiquent en REST.

```
            +------------------------+
            |     badwallet-api      |   Portefeuilles
            |       Port 8080        |
            +------------------------+
                  |        ^
                  | REST   |
                  v        |
            +----------------------+
            |   payment-service    |   Factures
            |      Port 8081       |
            +----------------------+
```

- **badwallet-api** : gestion des portefeuilles et des transactions (depot, retrait, transfert, paiement).
- **payment-service** : gestion des factures (consultation, reglement).
- Le wallet appelle le payment-service via `PaymentClient` (Spring `RestClient`).

## Prerequis

- Java 17+ (teste avec JDK 21)
- Maven 3.9+ (ou IntelliJ / Eclipse qui embarque Maven)
- Base de donnees : **H2 en memoire** (aucune installation requise)

## Lancer le projet

Ouvrir **deux terminaux** (l'ordre n'a pas d'importance, mais lancez les deux) :

```bash
# Terminal 1 — payment-service (8081)
cd payment-service
mvn spring-boot:run

# Terminal 2 — badwallet-api (8080)
cd badwallet-api
mvn spring-boot:run
```

> Sous IntelliJ : ouvrir chaque dossier comme projet Maven, puis lancer
> `PaymentApplication` et `WalletApplication`.

Le payment-service injecte automatiquement des factures de demonstration au demarrage (`FactureSeeder`).
Pour creer les portefeuilles de demo : `POST http://localhost:8080/wallets/seed`.

Consoles H2 (debug) : `http://localhost:8080/h2-console` et `http://localhost:8081/h2-console`
(JDBC URL `jdbc:h2:mem:walletdb` / `jdbc:h2:mem:paymentdb`, user `sa`, mot de passe vide).

## Endpoints

### badwallet-api (8080)

| Methode | Endpoint                              | Description                  |
|---------|---------------------------------------|------------------------------|
| POST    | `/wallets/seed`                       | Donnees de demonstration     |
| POST    | `/wallets`                            | Creer un portefeuille        |
| GET     | `/wallets`                            | Lister les portefeuilles     |
| GET     | `/wallets/{phone}`                    | Consulter un portefeuille    |
| GET     | `/wallets/{phone}/balance`            | Consulter le solde           |
| GET     | `/wallets/{phone}/transactions`       | Historique des transactions  |
| POST    | `/deposit/{phone}`                    | Depot                        |
| POST    | `/withdraw`                           | Retrait                      |
| POST    | `/transfer`                           | Transfert                    |
| POST    | `/pay`                                | Paiement direct d'un service |
| POST    | `/pay-factures`                       | Payer des factures           |
| GET     | `/external/factures/current?phone=`   | Proxy factures du mois       |
| GET     | `/external/factures/periode?phone=`   | Proxy factures par periode   |

### payment-service (8081)

| Methode | Endpoint                                   | Description                |
|---------|--------------------------------------------|----------------------------|
| POST    | `/pay`                                      | Regler les factures du mois|
| POST    | `/pay-factures`                             | Regler par reference       |
| GET     | `/factures/current?walletCode=`             | Factures du mois courant   |
| GET     | `/factures/current?walletCode=&unite=`      | Filtre par service         |
| GET     | `/factures/periode?walletCode=&start=&end=` | Factures sur une periode   |
| GET     | `/factures?references=`                      | Factures par references    |

Un fichier `requests.http` (a la racine) contient des exemples prets a executer.

## Frais appliques (FeeCalculator)

| Operation | Frais |
|-----------|-------|
| Depot     | 0 %   |
| Paiement  | 0 %   |
| Retrait   | 1 %   |
| Transfert | 1 %   |

## Workflow Git demande par le professeur

Une branche par endpoint. Depuis `main` :

```bash
git checkout -b feature/wallet-seeder
# ... developper l'endpoint, commit ...
git checkout main && git merge feature/wallet-seeder

git checkout -b feature/wallet-creation
# etc.
```

| Branche                        | Endpoint principal                  |
|--------------------------------|-------------------------------------|
| feature/wallet-seeder          | POST /wallets/seed                  |
| feature/wallet-creation        | POST /wallets                       |
| feature/wallet-listing         | GET /wallets                        |
| feature/wallet-consultation    | GET /wallets/{phone} + balance      |
| feature/transaction-deposit    | POST /deposit                       |
| feature/transaction-withdraw   | POST /withdraw                      |
| feature/transaction-transfer   | POST /transfer                      |
| feature/payment-services       | POST /pay + pay-factures            |
| feature/transaction-history    | GET /wallets/{phone}/transactions   |
| feature/proxy-factures         | GET /external/factures/...          |

## Architecture des packages

```
sn.ism.wallet                  sn.ism.payment
├── config                     ├── config
├── exception                  ├── exception
├── common (dto, enums, utils) ├── common (dto, enums)
└── wallet                     └── payment
    ├── controller                 ├── controller
    ├── service (interface+impl)    ├── service (interface+impl)
    ├── repository                  ├── repository
    ├── model                       ├── model
    ├── dto                         ├── dto
    ├── mapper                      ├── mapper
    └── client (PaymentClient)      └── seeder
```

> Le package racine est `sn.ism` (Senegal/ISM). Renommez-le si votre
> professeur impose une autre convention (`com.ism`, `sn.ism.gi`, etc.).
# examenDesignPattern-
