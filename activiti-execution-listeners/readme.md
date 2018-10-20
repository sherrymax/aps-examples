#### The project contains all the components required to create an ACS folder from APS via REST calls

### Use-Case / Requirement
Build a process to create a listener that listens to a task.


### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)


## Configuration Steps

### Activiti Setup and Process Deployment
1. Import the ![Task-Listener.zip](Task-Listener.zip) app available in this project into Activiti.
2. Process Flow.  ![Process-Flow](Process-Flow.png)
3. Task Listener Configuration in Process. ![Request-Mapping](Request-Mapping.png)
4. Task Configuration.   ![Human-Task-Configuration](Human-Task-Configuration.png)
5. Task Listener Configuration in Task. ![Request-Mapping-2](Request-Mapping-2.png)
6. Code Snippet. ![Code-Snippet](Java-Code.png)
7. Source Code. ![Source-Code](activiti-extension-task-listener-java-code.zip)
8. The JAR File. ![Task-Listener.jar](activiti-extension-task-listener-jar-1.0-SNAPSHOT.jar)
9. Copy the jar file to tomcat-lib location. [eg: /usr/local/tomcat/webapps/activiti-app/WEB-INF/lib/]
10. Publish/Deploy the APS App.


### Run the DEMO

### References
1. https://www.activiti.org/userguide/#taskListener
2. https://www.programcreek.com/java-api-examples/?api=org.activiti.engine.delegate.TaskListener
3. https://community.alfresco.com/thread/175168-proper-way-to-implement-a-custom-a-tasklistener-in-activiti
