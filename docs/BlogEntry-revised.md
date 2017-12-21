# Memory problemer ved query af MongoDB, og hvordan disse kan løses

![out of memory meme](https://github.com/gode-ting/UFO-blog-entry-frederik-daniel/blob/master/resources/out-of-memory-meme.jpg)

Memory problemer kan være et stort problem, når man gerne vil have en hurtig og responsiv applikation. De er skyld i at applikationer kan fremstpå langsome, og i visse tilfælde crashe. Memory problemer er ofte svære at finde, da de ofte ikke peger på nogle konkrete steder som er årsag til problemet, istedet viser de det ved at gøre applikationen langsommere, eller simpelt, fortælle at de er løbet tør for hukommelse, og derfor har lukket applikationen. Optimering af en applikations memory forbrug kan ofte lede til forbedret performance, samtidig med at man kan spare penge på sin hosting, da man ikke skal allokere applikationen lige så meget memory.

I vores applikation har vi brugt MongoDB til at lagre data, og bruger ligeledes MongoDB til at query data.

<!-- ## Hvad er memory leaks

Et memory leak er en type af ressource lækage, hvor et program håndterer memory allokeringen/tildelingen forkert, således, at det memory der ikke længere er skal bruges, ikke bliver released/frigivet igen. Altså betyder det, at en del eller hele af det tilgængelig memory er blevet allokeret. Når memory er allokeret, betyder det, at det allerede blevet brugt i en proces. Derfor vil man ved optimal håndtering af memory, frigive hukommelsen så snart processen er færdig, så den igen kan benyttes af andre processer. -->

<!-- ## Hvorfor skal man have styr på sin applikations memory forbrug -->

<!-- ### Reduceret performance

Memory leaks kan være med til, at reducerer en computers ydeevne, da det reducerer mængden af tilgængeligt hukommelse. På et tidspunkt kan en for stor del af systemets tilgængelige memory være allokeret, og dele eller hele systemet eller enheden kan stoppe med at fungere korrekt, applikationen kan fejle, eller systemet kan blive langsomt pga. trashing. -->

## Fordele ved ordentlig memory management

de fleste applikationer idag udnytter sig af en eller anden form for hosting, og de fleste hosting sites sælger deres produkt i pakker.
Hver pakke kommer med forskellige specs. Det kunne eksempelvis være hvor meget ram de tilbyder, i hver enkelt pakke. Derfor er det vigtigt at have styr på at bruge så minimalt meget memory som muligt, så kan man undgå at skulle betale for mere ram end man har behov for.
Derudover kan man potentielt opnå hurtigere applikationer ved at formindske memory forbrugen.

## Vores problem og dets konsekvenser

I vores applikation foretager vi database queries. Måden hvorpå vi gjorde dette, før vi løste vores problem, var skyld i, at vi alt memory som var allokeret applikationen, hvilket betød, at vores applikation crashede.

Grunden var, at hver gang `/latest` blev kaldt, hentede vores applikation alle posts fra vores MongoDb, sortede dem i faldende rækkefølge og returnerede det første element's hanesstID (seneste element).
Den process krævede en del hukommelse, og hvis `/latest` blev kaldt ofte, eller hvis kaldet til databasen indeholdt for meget data, løb vores applikation tør for tilgængelig memory, og crashede. Den fik en **Out of Memory** error.

## Hvordan løste vi vores problem

Vi refaktorerede vores kode, og optimerede måden hvorppå vi håndterede queries til databasen. Optimeringen gjorde, at vi ikke processerede lige så meget data ad gangen, så der ikke skulle bruges lige så meget memory. Det betød at applikationen ikke løb tør for memory, og crashede ikke med en "out of memory" error.

I vores system har vi en klasse **LatestDigestedController.java**, hvor vi havde en metode **getLatest(){}** som oprindeligt så således ud:

```java
@RequestMapping(method = RequestMethod.GET)
public int getLatest(){
	List<Post> maxObject = repository.findAll(new Sort(Sort.Direction.DESC, "hanesstID"));
	int latest = !maxObject.isEmpty() ?  maxObject.get(0).getHanesstID() : 0;
	return latest;
}
```

Efter vores refaktorering, så den således ud:

```java
@RequestMapping(method = RequestMethod.GET)
public int getLatest(){
	Query query = new Query();
	query.with(new Sort(Sort.Direction.DESC, "hanesstID"));
	query.limit(5);
	List<Post> maxObject = mongoTemplate.find(query, Post.class);
	int latest = !maxObject.isEmpty() ?  maxObject.get(0).getHanesstID() : 0;
	return latest;
}
```

Forskellen består i, at vi ikke processerer alle posts samtidig, men i stedet sætter et limit på antallet queries. På den måde undgår vi, at applikationen kommer på overarbejde, og løber tør på memory.

## Hvornår opstod problemet

Til at starte med, fungerede vores MongoDb kald upåklageligt. Det virkede efter hensigten, og der var hverken performance- eller out of memory issues. Måden hvorpå vi hentede og beregnede seneste element gjorde dog, at processen krævede mere og mere kraft/memory, da der løbenede kom flere og flere elementer i databasen. Det betød, at metoden løbende blev mere og mere krævene, og til sidste altså betød, at den opbrugte hele applikationens memory.

### Alternative løsninger

#### Anden database og anderledes query

Det kan tænkes, at man ved brug af en anden database, ikke var stødt ind i problemet her, eller kunne have løst det anderledes, for eksempel ved at ændre direkte på query kaldet. Ved at ændre på database kaldet, kunne man måske lette arbejde på applikationen, og i stedet flytte det hen på databasen. Dette skulle selvfølgelig optimeres således, at man ikke udelukkende flyttede problemet fra ét sted til et andet.

#### Øge størrelsen af Java Heap size

Nogle gange vil det være muligt, at øge allokeringen af memory, så applikationen blot får mere ram tilgængeligt. Det kan dog ikke garanteres, at det altid er løsningen, da der vil være tilfælde hvor processerne i applikationen stadig vil opbruge alt memory, selvom man øger allokeringen. Samtidig kan det være, at man ignorerer et reelt problem, da det out of memory ofte skyldes dårlig kodning, og derfor må det være dér man tager fast først.

En måde hvorpå man kan øge Javas heap size er således:

```java
export JVM_ARGS="-Xms1024m -Xmx1024m"
```

## Hvornår opstår memory problemer

Problemet opstår når en applikation skal holde styr på for mange referencer til for mange objekter. I Java og andre programmerings sprog hvor der er en indbygget garbage collecter, opstår dette problem når garbage collecteren ikke kan følge med, med antallet af ikke brugte referencer. I programmering sprog som C++, hvor der ikke er en indbygget garbage collecter, opstår problemet når man glemmer at frigøre hukommelse.

![Memory problem illustration C++](https://github.com/gode-ting/UFO-blog-entry-frederik-daniel/blob/master/resources/DeleteReferenceC%2B%2B.PNG)

## Hvordan kan problemet løses

Memory problemer kan deles ind i to grupper:

* simpelt mangel på ram. Dette problem opstår når en application skal bruge mere ram end den har. Derfor er problemet også nemt løst, da det eneste der skal gøres, er at allokere mere ram hvor det mangler.
* over konsumering. Dette problem opstår når en handling bruger mere ram en den egentlig burde. Dette sker ofte når noget ikke er kodet ordenligt. som f.eks. en metode eller en database query som bruger alt det allokerede ram. Dette problem er svære at løse, fordi det kan forveksles med det tidligere nævnte problem, og hvis man prøver at allokere mere ram til applicationen, bliver det bare brugt op på samme måde. Så dette problem bliver løst ved at optimere ens kode.

### Lokalisering af problemet

Der findes overvågnings tools som visualvm, hvor man kan overgåe "running code" og visualisere brugen af memory. Herved kan man følge brugen af memory, og have lettere ved, at lokalisere hvilke processer der opbruger store mængder.

![Visual vm illustration](https://github.com/gode-ting/UFO-blog-entry-frederik-daniel/blob/master/resources/VisualVm.PNG)

## Arbejde fremad - - *Bedre overskrift??* - -

Ting der er gode at være opmærksom på, hvis man vil undgå at støde ind i memory problemer i sin applikation:

* Husk at frigive de referencer du ikke skal bruge mere, eller reuse den allerede allokeret hukommelse.
* Lad vær med at hent hele alt dataen fra databasen samtidig, for så at sortere i din java applikation. Lav i stedet queries som gør det direkte på databasen - husk at optimere disse, så man ikke flytter stedet fra et sted til et andet.
* Undgå at alloker hukommelse til noget du ikke nødvendigvis behøver.
* Teste sine metoder ved forskellige scenarier, så man er klar over hvad de kan præstere, og herved mindsker risikoen for at løbe ind i uforudseete problemer i takt med, at data mængden stiger.

## Referencer

* [Trashing](https://en.wikipedia.org/wiki/Thrashing_(computer_science)).
* [http://searchwindowsserver.techtarget.com/definition/memory-leak](http://searchwindowsserver.techtarget.com/definition/memory-leak).
* [https://en.wikipedia.org/wiki/Memory_leak](https://en.wikipedia.org/wiki/Memory_leak).
* [Digital ocean - pris ved skarling - increasing available memory](https://www.digitalocean.com/pricing)
* [https://stackoverflow.com/questions/6261201/how-to-find-memory-leak-in-a-c-code-project](https://stackoverflow.com/questions/6261201/how-to-find-memory-leak-in-a-c-code-project)
* [https://visualvm.github.io/](https://visualvm.github.io/)
* [stack overflow - out of memory c++](https://stackoverflow.com/questions/6261201/how-to-find-memory-leak-in-a-c-code-project*)
* [MongoDB](https://www.mongodb.com/)
* [Out of memory fix Java - increasing available memory](https://confluence.atlassian.com/confkb/how-to-fix-out-of-memory-errors-by-increasing-available-memory-154071.html)
* [Java out of memory fix - increase java heap size](http://javarevisited.blogspot.dk/2011/09/javalangoutofmemoryerror-permgen-space.html)