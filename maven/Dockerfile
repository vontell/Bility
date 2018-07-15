  
FROM mattgruter/artifactory

RUN mkdir code/
COPY ./BilityPrivate ./code
COPY waitforserver.sh waitforserver.sh
RUN chmod +x waitforserver.sh