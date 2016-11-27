Corinna Huffaker
Jeromie Clark
Damian Mecham
Bolan Peng


Run unit tests:
   mvn compile test

Run cobertura report:
   mvn cobertura:clean cobertura:cobertura

Run FindBugs report:
   mvn site

Run 'deploy' to copy site reports to public_html/cs362ProjectReport/project-reports.html
Cobertura reports are at public_html/cs362ProjectReport/cobertura/index.html
 
Run PIT mutation coverage report:
  mvn org.pitest:pitest-maven:mutationCoverage

Unit test results for the input files in TestData/input are saved in target/results

