# Financial Organization System

## Overview
A software system designed to organize financial data pertaining to a club/organization.
This system acts as a middle man to a local SQL database (in my case, SQLite); as such, there
are specific commands available to insert and retrieve information:
- **Add a fund to the database**: add_fund  \<fund\>  \<amount\>  \<expiration\>
- **Add a purchase to the database**: add_purchase  \<fund\>  \<amount\>  \<person\>  \<date\>  \<description\>
- **Lookup info about a single fund**: lookup_fund  \<fund\>
- **Lookup info about a single person**: lookup_person  \<person\>
- **Lookup info about all funds**: overview
- **Lookup info about all purchases**: all_purchases
- **Wipe the database**: clear_data
- **Exit the system**: exit

## Invocation
To run this system, I first group the files into a jar file by the name of FOS.jar, then run with the
following command:  
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;java -jar FOS.jar \[script file\] \[database file\]  
  
A typical script file might look something like this:  
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;add_fund fund1 $5,000.00 12/31/2021  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;add_purchase fund1 $1,000.00 Aidan 12/24/2020 Misc  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exit  

## Libraries
Several libraries were used in this build. Specifically, two of them were crucial in connecting
to a SQL database: the first was sqlite-jdbc (link: https://github.com/xerial/sqlite-jdbc) which
allowed for accessing and creating SQL databases in java, and the second was the mySQL java connector
(link: https://dev.mysql.com/doc/connector-j/8.0/en/) which provided connectivity with mySQL. The jar
files for both of these resources are included in this repository.

## Notes:
This system is a work in progress; as such, there are numerous things I wish to change/fix in the
future. Chiefly, the system is designed to work around VT Student Engineer's Council type funds
(at least in the way the system formats them). I hope to change this to be more flexible with other
fund types. Additionally, the system only runs with a supplied script file which is useful for 
multiple commands, however having a line-by-line input could be beneficial.  
  
Other features planned include:
- Informing the user when a fund is almost expired / has expired
- Informing the user when a fund is empty 
- Not allowing the user to add a purchase to an empty fund
