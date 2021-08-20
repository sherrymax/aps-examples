#### The project contains all the components required to build custom reports using Kibana and ElasticSearch

### Use-Case / Requirement
Custom dashboards in Kibana should be built using APS, Kibana and ElasticSearch :

### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)
* Kibana
* ElasticSearch

## Configuration Steps
### Launch an EC2 Instance from an AMI Image
Orca users has to use following AMI Image to lanuch an instance
```
AMI Name : jwhyte_orca_3.2.2
AMI Id : ami-0617702d4f0917a5a
```

### Login to Okta and Start containers
Orca users has to use following AMI Image to lanuch an instance
```
./orca.py login okta
./orca.py start
```

### Prepare APS Container
1. Orca users should upload the latest APS license.
```
Login as admin@app.activiti.com/Alfresco01 to upload the license.
```
2. Download and backup the APS Process Apps.
[A few examples are available here](assets/APS-apps)


### Rebuild ACS Container (Optional Step)
Orca users who see the Orange color community login page has to destroy db containers using the following command
```
./orca.py destroy db -f

P.S : 
* All the ACS, APS and Analytics data will be erased upon destroying the db container.
* Hence the process container also has to be destroyed and rebuilt. 
    ./orca.py destroy process -f
    ./orca.py start process
* Import the already backed up Process Apps to the newly built process contianer.
```

### Prepare ElasticSearch Container
Orca users can start the ElaticSearch container by running the following command.
```
./orca.py start elasticsearch
```

### Prepare Kibana Container
Orca users can start the Kibana container by running the following command.
```
./orca.py start kibana
```

### Prepare Analytics Container
1. Orca users can start the Analytics container by running the following command.
```
./orca.py start analytics
```

2. Copy the [analytics.jar](assets/activiti-analytics-spring-boot-1.0.0-SNAPSHOT.jar) file into `orca/data` folder.
   Full credits and thanks to [Ciju Jospeh](https://github.com/cijujoseph) for building this spring-boot application. 
   More details are available at https://github.com/cijujoseph/activiti-analytics-spring-boot
```
scp -i my-key.pem activiti-analytics-spring-boot-1.0.0-SNAPSHOT.jar ec2-user@ec2-1-2-3-4.compute-1.amazonaws.com:/home/ec2-user/orca/data
```

3. Note the id of Analytics container
```
Run ./orca.py status and get the container-id
e.g: orca_analytics_1_99108e53455e
```

4. Copy the [analytics.jar](assets/activiti-analytics-spring-boot-1.0.0-SNAPSHOT.jar) file from `orca/data` into `analytics` container.



```
docker cp data/activiti-analytics-spring-boot-1.0.0-SNAPSHOT.jar orca_analytics_1_99108e53455e:/opt/analytics/activiti-analytics-spring-boot-1.0.0-SNAPSHOT.jar
```
5. Restart `analytics` container
```
./orca.py restart analytics
```
### Things to remember
```
Please wait for sometime for the bpmeventlog index to be created.
If it takes more than 15 minutes, restart db container, to restart the indexing.
```

### Prepare Kibana Report
1. Go to <hostname>:5601 and land into Kibana Homepage.
2. Select `Management > Index Patterns`
3. Create the index pattern of name `bpmanalyticseventlog-*`
4. Select `Management > Saved Objects`
5. Select `Import` and import the following JSON
   1. [task-dashboard-kibana.json](assets/task-dashboard-kibana.json)
   2. [task-search-kibana.json](assets/task-search-kibana.json)
6. Select `Dashboard` and the `Task Dashboard`

```
P.S : Custom visualisations and dashboards can be created to display business data.
```

### Run the DEMO


### References
1. https://github.com/cijujoseph/activiti-analytics-spring-boot
2. https://www.tutorialspoint.com/elasticsearch/elasticsearch_query_dsl.htm
3. https://www.bmc.com/blogs/elasticsearch-commands/
