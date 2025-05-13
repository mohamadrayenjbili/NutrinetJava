# Nutrinet

## Overview

**Nutrinet** is a full-featured health care web platform developed as part of the coursework at Esprit School of Engineering.
It empowers users to take control of their physical and mental well-being through a variety of health programs, medical and nutritional consultations, an integrated online store, and interactive community tools.

From **mental wellness** to diet programs, **cardiac coherence**, and **smoking cessation**, Nutrinet is designed to deliver a personalized and accessible health experience. Users can also **book appointments**, **submit feedback**, and **purchase** certified **health products**.


## Features

* 👤 User Management: Registration, login, profile editing.
* 🧘 Health Programs: Access curated programs (mental health, diet, cardiac coherence, etc.).
* 📅 Appointment System: Book consultations with nutritionists or doctors.
* 🛍️ Online Health Store: Purchase healthcare and wellness products.
* 📨 Reclamations Module: Submit, rate, and track reclamations.
* 📈 Client Performance Tracking: Track personal progress within programs and view performance analytics.
* 🔐 **Admin Dashboard**: Manage users, programs, appointments, products, and reclamations.

## Tech Stack

### Application Layer

* **Java 17+**
* **JavaFX** (UI Framework)
* **SceneBuilder** (UI Design)

### Backend & Persistence

* **Hibernate / JPA** (ORM)
* **MySQL** (Database)
* **JDBC** (Optional for basic operations)

### Tools & Libraries

* **Maven** (Dependency management)
* **JFoenix / ControlsFX** (UI Components)
* **Gson or Jackson** (for JSON processing)
* **Git / GitHub** (Version control)
* **IntelliJ IDEA / Eclipse** (IDE)

## Project Structure

```
📦 Nutrinet
├── .idea
├── .mvn
│   └── wrapper
├── src
│   └── main
│       ├── java
│       │   └── org.example.nutrinet
│       │       ├── app
│       │       ├── controller
│       │       ├── model
│       │       ├── service
│       │       └── util
│       └── resources
│           ├── assets
│           ├── styles
│           ├── uploadedImages
│           └── org.example.nutrinet.view
│               ├── auth
│               ├── backoffice
│               ├── frontoffice
│               └── components
├── target
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java JDK 17+
- Maven
- MySQL
- JavaFX SDK
- IntelliJ IDEA or other IDE with JavaFX support

### Installation

1. Clone the repo:

```bash
git clone https://github.com/mohamadrayenjbili/NutrinetJava
cd NutrinetJava
```

2. Configure your database:

   Update 'src/main/java/org/example/nutrinet/App/DatabaseConnection.java' with:

   ``
   DB_URL=jdbc:mysql://localhost:3306/nutrinet
   DB_USER=your-username
   DB_PASS=your-password
   ```

3. Run SQL schema:

   Use the `sql/schema.sql` file to set up the database structure.

4. Build and Run the Project:

   ```bash
   mvn clean install
   mvn javafx:run
   ```

## Usage


1. **Register** as a user on the platform.
2. **Browse** health programs (diet, mental health, cardiac coherence, etc.) and follow those that fit your needs.
3. **Book appointments** with certified doctors or nutritionists through the appointment system.
4. **Purchase health care products** from the online store using secure Stripe payment.
5. **Interact with programs** by commenting, liking/disliking, and reading others' feedback.
6. **Submit and rate reclamations** for services or orders that did not meet your expectations.
7. **Download your order invoice** by scanning the QR code received after a purchase.
8. **Receive updates** by email when new programs are added.
9. **Admin access** Manage users, programs, appointments, comments, and store inventory.

## Hosting

This project is hosted on **GitHub Education** as a public repository.
Currently hosted locally for development purposes. Optional future deployments can be considered on platforms like Heroku, DigitalOcean, or Namecheap.

## Tests

This project currently uses manual testing via the UI. Automated tests will be integrated in future releases.



## Contributions

We welcome community contributions!

1. Fork the repo.
2. Create a new branch:

   ```bash
   git checkout -b feature/your-feature
   ```

3. Commit your changes:

   ```bash
   git commit -m "Add new feature"
   ```

4. Push and create a pull request:

   ```bash
   git push origin feature/your-feature
   ```

## Acknowledgments

Developed as part of the coursework at **Esprit School of Engineering**.
Special thanks to our professors and inspiration from leading health platforms.

Technologies and tools like **ChatGPT**, **GitHub Copilot**, **Claude**, and Grok contributed to the success of this project.

## License

Currently not licensed for public reuse. For academic and internal use only.

## Topics (for GitHub Repository)

```
healthcare java javafx nutrition mental-wellness appointments stripe-api qr-code esprit-school-of-engineering


```

## Made By
THE SMURFS TEAM 🧠💪
