------------------------------- MODULE NoLoop ------------------------------- 
EXTENDS Sequences, Naturals, TLC

(*   
  --algorithm NoLoop
    variable x = 0; y = 0 ;
    begin a : with i = 3 do x := i ; end with;
          b : with j \in { 1 , 2 } do y := j ; x := x + y ; end with ;
          c : if y = 1 then x := x + 1 ; else x := x + y; end if;
              when Print ( x , TRUE );
    end algorithm
*)
					
(***** BEGIN TRANSLATION ***)
VARIABLES x, y, pc

vars == << x, y, pc >>

Init == (* Global variables *)
        /\ x = 0
        /\ y = 0
        /\ pc = "a"

a == /\ pc = "a"
     /\ LET i == 3 IN
          x' = i
     /\ pc' = "b"
     /\ y' = y

b == /\ pc = "b"
     /\ \E j \in { 1 , 2 }:
          /\ y' = j
          /\ x' = x + y'
     /\ pc' = "c"

c == /\ pc = "c"
     /\ IF y = 1
           THEN /\ x' = x + 1
           ELSE /\ x' = x + y
     /\ Print ( x' , TRUE )
     /\ pc' = "Done"
     /\ y' = y

Next == a \/ b \/ c
           \/ (* Disjunct to prevent deadlock on termination *)
              (pc = "Done" /\ UNCHANGED vars)

Spec == /\ Init /\ [][Next]_vars
        /\ WF_vars(Next)

Termination == <>(pc = "Done")

(***** END TRANSLATION ***)
=============================================================================
