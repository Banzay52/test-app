# Play-CMS Demo #

This is a Play framework demo application built with [insign](http://www.insign.ch)'s **play-cms** - a java-based Play
framework CMS.

This project creates some demo content and uses a h2 in-memory db for storage. It is intended to be used as
quick-and-easy starting point for your own play-cms project. We've also added a docker-based devops setup for a CI
(continuous integration) process.

Check out the [play-cms](http://www.play-cms.com) website for more information and documentation or visit the
[play-cms community](https://plus.google.com/u/0/communities/100224277865065215900/)

## Quick start

Either do a local installation using H2 by default, or run it with Docker which uses a MySQL database by default.

### Local installation ###

Clone the project to your computer, navigate to the new folder and run

    sbt run

By default, the application will be running on [http://localhost:9000/]() and use a H2 in-memory db for persistence.
After starting up the application, you need to execute the bootstrap code by calling [http://localhost:9000/reset](),
otherwise you will get an error.

Be aware that you won't be able to use the file manager for adding images/files to the WYSIWYG editor. For that,
you'd have to use the dockerized setup (instructions below).

### Backend Access ###

Default admin page url: [http://localhost:9000/admin]()

#### Credentials ####

- Login: admin@insign.ch
- Password: temp123

## Development environment and Continuous Integration ##

The project contains both a development environment and continuous integration setup (based on docker) for quick 
starting new projects.

### Use the development environment ###

- Make sure you have `docker` and `sbt` installed
- SBT options can be specified in `./.sbtopts` file. It must be noted that a line feed is required at the end of
the file because otherwise the last option will be ignored by sbt
- To enable based on docker environment execute the following command in your project root folder:

    cp .sbtopts-example .sbtopts

To quick start the project with this setup, run

    sbt run

The application will be running on [http://localhost:9000/]().
The environment features these dockerized applications:

- MySQL on port 3306, database: db_play-cms-demo, user: play-cms-demo, password: s3cr3t (can be changed in `build.sbt`)
- Mailhog SMTP server and mail viewer under [http://localhost:8025/]()
- Filemanager under [http://localhost:8035/]()

As with the setup not using docker, you will have to call the `/reset` route after starting the application for the
first time.

Sometimes (depending on your OS' `umask` settings) you will need add permissions in ./data folder (where all docker
volumes are mapped)

### Developing the play modules ##

If you want to develop the play-cms modules, uncomment next line in `./.sbtopts` file:

    -Dplaycms.version=local

Then execute the following commands in your project root folder:

    cd ..
    git clone ssh://git@sshgit.insign.rocks:10022/open-source/play-cms/play-cms.git

### Using debugger ##

If you want to develop with using debugger, uncomment next line in `./.sbtopts` file:

    -J-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9999

or instead you can use command when run app:

    sbt -jvm-debug 9999 run

Then configure debugger in your ide on port 9999 (you can replace 9999 with any suitable port)

## Developing the demo project itself ##

### Publishing modules to bintray ###

Modules can be published to bintray with

    sbt publish

Credentials will be asked on first time. 

Note: bintray doesn't support snapshots.

The current version of a module can be easily removed from bintray with

    sbt bintrayUnpublish

### Publishing docker image to bintray registry ###

Next command will upload a docker image of play-cms-demo to insign's bintray registry:

    sbt docker:publish
