# 📰 NewsApp

An Android news application built with Kotlin, leveraging the NewsAPI to fetch and display the latest news articles across various categories. The app follows modern Android development practices, ensuring a responsive and user-friendly experience.

---

##  User Roles

The app supports two main roles, each with distinct functionalities:

### Reporter
- Create and submit news articles.
- View submitted articles and track their review status.

### Editor
- Review and manage reported news.
- Accept or reject articles submitted by reporters.

---

## Features

- **Real-time News Fetching**: Stay updated with the latest news articles fetched directly from NewsAPI.
- **Category-wise Browsing**: Explore news based on categories like Business, Entertainment, Health, Science, Sports, and Technology.
- **Responsive Design**: Optimized for various screen sizes, ensuring a seamless experience on all devices.
- **Modern Android Architecture**: Implements MVVM architecture, utilizing components like ViewModel, LiveData, and Retrofit for efficient data handling.

---

## Interface Previews

### Loading Page
![Home Page](https://github.com/samiho03/NewsApp/blob/main/images/loading.jpg)

### Login & Register Pages
![Login-Register](https://github.com/samiho03/NewsApp/blob/main/images/Login-RegisterPages.png)

### My Profile
![My profile](https://github.com/samiho03/NewsApp/blob/main/images/MyProfile.jpg)

### Create News (For Reporters)
Reporters can submit their articles using this page.
![Create News](https://github.com/samiho03/NewsApp/blob/main/images/CreateNewsPage-Reporter.jpg)

### My Submissions (For Reporters)
Displays all articles submitted by the reporter along with their statuses.
![Submissions](https://github.com/samiho03/NewsApp/blob/main/images/MySubmissions-Reporter.jpg)

### Reported News (For Editors)
Editors can view reported or flagged articles here.
![Reported News](https://github.com/samiho03/NewsApp/blob/main/images/ReportedNews-Editor.jpg)

### Review News (For Editors)
Editors can accept or reject news submitted by reporters.
![Review](https://github.com/samiho03/NewsApp/blob/main/images/ReviewNews-Editor.jpg)

---

## Technologies Used

- **Kotlin**: Primary programming language for building the app.

- **Retrofit**: Handles network requests and communicates with the NewsAPI.

- **Glide**: Efficient image loading and caching in the app's UI.

- **Firebase**: Used for backend support and data storage.

- **MVVM Architecture**: Ensures a clean separation of concerns, improving testability and scalability.

- **LiveData & ViewModel**: Enables reactive UI updates and lifecycle-aware data handling.

---
