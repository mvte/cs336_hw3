# CS336 HW3 - Jan Marzan
April 7, 2022\
Section 07

## Project Information
This project was built using Gradle. MySQL dependencies and other dependencies can be found in the build.gradle file. Furthermore, 
## Random Data Generation
### Students
To randomly generate the Students table, I used the public Randommer.io API to generate 104 random names and load them into the database.
To create a unique ID for each student, I simply used the hashcode of the students name, and then truncated (or extended) the number to 9 digits.

### 
