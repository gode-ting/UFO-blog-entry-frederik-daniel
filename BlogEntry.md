# Memory leaks

## Vores problem og dets konsekvenser

I vores applikation foretager vi database queries. Måden hvorpå vi gjorde dette, var skyld i, at vi havde memory leaks, som fik vores applikation til at crashe.

Grunden var, at hver gang `/latest` blev kaldt, hentede vores applikation alle posts som simulatoren havde sendt, sortede dem i faldende rækkefølge og det det første element's hanesstID. Denne opgave kræver en del hukommelse, og når `/latest` ofte bliver kaldt, løb applikationen på et tidspunkt tør for memory, og crashede. **Årsag:** "Out of Memory" error.

## Hvad er memory leaks?

Et memory leak er en type af ressource lækage, hvor et program håndterer memory allokeringen/tildelingen forkert, således, at det memory der ikke længere er skal bruges, ikke bliver released/frigivet igen. Altså betyder det, at en del eller hele af det tilgængelig memory er blevet allokeret. Når memory er allokeret, betyder det, at det allerede blevet brugt i en proces. Derfor vil man ved optimal håndtering af memory, frigive hukommelsen så snart processen er færdig, så den igen kan benyttes af andre processer.

## Hvilke problemer kan memory leaks skabe?

### Reduceret performance

Memory leaks kan være med til, at reducerer en computers ydeevne, da det reducerer mængden af tilgængeligt hukommelse. På et tidspunkt kan en for stor del af systemets tilgængelige memory være allokeret, og dele eller hele systemet eller enheden kan stoppe med at fungere korrekt, applikationen kan fejle, eller systemet kan blive langsomt pga. trashing

## Hvordan kan man løse problemer med memory leaks?

## Hvordan løste vi vores problem?

Vi refaktorerede vores kode, og håndterer queries til vores database anderledes, således, at vi ikke processerede for meget data ad gangen.

## Referencer

* [Trashing](https://en.wikipedia.org/wiki/Thrashing_(computer_science)).
* [http://searchwindowsserver.techtarget.com/definition/memory-leak](http://searchwindowsserver.techtarget.com/definition/memory-leak).
* [https://en.wikipedia.org/wiki/Memory_leak](https://en.wikipedia.org/wiki/Memory_leak).
