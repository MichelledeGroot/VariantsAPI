# VariantsAPI

De VariantsAPI is een Docker applicatie met 3 services. Als eerste wordt de Mongo Database gestart. Hierna wordt deze database gevuld met de varianten van gnomAD. Hierbij worden eerst de varianten die beschouwd worden als niet pathogeen eruit gefilterd. Dit script is geschreven in Python. 

De API is een SpringBoot applicatie die een connectie maakt met de MongoDB. Als input wordt er een tsv bestand verwacht met de chromosomen en de posities. Als resultaat kan er een bestand gedownload worden waar elke variant aangevuld is met een ‘pathogenic’ of ‘benign’. De varianten met ‘pathogenic’ kunnen verder worden onderzocht. Deze mogelijke pathogene varianten zijn ook terug te vinden in een json bestand.

De applicatie kan worden gerund door:
`sudo docker-compose up –build`


