# 🎭 Playwright Java Automation Framework
### Saucedemo E2E Test Suite | Java + TestNG + Page Object Model + Parallel Execution + GitHub Actions CI

![Build Status](https://github.com/syedymn/playwright-saucedemo-framework/actions/workflows/playwright.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Playwright](https://img.shields.io/badge/Playwright-Java-green?logo=playwright)
![TestNG](https://img.shields.io/badge/TestNG-7.x-blue)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions-black?logo=github-actions)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 📋 Table of Contents

- [About This Framework](#about-this-framework)
- [Tech Stack](#tech-stack)
- [Framework Architecture](#framework-architecture)
- [Project Structure](#project-structure)
- [Key Design Decisions](#key-design-decisions)
- [Test Coverage](#test-coverage)
- [How to Run](#how-to-run)
- [CI/CD Pipeline](#cicd-pipeline)
- [Debugging Stories](#debugging-stories)
- [Author](#author)

---

## About This Framework

A **production-grade E2E automation framework** built with Playwright Java, TestNG, and Page Object Model — designed to demonstrate enterprise-level framework architecture patterns including thread-safe parallel execution, clean separation of concerns, and fully automated CI/CD integration.

**Built as a personal initiative** to demonstrate modern Playwright capabilities alongside an existing Selenium enterprise background. Designed to mirror the architectural rigor expected in senior SDET roles at enterprise organizations.

**Target application:** [Saucedemo](https://www.saucedemo.com) — a purpose-built e-commerce test application covering login, inventory, cart, and checkout flows.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Browser Automation | Playwright Java SDK |
| Test Framework | TestNG 7.x |
| Design Pattern | Page Object Model (POM) |
| Parallel Execution | ThreadLocal (4-level isolation) |
| Build Tool | Maven |
| CI/CD | GitHub Actions (ubuntu-22.04) |
| Config Management | ConfigReader (classpath-based) |
| Reporting | TestNG built-in + Surefire |

---

## Framework Architecture

```
playwright-saucedemo-framework/
├── src/
│   ├── main/java/
│   │   └── com.syed.framework/
│   │       ├── base/
│   │       │   └── BasePage.java          # Core Playwright setup + ThreadLocal management
│   │       ├── pages/
│   │       │   ├── LoginPage.java         # Login page actions & locators
│   │       │   ├── InventoryPage.java     # Product listing page
│   │       │   ├── CartPage.java          # Shopping cart page
│   │       │   └── CheckoutPage.java      # Checkout flow page
│   │       └── utils/
│   │           └── ConfigReader.java      # Classpath-based properties loader
│   │
│   └── test/java/
│       └── com.syed.tests/
│           ├── LoginTest.java             # Login flow test cases
│           └── CheckoutTest.java          # Full E2E checkout test cases
│
├── src/main/resources/
│   └── config.properties                 # Browser config, base URL, credentials
│
├── testng.xml                            # TestNG suite — parallel execution config
├── .github/workflows/
│   └── playwright.yml                    # GitHub Actions CI pipeline
└── pom.xml                              # Maven dependencies
```

---

## Key Design Decisions

### 1. ThreadLocal at All 4 Levels

The most critical architectural decision in this framework. Playwright's object hierarchy requires isolation at every level for true thread-safety:

```java
private static final ThreadLocal<Playwright> playwrightThread = new ThreadLocal<>();
private static final ThreadLocal<Browser> browserThread     = new ThreadLocal<>();
private static final ThreadLocal<BrowserContext> contextThread = new ThreadLocal<>();
private static final ThreadLocal<Page> pageThread           = new ThreadLocal<>();
```

**Why all 4 levels?** In parallel TestNG execution, threads share class-level state. If even one level is not wrapped in ThreadLocal, two threads can collide — Test A acts on Test B's browser context. Wrapping all 4 levels ensures each thread maintains a completely isolated execution stack from Playwright instance down to Page.

**Teardown matters too:** Each ThreadLocal is explicitly `.remove()`d in `@AfterMethod` to prevent memory leaks across TestNG's thread pool reuse.

---

### 2. ConfigReader with Classpath Loading

```java
public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
```

**Why `getResourceAsStream` instead of `FileInputStream`?**
FileInputStream resolves paths relative to the working directory — which differs between local execution and CI environments. `getResourceAsStream` loads from the classpath, making it environment-agnostic and CI-safe.

---

### 3. Page Object Model — Clean Locator Isolation

Each page class encapsulates its own locators and actions. Tests never directly reference selectors:

```java
public class LoginPage {
    private final Page page;

    // Locators
    private final String usernameField = "#user-name";
    private final String passwordField = "#password";
    private final String loginButton   = "#login-button";   // NOT #login-btn

    public LoginPage(Page page) {
        this.page = page;
    }

    public void login(String username, String password) {
        page.fill(usernameField, username);
        page.fill(passwordField, password);
        page.click(loginButton);
    }
}
```

> **Debugging note:** One early failure came from using `#login-btn` — the actual Saucedemo locator is `#login-button`. This is why locator verification against the live DOM is always step one.

---

## Test Coverage

| # | Test | Page Flow | Status |
|---|------|-----------|--------|
| 1 | Valid user login | LoginPage → InventoryPage | ✅ Pass |
| 2 | Invalid credentials error message | LoginPage | ✅ Pass |
| 3 | Add item to cart | InventoryPage → CartPage | ✅ Pass |
| 4 | Full E2E checkout — single item | Login → Inventory → Cart → Checkout | ✅ Pass |
| 5 | Full E2E checkout — multiple items | Login → Inventory → Cart → Checkout | ✅ Pass |

**All 5 tests run in parallel** across 3 threads via TestNG configuration.

---

## How to Run

### Prerequisites

- Java 17+
- Maven 3.8+
- Node.js (required by Playwright browser binaries installer)

### Install Playwright Browsers

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

### Run All Tests

```bash
mvn clean test
```

### Run in Headed Mode (local debugging only)

Set in `config.properties`:
```properties
headless=false
```

> ⚠️ **Note:** Headed mode requires a display server. It works locally on Windows/Mac but will fail in Linux CI (GitHub Actions) without an X server. Always keep `headless=true` in CI.

### Run Specific Test Class

```bash
mvn clean test -Dtest=LoginTest
mvn clean test -Dtest=CheckoutTest
```

---

## CI/CD Pipeline

GitHub Actions pipeline triggers on every push and pull request to `main`.

```yaml
name: Playwright Tests

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-22.04

    steps:
      - uses: actions/checkout@v3

      - name: Set up Java 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Install Playwright Browsers
        run: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

      - name: Run Tests
        run: mvn clean test

      - name: Upload Test Reports
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-reports
          path: target/surefire-reports/
```

**Build Status:** ![Green](https://img.shields.io/badge/build-passing-brightgreen)

---

## Debugging Stories

Real bugs hit and solved during this build — documented because debugging skill matters as much as building skill.

| Bug | Symptom | Root Cause | Fix |
|-----|---------|------------|-----|
| Node.js teardown error on Windows | `playwright install` crashed with missing package.json | Playwright CLI expected Node.js project context | Wrapped install step in try-catch; used Maven exec plugin |
| ConfigReader FileNotFoundException in CI | Tests passed locally, failed in GitHub Actions | FileInputStream resolves from working directory, which differs in CI | Switched to `getResourceAsStream` for classpath loading |
| Parallel execution object collision | Random test failures under parallel load | Playwright/Browser/Context/Page objects shared across threads | ThreadLocal at all 4 levels |
| Login page not found | `#login-btn` selector returned no element | Actual DOM locator is `#login-button` | Verified against live DOM with browser DevTools |
| Tests hung silently in CI | No timeout, no error — just waiting | `setHeadless(false)` on Linux CI with no display server | Forced `headless=true` for all CI runs |

---

## Author

**Syed Muttaquee**
Senior QA Automation Engineer / SDET
📍 New York, NY | 🟢 US Permanent Resident — No sponsorship required

- 💼 [LinkedIn](https://linkedin.com/in/syed-muttaquee-sdet)
- 🐙 [GitHub](https://github.com/syedymn)
- 🔗 [E-Commerce Selenium Framework](https://github.com/syedymn/IslahiArt-Automation-Framework)
- 🔗 [RestAssured API Framework](https://github.com/syedymn/Restful-Booker-API-Framework-)

---

*Built with Java, Playwright, and a weekend of debugging.*
