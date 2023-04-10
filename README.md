# CS336 HW3 - Jan Marzan
April 7, 2022\
Section 07

## Project Information/Usage
This project was built using Gradle. MySQL dependencies and other dependencies can be found in the build.gradle file. To run the project, 
navigate to the root directory and use the following command: \
`./gradlew --console plain run --args="[db_url] [db_username] [db_password]"` \
Change the url, username, and password as needed. Note that you only need to provide the hostname and port, since `jdbc:mysql://` will be prepended.

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
The number of majors a student would have would be determined by a normal distribution with mean 1 and standard deviation 0.6. 
If the number happened to be negative, I would take the absolute value.
Once I determined the number of majors a student would have, I would pick, without replacement, that amount of majors from the department list with each department  equally likely. 

### Minors
The number of minors a student would have was also determined by a normal distribution, this time with mean 0 and standard deviation 1.
I used the same `pickNoReplacement()` method as the majors to generate the minors that one took.

### Is Taking
It is assumed that every student is taking courses if they're enrolled in the university. Each student is assigned 4-5 classes that they have not already taken.

### Has Taken
To evenly distribute among the different classes, I iteratively trimmed my list of students and associated classes until there were no more students left. I iterated over the list 8 times, removing 13 students from the list and then assigning the remaining students 4-5 classes that they have not yet taken. This is why 104 students were required, as it was divisible by 8 and would make my life easier.
