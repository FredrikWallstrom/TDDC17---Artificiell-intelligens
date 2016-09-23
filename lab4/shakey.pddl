;; This is the domain for shakey

(define (domain shakey)
	(:requirements 
		:strips
		:typing
		:equality
	)
	;; There are 6 different types that we have in our model
	;; We have chosen these because this is what we think is necessary and still keep
	;; the problem from being too simple.

	(:types
		box				; boxes that can be pushed
		switch 			; turn on/off light
		room 			; there are several connected rooms
		shakey 			; the robot
		object 			; small objects that can be lifted by robot
		claw			; his both hands
	)

	;; The predicates we have are chosen after what we need for the actions
	;; some things have been simplified for example not "caring" about the fact that the 
	;; box is under a switch but only caring about the fact that the box is in the room. 

	(:predicates
		(adjacent		?r1	?r2 - room)			; can move from ?r1 directly to ?r2
		(wide-entrance	?r1 ?r2 - room)			; is there a wide door between ?r1 and ?r2
		
		(box-at			?b	- box ?r - room) 	; box ?b1 is in room ?r
		(robot-at		?s - shakey ?r - room)  ; is shakey ?s in room ?r
		(switch-at		?s - switch ?r - room)	; is switch ?s in room ?r
		(object-at		?o - object ?r - room)	; is there small objects ?o in room ?r
		
		(light			?r - room)				; is room ?r lit 
		
		(holding		?c - claw 	?o - object); is claw ?c holdig object ?o
		(empty			?c - claw)				; is claw ?c empty 
	)



	;; For actions that have a precondition for adjacent or wide opening we look for both ways
	;; so room1 and room2 adjacent OR room2 and room1 adjacent. The reason for this is that when
	;; we define the problem we only say that room1 is adjacent to room2 so there is not that much 
	;; to write in the problem.

	;; This action MOVE will move shakey from one room to another, the rooms have to be adjacent
	;; and the robot must be in the room he is moving from.
	(:action move
		:parameters		(?s - shakey	?from ?to - room)

		:precondition 	(and (robot-at 	?s ?from)
							(or (adjacent 	?from ?to)
								(adjacent 	?to ?from)
							)
						)

		:effect			(and (robot-at	?s ?to)
						(not (robot-at	?s ?from))
						)
	)

	;; This action will turn lights on in a room and for this to happen 
	;; we need a box to be in the room 
	;; where we want to turn the light on. We also make sure that the light is off and that there 
	;; is a lightswitch.
	(:action turn-light-on
		:parameters		(?s - shakey	?sw - switch 	?r - room 	?b - box)

		:precondition	(and (robot-at	?s ?r)
						 (switch-at ?sw ?r)
						 (box-at	?b  ?r)
						 (not (light ?r))
						)

		:effect			(light ?r)
	)

	;; This action will turn lights off in  a room and for this to happen we need 
	;; a box to be in the room where we want to turn the light off. We also make sure that the 
	;; light is turned on in the room. Here, compared to TURN-LIGHT-ON we dont need to look for a 
	;; lightswitch since if the light is turned on, there has to be a lightswitch in the room
	(:action turn-light-off
		:parameters		(?s - shakey	?r - room 	?b - box)

		:precondition	(and (robot-at	?s ?r)
						 (box-at	?b  ?r)
						 (light ?r)
						)

		:effect			(not (light ?r))
	)

	;; This action will move a box from one room to another room. The rooms have to be 
	;; adjacent for this to happen. Other things is that robot have to be in the 
	;; same room as the box and the rooms need to have a wide opening.
	(:action move-box
		:parameters		(?s - shakey 	?b - box 	?from ?to - room)

		:precondition	(and (robot-at	?s 	?from)
							 (box-at 	?b 	?from)
							(or (adjacent 	?from ?to)
								(adjacent 	?to ?from)
							)
							(or (wide-entrance 	?from ?to)
							(wide-entrance 	?to ?from)
							)
						)

		:effect			(and (robot-at 	?s 	?to)
							 (box-at 	?b 	?to)
							 (not (box-at 	?b 	?from))
							 (not (robot-at ?s 	?from))
						)
	)

	;; This action will pick up an object only if there is a claw (shakey have two) that is free 
	;; and the lights are on in the room where we are going to pick up the objects. Of course 
	;; shakey has to be in the same room too. 
	(:action pick-up
		:parameters 	(?s - shakey 	?r - room 	?c - claw 	?o - object)

		:precondition 	(and (robot-at 	?s 	?r)
							 (object-at ?o 	?r)
							 (light 	?r)
							 (empty 	?c)
						)

		:effect			(and (not(object-at ?o 	?r))
							 (holding	?c 	?o)
							 (not (empty ?c))
						)
	)

	;; This action will put down an object and for this to happen shakey has to hold something 
	;; in one of his claws. The light does not need to be turned on for shakey 
	;; to put down an object 
	
	(:action put-down
		:parameters 	(?s - shakey 	?r - room 	?c - claw 	?o - object)

		:precondition 	(and (robot-at 	?s 	?r)
							 (holding ?c ?o)
						)

		:effect			(and (object-at ?o 	?r)
							 (not (holding	?c 	?o))
							 (empty ?c)
						)
	)
)