# Jobify - Web-Based Freelancing Platform

**Jobify** is a web-based freelancing platform that connects **Clients** and **Freelancers**. Built with **Java 21**, **Spring Boot 3**, **Spring MVC**, **Thymeleaf**, **Hibernate/JPA**, **Spring Security**, and **MySQL**, this platform provides a complete workflow from job posting to project completion, featuring real-time communication and comprehensive admin oversight.

The Client UI allows clients to post job requirements, view freelancer applications, access freelancer profiles and resumes, shortlist candidates, and track application status. Only verified clients can post jobs, and all job postings require admin approval, ensuring a secure and well-managed hiring process.

---

## 🎯 Features and Functionalities

### ✅ Functional Requirements Status

**All functional requirements have been fully implemented!** See [FUNCTIONAL_REQUIREMENTS.md](FUNCTIONAL_REQUIREMENTS.md) for detailed verification.

### 🔐 User Roles & Security

The system supports three distinct roles with role-based access control:

- **Client**: Post jobs, hire freelancers, manage projects, track ongoing work
- **Freelancer**: Browse jobs, apply for projects, showcase skills, track earnings
- **Admin**: Verify accounts, monitor users/jobs, manage skills, handle disputes, analytics

### 👤 User Registration & Verification

- **Registration**: Clients and Freelancers register with email and password
- **Pending State**: New accounts start in pending state requiring admin verification
- **Identity Verification**: 
  - **Government-issued ID Required**: Users must upload passport, national ID, or driver's license
  - **Photo Matching**: Profile photo must match the ID document photo
  - **Name Verification**: Profile name must exactly match the ID document
  - **Document Authenticity**: Admins verify document legitimacy before approval
- **Business Verification** (Clients Only):
  - **Business License**: Upload valid business registration or license
  - **Company Legitimacy**: Verify business information authenticity
- **Admin Approval**: Admins review profiles and documents before granting full access
- **Email Notifications**: Users receive email notifications on account approval/rejection
- **Verification Status**: Visual indicators show verification progress and status
- **Security Compliance**: Meets KYC (Know Your Customer) requirements for platform trust

### 💼 Job Management

#### For Clients:
The Client Dashboard provides comprehensive job management capabilities:
- **Client Dashboard**: Centralized dashboard with statistics, posted jobs, proposals, and ongoing projects
- **Post Job Requirements**: Create detailed job postings (only verified clients can post)
- **Job Approval**: All job postings require admin approval before going live, ensuring quality and security
- **View Freelancer Proposals**: Review all proposals submitted by freelancers with filtering and status tracking
- **Find Talent**: Browse and search verified freelancers, filter by skills, experience, and location
- **Hire Freelancers**: Update application status to HIRED (only verified clients can hire)
- **Track Ongoing Projects**: Monitor active projects with hired freelancers
- **Manage Jobs**: Edit, view, and manage all posted jobs
- **Payments Tracking**: Monitor payments for active projects
- **Messaging**: Real-time communication with freelancers
- **Profile Settings**: Manage account and verification documents

#### For Freelancers:
The Freelancer Dashboard provides comprehensive job search and application capabilities:
- **Freelancer Dashboard**: Centralized dashboard with statistics, job search, proposals, and projects
- **Find Work**: Browse available jobs from verified clients only with advanced filtering
- **Browse Jobs**: Search and filter jobs with advanced filters (type, location, salary, date)
- **Apply for Jobs**: Submit applications with cover letters and proposed rates (only verified freelancers can apply)
- **My Proposals**: Track all submitted proposals and their status
- **Ongoing Projects**: View active projects where hired
- **Application Tracking**: Track application status in real-time (PENDING, REVIEWED, SHORTLISTED, REJECTED, HIRED)
- **Save Jobs**: Save interesting jobs for later
- **Profile & Skills Management**: Create comprehensive profiles with skills, experience levels, and years of experience
- **Earnings Tracking**: Monitor earnings from active projects and manage withdrawals (requires verification)
- **Messaging**: Real-time communication with clients

### 🔍 Advanced Search & Filtering

- **Job Search**: Search by job title, keywords, or company
- **Location Filter**: Filter by city, state, country, or remote options
- **Salary Range**: Filter jobs by minimum and maximum salary
- **Job Type**: Filter by Full-Time, Part-Time, or Freelance
- **Remote Options**: Filter by Remote Only, Office Only, or Partial Remote
- **Date Filters**: Filter by posted date (Today, Last 7 days, Last 30 days)
- **Pagination**: Efficient pagination for large result sets

### 💬 Real-Time Chat System

- **WebSocket Communication**: Real-time messaging between Clients and Freelancers
- **Job-Specific Chats**: Conversations linked to specific job postings
- **Message History**: Persistent message storage and retrieval
- **Read/Unread Status**: Track message read status
- **Chat Partners**: View list of all conversation partners
- **Real-Time Delivery**: Instant message delivery using WebSocket/STOMP

### 🔔 Notification System

- **Real-Time Notifications**: WebSocket-based instant notifications
- **Notification Types**: 
  - APPLICATION: New applications and status updates
  - MESSAGE: New chat messages
  - JOB: Job-related notifications (approvals, rejections)
  - SYSTEM: System announcements
- **Job Posting Notifications**: 
  - Clients notified when jobs are approved/rejected
  - Email and in-app notifications
- **Proposal Notifications**: 
  - Clients notified of new applications
  - Freelancers notified of application status updates
  - Real-time WebSocket delivery
- **Important Updates**: 
  - Account verification status
  - User approval/rejection
  - Platform announcements
- **Unread Count**: Badge showing unread notification count in navigation
- **Mark as Read**: Individual and bulk mark-as-read functionality
- **Email Integration**: Email notifications for important events complementing in-app notifications
- **Notification History**: View all notifications with filtering

### 📊 Admin Dashboard

- **User Management**: 
  - View all users with pagination
  - Review pending user registrations
  - View user verification documents
  - Approve/reject users
  - Verify user profiles (clients and freelancers)
  - Monitor verified vs unverified accounts
- **Job Management**:
  - View all job postings
  - Review pending jobs
  - Approve/reject job postings
  - View job statistics (active, pending, total)
  - Monitor job activity
- **Application Tracking**: Monitor all applications across the platform
- **Skills Management**: 
  - View all skills in platform
  - Monitor skill trends
  - Identify popular skills
- **Payments & Transactions**: 
  - Monitor active projects
  - Track total project value
  - View payment details
- **Disputes Management**: 
  - Handle reported issues
  - Manage disputes between users
  - Resolution tracking
- **Analytics Dashboard**: 
  - Growth metrics (7-day, 30-day)
  - User activity statistics
  - Job posting trends
  - Application status breakdowns
- **Reports**: Comprehensive platform statistics and analytics
- **Document Review**: Download and review verification documents

### 📈 Application Status Tracking

Applications progress through the following statuses:
- **PENDING**: Initial application submitted
- **REVIEWED**: Application reviewed by client
- **SHORTLISTED**: Freelancer shortlisted for interview
- **REJECTED**: Application rejected
- **HIRED**: Freelancer hired for the project

### 🔒 Security Features

- **Spring Security**: Comprehensive security framework
- **Role-Based Access Control**: Different access levels for Clients, Freelancers, and Admins
- **Password Encryption**: BCrypt password hashing
- **Account Verification**: Two-level verification (admin approval + profile verification)
- **Secure File Uploads**: Protected file upload with size limits
- **CSRF Protection**: Cross-site request forgery protection
- **Session Management**: Secure session handling

---

## 🛠️ Technology Stack

### Backend
- **Java 21**: Latest LTS version
- **Spring Boot 3.4.0**: Modern Spring framework
- **Spring MVC**: Model-View-Controller architecture
- **Spring Data JPA**: Database abstraction layer
- **Spring Security**: Authentication and authorization
- **Spring WebSocket**: Real-time communication
- **Hibernate**: ORM framework
- **Maven**: Build and dependency management

### Frontend
- **Thymeleaf**: Server-side templating engine
- **Bootstrap 5.3.3**: Responsive CSS framework
- **jQuery 3.7.1**: JavaScript library
- **Font Awesome 6.5.2**: Icon library
- **SockJS**: WebSocket fallback
- **STOMP**: WebSocket messaging protocol

### Database
- **MySQL**: Relational database management system

### Additional
- **Spring Mail**: Email notification service
- **Apache Commons IO**: File handling utilities

---

## 📋 Database Schema

The application uses the following main tables:

- `users_type`: User role definitions (Client, Freelancer, Admin)
- `users`: User accounts with approval status
- `job_seeker_profile`: Freelancer profiles with verification
- `recruiter_profile`: Client profiles with business verification
- `job_post_activity`: Job postings with approval status
- `job_seeker_apply`: Applications with status tracking
- `job_seeker_save`: Saved jobs
- `skills`: Freelancer skills and experience
- `job_company`: Company information
- `job_location`: Location data
- `chat_message`: Real-time chat messages
- `notification`: User notifications

---

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd java-jobPortalWebApp-master
   ```

2. **Create MySQL Database**
   ```sql
   CREATE DATABASE jobportal;
   ```

3. **Configure Database**
   - Update `src/main/resources/application.properties` with your MySQL credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/jobportal
     spring.datasource.username=your-username
     spring.datasource.password=your-password
     ```

4. **Run Database Script**
   - Execute `src/main/resources/jobPortal.sql` in your MySQL client
   - This creates all tables and inserts initial data including the admin user

5. **Configure Email (Optional)**
   - Update email settings in `application.properties`:
     ```properties
     spring.mail.username=your-email@gmail.com
     spring.mail.password=your-app-password
     ```
   - Note: For Gmail, use an App Password, not your regular password

6. **Build the Application**
   ```bash
   mvn clean install
   ```

7. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   Or use your IDE's run configuration

8. **Access the Application**
   - Open your browser and navigate to: `http://localhost:8081`
   - Default Admin Credentials:
     - Email: `admin@jobportal.com`
     - Password: `admin123`
     - ⚠️ **Change this password in production!**

9. **Run Verification Update (Optional)**
   - Execute `verification-update.sql` to ensure verification system compatibility
   - This adds any missing verification fields and creates the admin user

---

## 🔐 User Verification System

Jobify implements a comprehensive identity verification system to ensure platform security and user trust.

### Verification Requirements
- **Government-issued ID**: Passport, National ID, or Driver's License required
- **Photo Matching**: Profile photo must match ID document
- **Name Verification**: Profile name must match ID document exactly
- **Business License**: Required for client accounts
- **Admin Review**: All documents reviewed by administrators

### Verification Process
1. User registers and completes profile
2. Uploads required verification documents
3. Admin reviews documents for authenticity
4. Account approved/verified or rejected
5. Email notification sent to user
6. Verified users gain full platform access

For detailed information, see [VERIFICATION_SYSTEM.md](VERIFICATION_SYSTEM.md)

---

## 👥 User Workflows

### Client Workflow

1. **Registration**: Sign up as a Client
2. **Profile Setup**: Complete profile and upload verification documents
3. **Admin Verification**: Wait for admin to verify account and documents
4. **Post Jobs**: Once verified, post job opportunities
5. **Review Applications**: Review freelancer applications
6. **Manage Applications**: Update application status, communicate via chat
7. **Hire Freelancers**: Mark applications as HIRED when ready

### Freelancer Workflow

1. **Registration**: Sign up as a Freelancer
2. **Profile Setup**: Complete profile, add skills, upload resume and verification documents
3. **Admin Verification**: Wait for admin to verify account
4. **Browse Jobs**: Search and filter available jobs
5. **Apply**: Submit applications with cover letters and proposed rates
6. **Track Applications**: Monitor application status
7. **Communicate**: Chat with clients about projects
8. **Get Hired**: Receive job offers and start working

### Admin Workflow

1. **Login**: Access admin dashboard
2. **Review Users**: Check pending user registrations
3. **Verify Documents**: Download and review verification documents
4. **Approve Users**: Verify and approve user accounts
5. **Review Jobs**: Check pending job postings
6. **Approve Jobs**: Approve jobs to make them visible
7. **Monitor Platform**: View reports and platform statistics

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/webapp/jobportal/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/       # MVC Controllers
│   │       ├── entity/           # JPA Entities
│   │       ├── enums/           # Enumerations
│   │       ├── repository/       # Data repositories
│   │       ├── services/        # Business logic
│   │       └── util/            # Utility classes
│   └── resources/
│       ├── templates/            # Thymeleaf templates
│       ├── static/               # Static resources
│       ├── application.properties
│       └── jobPortal.sql        # Database schema
└── test/                         # Test files
```

---

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

- **Server Port**: `server.port=8081`
- **Database**: MySQL connection settings
- **Email**: SMTP configuration for notifications
- **File Upload**: Maximum file size limits (10MB)

### Security Configuration

- Public URLs: Home, registration, static resources
- Protected URLs: Dashboard, admin panel, user-specific pages
- Role-based access: Admin routes require ADMIN authority

---

## 📧 Email Notifications

The system sends email notifications for:

- User account approval/rejection
- Job posting approval/rejection
- New application received
- Application status updates
- New chat messages

Configure SMTP settings in `application.properties` to enable email functionality.

---

## 🎨 UI/UX Features

- **Modern Design**: Clean, professional interface with Jobify branding
- **Responsive Layout**: Mobile-friendly design
- **Real-Time Updates**: Live notifications and chat
- **Intuitive Navigation**: Easy-to-use interface
- **Professional Styling**: Bootstrap-based responsive design

---

## 🔐 Security Best Practices

1. **Change Default Admin Password**: Immediately change the default admin password
2. **Use Strong Passwords**: Enforce strong password policies
3. **HTTPS in Production**: Always use HTTPS in production environments
4. **Regular Updates**: Keep dependencies updated
5. **Database Security**: Use secure database credentials
6. **File Upload Validation**: Validate all file uploads

---

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in `application.properties`
   - Ensure database `jobportal` exists

2. **Email Not Sending**
   - Verify SMTP settings
   - For Gmail, use App Password
   - Check firewall settings

3. **WebSocket Connection Failed**
   - Ensure port 8081 is not blocked
   - Check browser console for errors
   - Verify WebSocket dependencies in pom.xml

4. **File Upload Errors**
   - Check file size limits
   - Verify directory permissions
   - Ensure sufficient disk space

---

## 📝 API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /login` - Login page
- `GET /register` - Registration page
- `GET /global-search/` - Job search

### Authenticated Endpoints
- `GET /dashboard/` - User dashboard
- `GET /chat/` - Chat interface
- `GET /notifications/` - Notifications
- `POST /dashboard/addNew` - Post new job (Clients only)
- `POST /chat/send` - Send message

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - User management
- `GET /admin/jobs` - Job management
- `POST /admin/users/{id}/verify` - Verify user

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

## 📄 License

This project is open source and available for educational purposes.

---

## 📞 Support

For issues, questions, or contributions:
- Create an issue in the repository
- Contact: support@jobportal.com

---

## 🎯 Future Enhancements

- Payment integration
- Rating and review system
- Advanced analytics dashboard
- Mobile application
- AI-powered job matching
- Video interview integration
- Portfolio showcase for freelancers
- Milestone-based payments

---

## 👨‍💻 Development Team

Built with ❤️ using Spring Boot and modern web technologies.

---

**Version**: 1.0.0  
**Last Updated**: December 2024
