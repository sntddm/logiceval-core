# LogicEval 🧠🤖

LogicEval is a high-performance, low-latency Modular Monolith built on **Spring Boot** and **Spring AI**. It utilizes RAG (Retrieval-Augmented Generation) against an **Ollama LLM** instance to parse and analyze text payloads for logical fallacies, persisting embeddings into a secure **PostgreSQL PGVector** instance.

The entire API is guarded by a stateless, zero-cookie asymmetric **OAuth2 JWT architecture** alongside **Bucket4j identity rate-limiting**.

---

## 🛠️ Architecture Stack
* **Backend Framework:** Spring Boot 3.x / 4.x (Spring Modulith structure)
* **AI Core Orchestration:** Spring AI (Inference: `llama3` | Embeddings: `nomic-embed-text`)
* **Database Engine:** PostgreSQL + PGVector (Lifecycle managed via embedded Dev support)
* **Traffic Management:** Bucket4j Token Bucket rate-limiting (5 req/min per user)
* **Security Layer:** Stateless OAuth2 Resource Server (RSA Asymmetric Key pair)

---

## 🚀 Getting Started

### 1. Fire up the Infrastructure Containers
With Spring Boot's built-in Dev Docker Compose support, you no longer need to run manual docker up commands! Ensure you have a standard `docker-compose.yml` file present in the project root containing your PostgreSQL (PGVector) and Ollama image declarations.

### 2. Generate Your Asymmetric Cryptographic Keys
Spring Security's OAuth2 resource server requires an asymmetric RSA key pair to sign (Private Key) and decode/verify (Public Key) state tokens. 

Open your terminal in the root directory of your project and run these standard OpenSSL commands to generate your local files:

```bash
# 1. Generate a secure 2048-bit RSA private key
openssl genrsa -out app-private.pem 2048

# 2. Extract the matching cryptographic public key from it
openssl rsa -in app-private.pem -pubout -out app-public.pem
```
---
### 🌐 Running in Production (Overriding Environment Properties)

To run LogicEval with external cloud services or custom infrastructure credentials without changing the source configuration files, pass the system properties directly to the application runner.

#### 🪟 Windows (Command Prompt):
```cmd
gradlew bootRun ^
  -Dspring.datasource.url="jdbc:postgresql://your-prod-db-host:5432/logiceval_db" ^
  -Dspring.datasource.username="your_real_user" ^
  -Dspring.datasource.password="your_real_secure_password" ^
  -Dspring.ai.ollama.chat.base-url="http://your-remote-ollama:11434/v1" ^
  -Dspring.ai.ollama.embedding.base-url="http://your-remote-ollama:11434/v1"
```
#### 🪟 macOS / Linux / Git Bash:
```
./gradlew bootRun \
  -Dspring.datasource.url="jdbc:postgresql://your-prod-db-host:5432/logiceval_db" \
  -Dspring.datasource.username="your_real_user" \
  -Dspring.datasource.password="your_real_secure_password" \
  -Dspring.ai.ollama.chat.base-url="http://your-remote-ollama:11434/v1" \
  -Dspring.ai.ollama.embedding.base-url="http://your-remote-ollama:11434/v1"
```
---

### 💰 Commercial Use & Dual Licensing
> 👋 **Note for Hiring Managers & Reviewers:** You are fully authorized to clone, compile, run, and review this project locally for evaluation or interview purposes completely free of charge.


This project is dual-licensed under the **GNU Affero General Public License (AGPLv3)** and a proprietary **Commercial Enterprise License**.

### 🌐 Open Source Use (AGPLv3)
You are permitted to clone, modify, and run this system for personal, academic, or evaluation purposes for free under the terms of the AGPLv3. 

> ⚠️ **The Network Server Clause:** Because this software is designed to run over a network, if you host this code on a server to power a cloud service, SaaS application, web platform, or public API, **you are legally obligated to open-source your entire infrastructure and proprietary application code under the same AGPLv3 terms.**

### 🏢 Commercial Enterprise Use
If your organization or startup wants to integrate this logical evaluation engine into a closed-source product, commercial application, or internal corporate environment without disclosing your proprietary code, **you must acquire a Commercial License Exception.**

Purchasing a commercial license legally removes the AGPLv3 copyleft restrictions. For commercial licensing fees, customized enterprise terms, or custom implementation inquiries, please contact the author directly:

📩 **Licensing Inquiries:** `sntddm77@gmail.com`

This project is dual-licensed under the **GNU Affero General Public License (AGPLv3)** and a proprietary **Commercial Enterprise License**...

---
*Copyright © 2026 Santo Addamo. All rights reserved.*

