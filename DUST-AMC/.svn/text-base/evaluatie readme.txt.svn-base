Hoi Silvia,

Intussen heb ik nieuwe, betere code voor monitoren omgebouwd, met meer eventtypes:

start: start van een workflow
jobstart: start van een job
joberror: error van een job (zowel logfile detectie als grid callback)
jobsuccess: slagen van een job
update: statusinfo van een job
resubmit: resubmit van een job
success: geslaagde workflow
error: gefaalde workflow

Ik ben er nu even vanuit gegaan dat het starten van een agent in een job later getest wordt. Het werkt wel, maar moet helderder opgezet en beschreven worden.

- de toepassing staat klaar in de directory monitoring onder mijn account op alegre

- startcommando: "ant -f run.xml" in die directory (evt. met nohup ervoor)

- er is een directory config onder monitoring, hier staan:

    -- moteur_monitor.properties, wijst zich redelijk vanzelf (alles onder "# app specific" stuurt het monitoren)
    -- status.properties, regexen per eventtype, commentaar in de file
    -- user_<naam>.properties, <naam> is de naam zoals in 'userToWorkflow.txt' geschreven staat, als user_<naam>.properties niet gevonden wordt gebruik default user_0.properties
    -- jade-logging.properties, loglevels aanpassen, voor debuggen enzo

Je kunt dus voor iedere user een configfile neerzetten waarin staat welke eventtypes naar wie gemaild worden.

logging wordt weggeschreven in ~/moteur_monitor0.log (server) en ~/moteur_monitor1.log (monitorapp, meest interessant)

Ik vermoed dat er met deze versie heel behoorlijk getest kan worden. Volgende week heb ik zelf meer zicht op mijn beschikbaarheid.

Groeten,

Eduard

