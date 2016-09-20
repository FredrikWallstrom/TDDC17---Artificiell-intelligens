(define (domain shakey)
	(:requirements 
		:strips)
		:typing
		:equality
	)
	
	(:types
		box				; boxes that can be pushed
		switch 			; turn on/off light
		room 			; there are several connected rooms
		shakey 			; the robot
		object 			; small objects that can be lifted by robot
		claw			; 
	)

	(:predicates
		(adjacent		?r1	?r2 - room)			; can move from ?r1 directly to ?r2
		(wide-entrace	?r1 ?r2 - room)			; is there a wide door between ?r1 and ?r2
		
		(box-at			?b	- box ?r - room) 	; box ?b1 is in room ?r
		(robot-at		?s - shakey ?r - room)  ; is shakey ?s in room ?r
		(switch-at		?s - switch ?r - room)	; is switch ?s in room ?r
		(objects-at		?o - object ?r - room)	; is there small objects ?o in room ?r
		
		(light			?s - switch)			; is switch ?s on
		(box-under		?b - box  	?s - switch); is the box ?b under the switch ?s
		(on-box			?s - shakey	?b - box)	; is shakey ?s on box ?b
		
		(holding		?c - claw 	?o - object); is claw ?c holdig object ?o
		(empty			?c - claw)				; is claw ?c empty 
	)

	(:action move
		:parameters		(?s - shakey	?from ?to - room)

		:precondition 	(and (adjacent 	?from ?to)
							 (robot-at 	?s ?from)
						)

		:effect			(and (robot-at	?s ?to)
						(not (robot-at	?s ?from))
						)
	)


		(:action turn-light-on
			:parameters		(?s - shakey	?sw - switch 	?r - room 	?b - box)

			:precondition	(and (robot-at	?s ?r)
							 (switch-at ?sw ?r)
							 (not (light ?sw))
						)

			:effect			(light ?sw)

	)

)