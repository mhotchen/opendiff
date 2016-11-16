# Open Diff

See the [opendiff](http://diff.mhn.me) website for information on what the project is.

## Running

To run locally you will need scala and postgres. This program uses the standard lightbend tools and SBT so configure it as such a project in your preferred IDE.

The following environment variables will need configured with valid parameters to connect to the database. The database and user will need to already exist with full grants:

* `JDBC_DATABASE_URL=jdbc:postgresql://localhost/opendiff`
* `JDBC_DATABASE_USERNAME=postgres`
* `JDBC_DATABASE_PASSWORD=`

Or you can run the application using `java`, `scala` or the generated binary setting the following parameters with -D:

* `-Dslick.dbs.default.db.url=jdbc:postgresql://localhost/opendiff`
* `-Dslick.dbs.default.db.user=opendiff`
* `-Dslick.dbs.default.db.password=opendiff`

If you're going to be running it in production mode then you'll need to configure an application secret:

* `APPLICATION_SECRET=supersecret`

Or:

* `-Dplay.crypto.secret=supersecret`

## Contributing

Feel free to contribute in any way you like. Feel free to contact me if you have an idea, open an issue, open a random PR, whatever you want really. I'm a one man band on this so there's no strict process.
