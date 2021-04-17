# ChatRuum
OOP group work.  

___

## Kuidas rakendust jooksutada:

### Server
`gradlew server:run` või IDEA-s `server` -> `Tasks` -> `application` -> `run` gradle'i taskide alt  
Kui server käivitus edukalt, prinditakse konsooli vastav teade.

### Client
`gradlew client:run` või IDEA-s `client` -> `Tasks` -> `application` -> `run` gradle'i taskide alt  
Kui server jookseb samas arvutis saab sisestada aadressiks `localhost` ning vajutada connect, et serveriga ühenduda.

___

**Rollid:**  
UI  - Kikkatalo  
server - Sulg  
kliendi serveri proto - Juhan Oskar  

___

variant 2 kanalid HashMapidena

Serveriga ühendumine IP-ga  
Kasutajaliides JavaFXiga  
Gradle  
Liitu Kanaliga (vajadusel loo) -> kanalil seni toimunud kuvatakse osadena  
Kanalil nimi, parool (valikuline)  
Kasutajanimi, parool  

Sõnumi sisu-  
Saatja - kuupäev - sisu tüüp - sisu  

Rakenduse avamine -> sisse logimine -> kanaliga liitumine -> vestlus  

server protokoll - sõnumite tüübite ja sisude vahendamine  
Kliendi protokoll - sõnumi tüübi ja sisu saatmine / vastu võtmine  

Kanali sisu- viimased n sõnumit, aktiivsed kasutajad(nimi, arv), saada uut infot  

___

**UI** - Scene builder (gluon)  
sisse logimise aken  
main menu (kanaliga liitumine, üldsätted)  
vestluse menu  

**server**  
kanalite loomine  
saadetud sõnumid  
kasutajate info  
