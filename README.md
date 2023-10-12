# Student-Tracker-App
This Android mobile application is designed to help students keep track of their academic progress by organizing their terms, courses, and assessments. The app allows users to enter term information, add courses with associated details, including instructors' information, and manage assessments. It provides features such as setting alerts, sharing notes, and populating assessment details. The application uses a SQLite database for data storage.

------------------------------ Project Requirements: ------------------------------

A. Application Functionality

-Users can enter term titles, start dates, and end dates for each term.

-The application allows users to add multiple terms as needed.

-Validation ensures that a term cannot be deleted if courses are assigned to it.

-For each term, users can:

---Add as many courses as needed.

---View a list of courses associated with the term.

---Access a detailed view of the term, including its title, start date, and end date.

-Each course includes:

---Course title

---Start date

---End date

---Status (in progress, completed, dropped, plan to take)

---Course instructor's names, phone numbers, and email addresses

---Users can add multiple assessments to each course and include optional notes.

---Users can enter, edit, and delete course information.

---Users can view optional notes.

---Users can access a detailed view of the course, including the end date.

---Alerts for the start and end date can be set to trigger when the application is not running.

---Notes can be shared via email or SMS, with automatic population of the notes.

-Assessments functionality:

--- Users can add performance and objective assessments for each course, including titles and end dates.

--- Users can enter, edit, and delete assessment information.

--- Alerts for start and end dates can be set to trigger when the application is not running.


B. Screen Layouts

-Home screen

-List of terms

-List of courses

-List of assessments

-Detailed course view

-Detailed term view

-Detailed assessment view


C. Implementation of Scheduler

-ArrayList

-Intent

-Navigation between multiple screens using activities

-Three activities

-Events, e.g., click events

-Portrait and landscape layout support

-Interactive capability to accept and act upon user input

-Database for data storage

-Application title and icon

-Notifications or alerts

-Use of declarative and programmatic methods for creating the user interface

-Interface Requirements:

---Vertical scrolling

---Action bar

---Two layouts

Input controls

Buttons
