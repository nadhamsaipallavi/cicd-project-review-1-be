# Property Management System Backend

This is the backend service for the Property Management System, built with Spring Boot and Spring Security.

## Features

- User Authentication and Authorization with JWT
- Role-based Access Control (Admin, Landlord, Tenant)
- User Management
- Property Management
- Maintenance Request Management
- Payment Processing
- Messaging System

## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- MySQL 8.0 or later

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/property-management-system.git
cd property-management-system/backend
```

2. Configure the database:
- Create a MySQL database named `property_management`
- Update the database credentials in `src/main/resources/application.properties`

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring:boot run
```

The application will be available at `http://localhost:8080`

## API Documentation

### Authentication Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/me` - Get current user information

### User Endpoints

- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

### Property Endpoints

- `GET /api/properties` - Get all properties
- `GET /api/properties/{id}` - Get property by ID
- `POST /api/properties` - Create property (Landlord only)
- `PUT /api/properties/{id}` - Update property (Landlord only)
- `DELETE /api/properties/{id}` - Delete property (Landlord only)

### Maintenance Request Endpoints

- `GET /api/maintenance-requests` - Get all maintenance requests
- `GET /api/maintenance-requests/{id}` - Get maintenance request by ID
- `POST /api/maintenance-requests` - Create maintenance request
- `PUT /api/maintenance-requests/{id}` - Update maintenance request
- `DELETE /api/maintenance-requests/{id}` - Delete maintenance request (Admin only)

### Payment Endpoints

- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `POST /api/payments` - Create payment
- `PUT /api/payments/{id}` - Update payment
- `DELETE /api/payments/{id}` - Delete payment (Admin only)

### Messaging Endpoints

- `GET /api/messages` - Get all messages
- `GET /api/messages/{id}` - Get message by ID
- `POST /api/messages` - Send message
- `PUT /api/messages/{id}` - Update message
- `DELETE /api/messages/{id}` - Delete message

## Security

The application uses JWT (JSON Web Tokens) for authentication and authorization. All endpoints except `/api/auth/**` and `/api/public/**` require authentication.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 