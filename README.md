## Design project ##

This project was built for course E033710 at Ghent University.

## Database ##

Create a local database clone

1. Install posgresql + pgAdmin3.
2. In pgAdmin3 connect to your local database.
 * Default: localhost:5432
3. Select the default database "postgres" and click the "SQL"-button in the action bar.
4. Go to File -> Open.. and open the file "createHelloworld.sql" and press run
5. Close the window and hit the refresh button untill you see the "helloworld" database.
6. Select the "helloworld" database and press the "SQL"-button.
7. Go to File -> Open.. and open the file "populateHelloworld.sql" and press run

This should completly set up the database localy

## Frontend ##

Setup frontend for local development

1. Install XAMPP
2. Run XAMPP as Administrator
3. In the modules box, next to Apache, click 'Config' and select 'httpd.conf'
4. Replace in the line DocumentRoot "/home/user/www" the directory to your liked one. Use forward slashes.
5. Do the same in the line \<Directory "/home/user/www">

The default DocumentRoot path will be different for windows [the above is for linux].
If after restarting the server you get errors, you may need to set your directory options as well. This is done in the <Directory> tag in httpd.conf. Make sure the final config looks like this:

    DocumentRoot "C:\alan"
    <Directory "C:\alan">
        Options Indexes FollowSymLinks Includes ExecCGI
        AllowOverride All
        Order allow,deny
        Allow from all
      	Require all granted
    </Directory>

When there are problems with Allow Control Allow Origin headers, install the following Chrome extension: https://chrome.google.com/webstore/detail/allow-control-allow-origi/nlfbmbojpeacfghkpbjhddihlkkiljbi?utm_source=chrome-app-launcher-info-dialog
