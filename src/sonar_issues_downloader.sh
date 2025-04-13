#!/bin/bash

pagesize=400

url='https://sonarcube.myorg.com/api/issues/search?componentKeys=my-proj-Builds%3Amaster&s=FILE_LINE&resolved=false&rules=java%3AS115%2Cjava%3AS1172%2Cjava%3AS1444%2Cjava%3AS1481%2Cjava%3AS1854%2Cjava%3AS2129&ps='${pagesize}'&facets=severities%2Ctypes&additionalFields=_all&timeZone=Asia%2FCalcutta'

total=$(curl -k -ss $url | jq -r '.total')
echo 'Total Records = '$total

totalpages=$((total / pagesize))
remainder=$((total % pagesize))

[[ $remainder -gt 0 ]] && totalpages=$((totalpages+1)) || totalpages=$((totalpages))
echo 'Total Pages: '$totalpages

reportfilename=sonar_proj.csv

for i in $(seq 1 $totalpages)
do
    echo "Page : $i"

if [[ $i == 26 ]] # Because mostly the sonar portal supports download of only first 10K issues, we are breaking this loop, if page# reaches 26 (greater than 10K)
then break
fi
if [[ $i == 1 ]]
then
    curl -k -ss $url+'&p='${i}'' | jq -r '.issues |[ map(.) | .[] | {"severity": .severity, "component": .component, "line": .line, "textRange/startLine": .textRange.startLine, "textRange/endLine": .textRange.endLine, "textRangestartOffset": .textRange.startOffset, "textRange/endOffset": .textRange.endOffset, "status": .status, "message": .message, "effort": .effort, "debt": .debt, "author": .author, "creationDate": .creationDate, "updateDate": .updateDate, "type": .type, "scope": .scope, "quickFixAvailable": .quickFixAvailable}]|. |(.[0] | to_entries | map(.key)), (.[] | [.[]]) | @csv' >> ./$reportfilename
else
    curl -k -ss $url+'&p='${i}'' | jq -r '.issues |[ map(.) | .[] | {"severity": .severity, "component": .component, "line": .line, "textRange/startLine": .textRange.startLine, "textRange/endLine": .textRange.endLine, "textRangestartOffset": .textRange.startOffset, "textRange/endOffset": .textRange.endOffset, "status": .status, "message": .message, "effort": .effort, "debt": .debt, "author": .author, "creationDate": .creationDate, "updateDate": .updateDate, "type": .type, "scope": .scope, "quickFixAvailable": .quickFixAvailable}]|. |(.[] | to_entries | map(.value)) | @csv' >> ./$reportfilename
fi

done