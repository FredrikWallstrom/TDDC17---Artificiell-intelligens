;; This is a problem for shakey domain

(define (problem all-lights-on-and-move-object)
  (:domain shakey)
  (:objects 
  			r1 r2 r3 	 - room
  			s 		 	 - shakey
  			sw1 sw2 sw3	 - switch
  			b			 - box
  			c1 c2		 - claw
  			o1 		 	 - object
  			
  )
  (:init 
  	;; All things that are set to be true when we start
  			(adjacent r1 r2)	(adjacent r2 r3)	(robot-at s r1)		(box-at	b r2)
  			(switch-at sw1 r1)	(switch-at sw2 r2)	(switch-at sw3 r3)	
  			(wide-entrance r1 r2) (wide-entrance r2 r3) (object-at	o1 r1)
  			(empty c1)	(empty c2)
  )
  (:goal 
  ;; The goal is to turn on all the lights and also move one object from room 1 to room 2
  		(and (light r1)
  			 (light r2)
  			 (light r3)
  			 (object-at o1 r2))
  )
  )