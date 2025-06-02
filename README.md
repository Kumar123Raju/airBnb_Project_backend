------------------------Step by step how to setup stripe for payment-----------------------------------------------------
# Airbnb Booking App with Stripe Integration
This is a Spring Boot-based Airbnb-like booking application that integrates with **Stripe** for secure online payments.
---
## 🔧 Prerequisites
- Java 21
- Maven
- PostgreSQL
- Stripe Account (https://stripe.com)
- Internet access to connect Stripe CLI
---
## 💳 Stripe Payment Integration
We use **Stripe** to handle booking payments. To test payments and webhooks locally, we use the **Stripe CLI**.
---
## 🚀 Install Stripe CLI
### Windows
1. Download the latest `stripe.exe` from the [Stripe CLI GitHub Releases](https://github.com/stripe/stripe-cli/releases/latest).
2. Extract and place it in a known directory (e.g., `C:\stripe`).
3. Add the path to your environment variables:
   - Open **System Properties** → **Environment Variables**
   - Edit `Path` and add: `C:\stripe`
4. Verify installation:
```bash`
stripe version


Step 2: Login to Stripe CLI
Use the command below to authenticate your CLI:
command: stripe login

  2.1 A browser window will open for you to log in to your Stripe account.
  After success, it will generate a local API key for CLI use.

Step 3: Listen for Webhook Events
Run this command to listen for payment events and forward them to your Spring Boot app:
command: stripe listen --forward-to localhost:8080/api/v1/webhook/payment
After starting, you’ll see something like:

Ready! Your webhook signing secret is: whsec_9fd339c2148d65a23bb1dfb959c6daf2be862607de20c318d1475b316cae7362
🔒 Important: Copy this whsec_... value and store it securely. You'll use it to validate webhooks from Stripe.



------------------------------------------------------------------------------------------------------------
# airBnb_Project_backend

Repace application.properties with this: 

spring.application.name=airBnbApp
#Db configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/airbnb
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.servlet.context-path=/api/v1


#security
jwt.secretKey=asdfjooodoadjfojdfojaofjosghafhohikigmyggmbmygbg

frontend.url=http://localhost:8080

stripe.secret.key=sk_test_51RSbPxB7dzzZUpgFnDE9l2zx4vYcKdMijZsmXu2PurkLQPrzd2eiwwdMjZEyeT1KicZeGHd3ac8bZlb90poYjk5x00LtfaZqSk

stripe.webhook.secret=whsec_9fd339c2148d65a23bb1dfb959c6daf2be862607de20c318d1475b316cae7362



