# Java-Queue-Problem
## Rules
* Jobs take turns to be processed in First-in First-out manner.
* At each turn, one job can run maximum 100 milliseconds (quantum time): 
  * If the execution/remaining execution time for the job is less than 100ms , I.e 30 ms, the job
leaves the queue at the end of 30 ms.
  * If the execution/remaining execution time for the job is 100ms, it leaves the queue after the
quantum time is over.
  * If the execution/remaining execution time for the job is over, after 100ms the job is dequeued
and enqueued back to the job queue and waits for its next turn.
* The processes should be executed until the queue is empty.
### Sample input: 
1. Job0 ,0 ,250
2. Job1 ,50 ,170
3. Job2 ,130 ,75
4. Job3 ,190 ,100
5. Job4 ,210 ,130
6. Job5 ,350 ,50
### Sample output:
CurrentTime -Queue - Event
1. 0-Job0 - Job0 enter
2. 50- Job0 ,Job1 - Job1 enter
3. 100 - Job1 ,Job0 - Job0 is expired , remaining 150 ms
4. 130 - Job1 ,Job0 ,Job2 - Job2 enter
5. 190 - Job1 ,Job0 ,Job2 ,Job3 - Job3 enter
6. 200 - Job0 ,Job2 ,Job3 ,Job1 - Job1 is expired , remaining 70 ms
7. 210 - Job0 ,Job2 ,Job3 ,Job1 ,Job4 - Job4 enter
8. 300 - Job2 ,Job3 ,Job1 ,Job4 ,Job0 - Job0 is expired , remaining 50 ms
9. 350 - Job2 ,Job3 ,Job1 ,Job4 ,Job0 ,Job5 - Job5 enter
10. 375 - Job3 ,Job1 ,Job4 ,Job0 ,Job5 - Job2 is terminated
11. 475 - Job1 ,Job4 ,Job0 ,Job5 - Job3 is terminated
12. 545 - Job4 ,Job0 ,Job5 - Job1 is terminated
13. 645 - Job0 ,Job5 ,Job4 - Job4 is expired , remaining 30 ms
14. 695 - Job5 ,Job4 - Job0 is terminated
15. 745 - Job4 - Job5 is terminated
17 775 -- Job4 is terminated
## Run
/.java Queue input.txt
