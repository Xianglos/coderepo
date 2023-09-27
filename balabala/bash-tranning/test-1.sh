#!/bin/bash

FIEL_LIST=$(ls -R lexerParser/)
REG_MATCH=0
EXECRST=""
#JAVA_SUFFIX=".java"
STRING="String "

for FILE in $FIEL_LIST; do
    # echo "FIEL_LIST:$FILE"
    REG_MATCH=$(expr match "$FILE" "*[.][j][a][v][a]")
    echo "FILE: $FILE"
    if [ "$?" -eq "0" ]; then
        FILEPATH=$(find -name $FILE)
        echo "current path: $FILEPATH"
        LINE=1
        while read CURREN_LINE; do

            REG_MATCH=$(expr match "$CURREN_LINE" "$STRING")
            if [ "$?" -eq "0" ]; then
                echo "Line$LINE>  $CURREN_LINE"
            fi

            ((LINE++))
        done <$FILEPATH
    fi

done
