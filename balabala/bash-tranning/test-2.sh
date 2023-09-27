#!/bin/bash

FIEL_LIST=$(ls -R lexerParser/)
JAVA_SUFFIX=".*[.][j][a][v][a]"
KEYWORD1="public "
KEYWORD2="private "
KEYWORD3="List< "
REG_MATCH1=0
REG_MATCH2=0
REG_MATCH3=0

#define function
function readfile() {
    LINE=1

    while read CURREN_LINE; do
        #echo "Line$LINE>  $CURREN_LINE"
        if [ "$CURREN_LINE" ]; then
            REG_MATCH1=$(expr match "$CURREN_LINE" "$KEYWORD1")
            REG_MATCH2=$(expr match "$CURREN_LINE" "$KEYWORD2")
            REG_MATCH3=$(expr match "$CURREN_LINE" "$KEYWORD3")

            if [ $REG_MATCH1 -gt 0 ] || [ $REG_MATCH2 -gt 0 ] || [ $REG_MATCH3 -gt 0 ]; then
                echo "Line$LINE>  $CURREN_LINE"
            fi
        else
            continue
        fi

        ((LINE++))
    done <$1
}

for FILE in $FIEL_LIST; do
    # echo "FIEL_LIST:$FILE"
    REG_MATCH=$(expr match "$FILE" "$JAVA_SUFFIX")
    if [ $REG_MATCH -gt 0 ]; then
        #FILEPATH=$(pwd)$(find -name $FILE)
        #FILEPATH=${FILEPATH/"./"/"/"}
        FILEPATH=$(find -name $FILE)
        echo "~~current path: $FILEPATH"
        readfile $FILEPATH
    fi

done
