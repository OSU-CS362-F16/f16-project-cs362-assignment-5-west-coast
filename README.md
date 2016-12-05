Corinna Huffaker
Jeromie Clark
Damian Mecham
Bolan Peng

Github link:  https://github.com/OSU-CS362-F16/f16-project-cs362-assignment-5-west-coast
Link to base code: http://web.engr.oregonstate.edu/~baldwdav/BuggyURLValidator.zip 


Run unit tests first to compile test classes:
   mvn compile test

Run cobertura report:
   mvn cobertura:clean cobertura:cobertura
Cobertura report is saved to the ./target/site/cobertura directory.


Run FindBugs report (also runs Maven dependency reports):
   mvn site
Site report is saved to the ./target/site directory.


Run PIT mutation coverage report:
  mvn org.pitest:pitest-maven:mutationCoverage
PIT mutation coverage report is saved to the ./target/pit-reports/ directory
in a sub-directory with time/date stamp corresponding to the report creation date.

Run 'deploy' to copy site reports to ~/public_html/cs362ProjectReport/project-reports.html
Cobertura reports are copied to ~/public_html/cs362ProjectReport/cobertura/index.html
The latest PIT report is copied to ~/public_html/cs362ProjectReport/pit-reports/index.html

Unit test results for the input files in TestData/input are saved 
in ./target/results as '.csv' files.
