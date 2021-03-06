Releasing guide
===============

This guide will help you to perform the full release process for Logisland framework.

1.


git hf release start v0.15.0


Build the code and run the tests
--------------------------------


The following commands must be run from the top-level directory.

.. code-block:: sh

    mvn clean install

If you wish to skip the unit tests you can do this by adding `-DskipTests` to the command line.


Release to maven repositories
-----------------------------
to release artifacts (if you're allowed to), follow this guide `release to OSS Sonatype with maven <http://central.sonatype.org/pages/apache-maven.html>`_

.. code-block:: sh

    # update the version (you should run a dry run first)
    ./update-version.sh -o 0.14.0 -n 0.15.0 -d
    ./update-version.sh -o 0.14.0 -n 0.15.0
    mvn license:format
    mvn clean install
    mvn -DperformRelease=true clean deploy -Phdp2.5
    mvn versions:commit


follow the staging procedure in `oss.sonatype.org <https://oss.sonatype.org/#stagingRepositories>`_ or read `Sonatype book <http://books.sonatype.com/nexus-book/reference/staging-deployment.html#staging-maven>`_

go to `oss.sonatype.org <https://oss.sonatype.org/#stagingRepositories>`_ to release manually the artifact



Publish release assets to github
--------------------------------

please refer to `https://developer.github.com/v3/repos/releases <https://developer.github.com/v3/repos/releases>`_

curl -XPOST https://uploads.github.com/repos/Hurence/logisland/releases/v0.15.0/assets?name=logisland-0.15.0-bin-hdp2.5.tar.gz -v  --data-binary  @logisland-assembly/target/logisland-0.10.3-bin-hdp2.5.tar.gz --user oalam -H 'Content-Type: application/gzip'



Publish Docker image
--------------------
Building the image

.. code-block:: sh

    # build logisland
    mvn clean install -DskipTests -Pdocker -Dhdp2.5

    # verify image build
    docker images


then login and push the latest image

.. code-block:: sh

    docker login
    docker push hurence/logisland


Publish artifact to github
--------------------------

Tag the release + upload latest tgz
