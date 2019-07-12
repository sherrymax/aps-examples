
### Data Source Cofiguration for various custom DB

You can establish connection from your process in Activiti with a relational database. To enable the connection, you must first register the data source for your tenant in the Identity Management app in Activiti.

Before defining a data model, you must establish a database connection and register the data source in your tenant.
In the Identity Management app, click Tenants > Data sources.
Click + (plus icon) and configure the following settings (see the activiti-app.properties file for more details):

### Data Source configuration of following DBs are given below
1. [Oracle](#oracle)
2. [MySQL](#mysql)
3. [PostGreSQL](#postgresql)

## <a name="oracle"></a>Oracle DB
![Oracle](oracle.png)

## <a name="mysql"></a>MySQL DB
![MySQL](mysql.png)

## <a name="postgresql"></a>PostGreSQL DB
![Postgres](postgres.png)

### References
1. http://docs.alfresco.com/activiti/docs/user-guide/1.5.0/
2. http://docs.alfresco.com/activiti/docs/user-guide/1.5.0/#_connecting_your_data_model_to_a_relational_database
3. https://docs.alfresco.com/process-services1.7/topics/databaseConfiguration.html