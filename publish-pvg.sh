#!/bin/sh

VERSION=`xmllint --xpath "//*[local-name()='project'='project']/*[local-name()='version']/text()" pom.xml`
echo Using project artifact version $VERSION

PROJECT_DIR=bonewars-html-$VERSION
echo Using project directory $PROJECT_DIR

DEST=/home/pvg/www/games/2014/bonewars
echo Publishing to $DEST

cd html/target
rsync -avz --progress --exclude 'META-INF' --exclude 'WEB-INF' --delete $PROJECT_DIR/. $DEST
