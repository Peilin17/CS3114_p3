/**
 * Student class that represents the student info for a particular person
 * 
 * @author Naod Haregot (nharegot)
 * @author Runnan Zhou (rnzhou)
 * @version 2019.02.09
 *
 */
public class Student implements Comparable<Student> {

    // all the variables that required
    private String firstName;
    private String lastName;
    private String studentID;
    private long pid;
    private String midName;
    private int score;
    private String grade;
    private int section = 0; // no register = 0

    /**
     * Creates Student object
     * 
     * @param fname first name
     * @param lname last name
     */
    public Student(String fname, String lname) {
        firstName = fname;
        lastName = lname;
        score = 0;
        studentID = null;
        grade = null;
    }

    /**
     * set pid
     * 
     * @param a pid number
     */
    public void setPid(long a) {
        pid = a;
    }

    /**
     * Gets Student's first name
     * 
     * @return returns Student's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets Student's mid name
     * 
     * @return returns Student's first name
     */
    public String getMidName() {
        return midName;
    }

    /**
     * sets Student's mid name
     * 
     * @param name Student's middle name
     */
    public void setMidName(String name) {
        midName = name;
    }

    /**
     * sets Student's grade
     * 
     * @param grade set student's grade
     */
    public void setGrade(String grade) {
        if (grade.length() < 2) {
            this.grade = grade + " ";
        }
        else {
            this.grade = grade;
        }
    }

    /**
     * gets Student's grade
     * 
     * @return grade student's grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Gets Student's last name
     * 
     * @return returns Student's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets Student's score
     * 
     * @return returns Student's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets Student's score
     * 
     * @param num score that will be set for student
     */
    public void setScore(int num) {
        score = num;
    }

    /**
     * Gets Student's score
     * 
     * @return returns Student's score
     */
    public int getSection() {
        return section;
    }

    /**
     * Sets Student's section
     * 
     * @param num score that will be set for student
     */
    public void setSection(int num) {
        section = num;
    }

    /**
     * get the student's id
     * 
     * @return studentID return the student ID
     */
    public String getStudentID() {
        return studentID;
    }

    /**
     * get the student's
     * 
     * @return pid return the student's pid
     */
    public Long getPid() {
        return pid;
    }

    /**
     * 
     * @param num student ID that will be assigned to student
     */
    public void setStudentID(String num) {
        studentID = num;
    }

    /**
     * CompareTo method from comparable class
     * 
     * @param o object being compared
     * @return returns number based on comparison
     */
    @Override
    public int compareTo(Student o) {
        if (lastNameCompare(o) != 0) {
            return lastNameCompare(o);
        }
        else if (firstNameCompare(o) != 0) {
            return firstNameCompare(o);
        }
        return 0;
    }

    /**
     * Compares last names
     * 
     * @param obj object to be compared to
     * @return returns number based on comparison
     */
    private int lastNameCompare(Student obj) {
        Student other = obj;
        String lastNameLowerCase = this.lastName.toLowerCase();
        String otherLastNameLowerCase = other.lastName.toLowerCase();

        int i = 0;
        while (i < lastNameLowerCase.length() 
                    && i < otherLastNameLowerCase.length()) {
            if (lastNameLowerCase.charAt(i) 
                    < otherLastNameLowerCase.charAt(i)) {
                return -1;
            }
            else if (lastNameLowerCase.charAt(i) >
                    otherLastNameLowerCase.charAt(i)) {
                return 1;
            }
            i++;
        }
        if (lastNameLowerCase.length() <
                otherLastNameLowerCase.length()) {
            return -1;
        }
        else if (lastNameLowerCase.length() > 
                otherLastNameLowerCase.length()) {
            return 1;
        }
        return 0;
    }

    /**
     * Compares first names
     * 
     * @param obj object to be compared to
     * @return returns number based on comparison
     */
    private int firstNameCompare(Student obj) {
        Student other = obj;
        String firstNameLowerCase = this.firstName.toLowerCase();
        String otherFirstNameLowerCase = other.firstName.toLowerCase();

        int i = 0;
        while (i < firstNameLowerCase.length() 
                        && i < otherFirstNameLowerCase.length()) {
            if (firstNameLowerCase.charAt(i) <
                    otherFirstNameLowerCase.charAt(i)) {
                return -1;
            }
            else if (firstNameLowerCase.charAt(i) >
                    otherFirstNameLowerCase.charAt(i)) {
                return 1;
            }
            i++;
        }
        if (firstNameLowerCase.length() <
                    otherFirstNameLowerCase.length()) {
            return -1;
        }
        else if (firstNameLowerCase.length() >
                     otherFirstNameLowerCase.length()) {
            return 1;
            
        }
        return 0;
    }

    /**
     * Outputs Student's properties as String
     * 
     * @return returns String output of Student class including first name, last
     *         name, and score
     */
    public String toString() {
        return studentID + ", " + firstName + " " + lastName 
                + ", " + "score = " + Integer.toString(score) + "\n";
    }
}
