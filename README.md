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