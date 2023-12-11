# springboot-backend-accessibilator

This is the backend component of the Accessibilator application, a web app  designed to optimize Word documents for readability, specifically for users with dyslexia.
Amazon DynamoDB serves as the primary data storage solution, while Amazon S3 handles file storage.
For user authentication and authorization, the backend leverages OAuth2, ensuring secure access to the application. Additionally, the system implements passwordless authentication through email, providing a seamless and secure user experience.
To optimize documents for improved readability, the backend relies on the Apache POI library. This versatile library enables efficient processing and enhancement of Word documents in making reading more accessible to readers with dyslexia.


- ## Features
- #### User authentication and authorization 
- #### Passwordless authentication via email
- #### File upload 
- #### Document optimization for dyslexia readability
- #### User profile management


- ## Getting Started
- ### Prerequisites
- #### Java 17 or later
- #### Maven

- ## Configuration
- #### Configure the application by editing the application.properties file. Provide the necessary AWS credentials and other environment-specific configurations.

- ## API Documentation
- #### Access the Swagger UI to explore and test the APIs:
- #### Swagger UI: http://localhost:8080/swagger-ui/index.html
- ####  API Docs: https://documenter.getpostman.com/view/12461648/2s9YeD9tUE