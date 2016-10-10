# Open Diff

Open Diff is a tool that allows you to paste a unified diff (eg. from git, mercurial or the diff command line utility) and get a nicely rendered view of it with code highlighting, and a URL for you to share.

In addition to simply being a place to store your diffs, people can view and comment on your diff freely, giving you a place to have open discussions about ways of doing things with strangers.

To get started you can submit a diff via curl as demonstrated below:

## Use from the command line to get a URL:

* git: `curl -o /dev/null -s --data-urlencode "diff=$(git diff)" -w "%{redirect_url}\n" localhost:9000/d`
* mercurial: `curl -o /dev/null -s --data-urlencode "diff=$(hg diff)" -w "%{redirect_url}\n" localhost:9000/d`
* diff: `curl -o /dev/null -s --data-urlencode "diff=$(diff -u $FILE1 $FILE2)" -w "%{redirect_url}\n" localhost:9000/d`

If the above isn't giving you back a URL then it's most likely because your diff is empty or you've hit one of the abuse limits outlined below. Try sending the diff through the below form instead:

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
