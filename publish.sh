#!/bin/sh

VERSION=`xmllint --xpath "//*[local-name()='project'='project']/*[local-name()='version']/text()" pom.xml`
echo Using project artifact version $VERSION

PROJECT_DIR=bonewars-html-$VERSION
echo Using project directory $PROJECT_DIR

cd html/target
rsync -az --progress --delete -e "ssh -l bonewars" $PROJECT_DIR bonewars@csweb.bsu.edu:/home/bonewars/www

