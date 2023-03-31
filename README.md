# CS336 HW3 - Jan Marzan
April 7, 2022\
Section 07

## Project Information
This project was built using Gradle. MySQL dependencies and other dependencies can be found in the build.gradle file. Furthermore, all passwords and API keys are hidden environment variables.

## Random Data Generation
All tables, except for departments, were loaded into the database by iterating with prepared statements.

### Departments
There aren't that many rows here, so I just manually inserted each value using terminal.

### Students
To randomly generate the Students table, I used the public Randommer.io API to generate 104 random names (amount will be explained later) and load them into the database.
To create a unique ID for each student, I simply used the hashcode of the student's name, and then truncated (or extended) the number to 9 digits.
If the modified hashcode happens to already exist in the database, we will increment by one until it doesn't exist. 

### Classes
The classes were taken from the Rutgers Schedule of Classes API because I don't want to come up with 50 or so class names. 
For each department that we have defined in this project, I added the courses within each department's corresponding subject that happened to have "expanded titles" and are worth more than 3 credits.
This turns out to be over 80 classes, which is good enough.

### Majors
The number of majors a student would have would be determined by a normal distribution with mean 1.5 and standard deviation 0.6. 
If the number happened to be negative, I would take the absolute value.
Once I determined the number of majors a student would have, I would pick, without replacement, that amount of majors from the department list with each department  equally likely. 

### Minors
The number of minors a student would have was also determined by a normal distribution, this time with mean 0 and standard deviation 0.8.
I used the same pickNoReplacement() method as the majors to generate the minors that one took.

### Is Taking



