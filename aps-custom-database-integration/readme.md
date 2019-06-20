
### Data Source Cofiguration for various custom DB

You can establish connection from your process in Activiti with a relational database. To enable the connection, you must first register the data source for your tenant in the Identity Management app in Activiti.

Before defining a data model, you must establish a database connection and register the data source in your tenant.
In the Identity Management app, click Tenants > Data sources.
Click + (plus icon) and configure the following settings (see the activiti-app.properties file for more details):

## Oracle DB
![Oracle](oracle.png)

## MySQL DB
![MySQL](mysql.png)

## PostGresSQL DB
![Postgres](postgresdb.png)

### References
1. http://docs.alfresco.com/activiti/docs/user-guide/1.5.0/
2. http://docs.alfresco.com/activiti/docs/user-guide/1.5.0/#_connecting_your_data_model_to_a_relational_database