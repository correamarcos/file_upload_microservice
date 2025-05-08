# 🚀 Upload de Arquivos com Microserviços e Programação Reativa

![Status do Projeto](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![License](https://img.shields.io/badge/license-MIT-green)
![Java](https://img.shields.io/badge/java-17-blue)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.0-brightgreen)

Este projeto é uma aplicação moderna de **upload de arquivos**, desenvolvida para praticar os conceitos de **microserviços** e **programação reativa** com `Spring WebFlux`. Ele utiliza um ecossistema robusto com **Spring Gateway**, **MinIO**, **PostgreSQL**, **RabbitMQ**, além de containers com **Docker Compose**.

---

## 🧰 Tecnologias Utilizadas

- ☕ **Java 17**  
- 🌐 **Spring Boot 3 + WebFlux**  
- 🌀 **Spring Cloud Gateway**  
- 🗂 **MinIO** – Armazenamento de arquivos  
- 🐘 **PostgreSQL** – Banco de dados relacional  
- 🐇 **RabbitMQ** – Mensageria assíncrona  
- 🐳 **Docker & Docker Compose** – Ambientes isolados e reproduzíveis  

---

## 🏗️ Arquitetura do Sistema

O projeto adota uma arquitetura baseada em **microserviços desacoplados** e **event-driven**. Os principais serviços são:

| Serviço               | Descrição |
|-----------------------|-----------|
| **Gateway Service**     | Encaminha requisições para os serviços internos. |
| **Upload Service**      | Recebe arquivos e persiste metadados no banco de dados. |
| **Processing Service**  | Processa eventos de upload e interage com o RabbitMQ. |
| **Notification Service**| (Em desenvolvimento) Notifica quando o processamento for concluído. |

> Toda a comunicação é feita de forma **reativa**, assegurando **alta performance**, **escalabilidade** e **baixa latência**.

---

## ⚙️ Como Executar Localmente

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/seu-repositorio.git
   cd seu-repositorio

2. **Execute a limpesa e instalação do projeto com Maven:
   ```bash
   mvn clean install
   
3. **Suba os containers necessários com Docker Compose**:
   ```bash
   docker-compose up -d

---

## 📡 Endpoints da API

1. POST /upload
   - Descrição: Envia um arquivo para armazenamento no MinIO.
   - Body: multipart/form-data
   - Retorno: Status HTTP e metadados do arquivo.

3. GET /files
   - Descrição: Retorna a lista de arquivos armazenados.
   - Retorno: JSON com detalhes dos arquivos.

---

## 📬 Mensageria com RabbitMQ

O RabbitMQ é utilizado para processar eventos assíncronos, garantindo que operações críticas (como validação e processamento de arquivos) ocorram sem bloqueios. Isso melhora a escalabilidade do sistema e evita gargalos de processamento.

---

💡 *Em constante evolução!*  
Se tiver sugestões, feedbacks ou quiser contribuir, sinta-se à vontade para abrir uma **issue** ou **pull request**.

📬 *Fique à vontade para entrar em contato!*

---


🛠️ Desenvolvido com dedicação por **Marcos Correa**

