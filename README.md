### **Objective**

This project intends to create an environment to integrate different investment analisys Technics. Such technics may come from different fields such as Statistics, Accounting, Macroeconomics, and so on... 

Autonomous agents are used to encapsulate such technics and they collaborate among themselves to produce deeper analisys. 

This is my Graduation Final Project for the Bachelor of Computer Sciences Degree, please see the full documentation here:

#### [Agent Based Investment Analysis - Brazilian Portuguese](doc/TCII.pdf)

### **Overview**

The main component of the system is the Agent Container which controls the agents, and the access to the system. The agents share a common data repository called the Blackboard. This has been currently been developed as a PostgreSQL object-relational mapping. 

The current version has 2 types of Agents:Web Mining and Analisys Agents
* **Web Mining**: Those agents retrieve financial data from the Internet and store them in the Blackboard. Currently only brazilian financial market data is retrieved. **Both from CVM (Brazil's SEC) and BOVESPA (Sao Paulo Stock Exchange)**.
* **Analysis Agent**: Those agents encapsulate different analisys techincs. Currently there is the following agents:
* **Technical Analisys** (Expert System), 
* **Fundamental Analisys** (Expert System),
* **Portfolio Management** (Expert System),
* **Neural Network**

### **System Installation on Windows**

Please follow the steps below in order to install the system:

**1. Install Cygwin and Postgresql**

Is is necessary to install cygwin, which is a linux translation layer for linux on windows. It can be obtained at: http://sources.redhat.com/. Is it necessary to  install postgres from cygwin package.
After the install, connect to postgresql and create a database called ABIA.

**2. Install JDK**

Is is necessary to install JDK 1.4 or higher from http://java.sun.com/

**3. Install Apache Tomcat 4.21**

Is it necessary to install Apache Tomcat 4.21, which can be downloaded @ http://jakarta.apache.org/

**4. Software Instalation**

To install the software, please download the source package from  http://abia.sourceforge.net/ and copy the test folder to some directory on your machine, there is no need for any other software installation.

**5. Execution**

To execute,just open a windows command prompt (cmd.exe) and go to the test folder on the folder where the source package was uncompressed and execute the Batch file: executarABIA.bat

**6 Simulation**

To simulate, just execute the simulator on the bin folder, just make sure to configura all xml files on the conf file.

### Project Folder Organization
