;; This is a problem for shakey domain
;; We are going to vary 2 parameters. The number of objects to be moves and the numbers of rooms
;; we are going to put all objects in the first room and shakey in the same room.
;; the job is to move all objects from the first to last where all rooms are after each other. 

;; here are the parameters 
;; objects to move (5, 7, 10)
;; rooms in the model (3, 5, 10)

(define (problem task2)
  (:domain shakey)
  (:objects 
  			r1 r2 r3 r4 r5 r6 r7 r8 r9 r 	                           - room
  			s 		 	                                   - shakey
  			sw1                                        - switch
  			b			                                     - box
  			c1 c2		                                   - claw
  			o1 o2 o3 o4 o5 o6 o7 o8 o9 o10               	           - object

  )

  (:init 
  	;; All things that are set to be true when we start
  			(adjacent r1 r2)	(adjacent r2 r3)
        (adjacent r3 r4) (adjacent r4 r5) 
        (adjacent r5 r6)
        (adjacent r6 r7)   (adjacent r7 r8)   (adjacent r8 r9) (adjacent r9 r) 

        (robot-at s r1)		(box-at	b r1) (empty c1) (empty c2) (switch-at sw1 r1) 
  			
  			(object-at	o1 r1) (object-at o2 r1) (object-at o3 r1) (object-at o4 r1) (object-at o5 r1)
        (object-at  o6 r1) (object-at o7 r1) 
        (object-at o8 r1) (object-at o9 r1) (object-at o10 r1)
  			
  )
  (:goal 
  		(and (object-at o1 r) (object-at o2 r) (object-at o3 r) 
            (object-at o4 r) (object-at o5 r) 
            (object-at  o6 r) (object-at o7 r) 
            (object-at o8 r) 
            (object-at o9 r) (object-at o10 r)
      )
  )
  )