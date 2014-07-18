To run the program go to "dist" directory and run Amazon.exe file.

The two functions provided by the program : 

1. Find : 
   ====
   
   It finds the textbooks through the API, which are below the specified rank, have difference between New and Like New prices above the specified difference and 
   contain the keyword specified. 
   
   While selecting the output csv file, if an existing csv file with some ISBN's (found out during some previous run of the program) is selected, then 
   the program skips those ISBN's (if they occur in the new search) and operates only on new ISBN's.
   
2. Upload ISBN: 
   ===========
   
   It finds the details of the ISBN's from the selected file which contains a list of the ISBN's seperated by new line.
   
   While selecting the output csv file, if an existing csv file with some ISBN's (found out during some previous run of the program) is selected, then 
   the program skips those ISBN's (if they occur in the new search) and operates only on new ISBN's.

Nodes.txt file contains the node ID for the Textbooks browse node. If additional nodes are required then they must be specified in a new line along with a "-" 
seperating the node name and it's corresponding ID.

Amazon.properties file contains the credentials for using the API. It contains 3 fields : 
	1. AWS_ACCESS_KEY_ID 
	2. AWS_SECRET_KEY 
	3. ACCESS_TAG 
which are to be replaced by the credentials obtained. 


