# Memory problemer

Memory problemer kan være et stort problem, når man gerne vil have en hurtig og responsiv applikation. De er skyld i at applikationer kan fremstpå langsome, og i visse tilfælde crashe. Memory problemer er ofte svære at finde, da de ofte ikke peger på nogle konkrete steder som er årsag til problemet, istedet viser de det ved at gøre applikationen langsommere, eller simpelt, fortælle at de er løbet tør for hukommelse, og derfor har lukket applikationen. Optimering af en applikations memory forbrug kan ofte lede til forbedret performance, samtidig med at man kan spare penge på sin hosting, da man ikke skal allokere applikationen lige så meget memory.

## Hvad er memory leaks

Et memory leak er en type af ressource lækage, hvor et program håndterer memory allokeringen/tildelingen forkert, således, at det memory der ikke længere er skal bruges, ikke bliver released/frigivet igen. Altså betyder det, at en del eller hele af det tilgængelig memory er blevet allokeret. Når memory er allokeret, betyder det, at det allerede blevet brugt i en proces. Derfor vil man ved optimal håndtering af memory, frigive hukommelsen så snart processen er færdig, så den igen kan benyttes af andre processer.

## Hvorfor er det vigtigt at løse problemet

### Reduceret performance

Memory leaks kan være med til, at reducerer en computers ydeevne, da det reducerer mængden af tilgængeligt hukommelse. På et tidspunkt kan en for stor del af systemets tilgængelige memory være allokeret, og dele eller hele systemet eller enheden kan stoppe med at fungere korrekt, applikationen kan fejle, eller systemet kan blive langsomt pga. trashing.

### Fordele ved ordentlig memory management

de fleste applikationer idag udnytter sig af en eller anden form for hosting, og de fleste hosting sites sælger deres produkt i pakker.
Hver pakke kommer med forskellige specs. Det kunne eksempelvis være hvor meget ram de tilbyder, i hver enkelt pakke. Derfor er det vigtigt at have styr på at bruge så minimalt meget memory som muligt, så kan man undgå at skulle betale for mere ram end man har behov for.
Derudover kan man potentielt opnå hurtigere applikationer ved at formindske memory forbrugen.

## Vores problem og dets konsekvenser

I vores applikation foretager vi database queries. Måden hvorpå vi gjorde dette, før vi løste vores problem, var skyld i, at vi alt memory som var allokeret applikationen, hvilket betød, at vores applikation crashede.

Grunden var, at hver gang `/latest` blev kaldt, hentede vores applikation alle posts fra databasen, sortede dem i faldende rækkefølge og returnerede det første element's hanesstID (latest).
Den process krævede en del hukommelse, og hvis `/latest` blev kaldt ofte, eller hvis kaldet til databasen indeholdt for meget data, løb vores applikation tør for tilgængelig memory, og crashede. Den fik en **Out of Memory** error.

## Hvordan løste vi vores problem

Vi refaktorerede vores kode, og optimerede måden hvorppå vi håndterede queries til databasen. Optimeringen gjorde, at vi ikke processerede lige så meget data ad gangen, så der ikke skulle bruges lige så meget memory. Det betød at applikationen ikke løb tør for memory, og crashede ikke med en "out of memory" error.

## Hvornår opstår problemet

Problemet opstår når en applikation skal holde styr på for mange referencer til for mange objekter. I Java og andre programmerings sprog hvor der er en indbygget garbage collecter, opstår dette problem når garbage collecteren ikke kan følge med, med antallet af ikke brugte referencer. I programmering sprog som C++, hvor der ikke er en indbygget garbage collecter, opstår problemet når man glemmer at frigøre hukommelse - - *Indsæt dette billed her (for jeg kan ikke huske hvordan man gør): https://stackoverflow.com/questions/6261201/how-to-find-memory-leak-in-a-c-code-project* - -

![Memory problem illustration C++](https://github.com/gode-ting/UFO-blog-entry-frederik-daniel/blob/master/resources/DeleteReferenceC%2B%2B.PNG)

## Hvordan kan problemet løses

Memory problemer kan deles ind i to grupper:

* simpelt mangel på ram. Dette problem opstår når en application skal bruge mere ram end den har. Derfor er problemet også nemt løst, da det eneste der skal gøres, er at allokere mere ram hvor det mangler.
* over konsumering. Dette problem opstår når en handling bruger mere ram en den egentlig burde. Dette sker ofte når noget ikke er kodet ordenligt. som f.eks. en metode eller en database query som bruger alt det allokerede ram. Dette problem er svære at løse, fordi det kan forveksles med det tidligere nævnte problem, og hvis man prøver at allokere mere ram til applicationen, bliver det bare brugt op på samme måde. Så dette problem bliver løst ved at optimere ens kode.

### Finde problemet

Der findes overvågnings tools som visualvm, det de gør er at overvåge "running code" og visuallisere brugen af memory. Derfor kan man følge med på brugen af memory, og finde problemet. - - *Indsæt dette billed her (for jeg kan ikke huske hvordan man gør): https://github.com/gode-ting/UFO-blog-entry-frederik-daniel/blob/master/resources/VisualVm.PNG* - - 

## Arbejde fremad - - *Bedre overskrift??* - -

Her er en liste over de ting du skal være opmærksom på hvis du gerne ville undgå at løbe ind i memory problemer, når du skriver kode til din applikation:

* Husk at frigive de referencer du ikke skal bruge mere, eller reuse den allerede allokeret hukommelse.
* Lad vær med at hent hele databasen, for så at sortere i din java applikation. gør det på database niveauet.
* Undgå at alloker hukommelse til noget du ikke nødvendigvis behøver.

## Referencer

* [Trashing](https://en.wikipedia.org/wiki/Thrashing_(computer_science)).
* [http://searchwindowsserver.techtarget.com/definition/memory-leak](http://searchwindowsserver.techtarget.com/definition/memory-leak).
* [https://en.wikipedia.org/wiki/Memory_leak](https://en.wikipedia.org/wiki/Memory_leak).
* [https://www.digitalocean.com/pricing/](https://www.digitalocean.com/pricing)
* [https://stackoverflow.com/questions/6261201/how-to-find-memory-leak-in-a-c-code-project](https://stackoverflow.com/questions/6261201/how-to-find-memory-leak-in-a-c-code-project)
* [https://visualvm.github.io/](https://visualvm.github.io/)