# Notes (minutes) 29 November Meeting #2

- What exactly does need to be handed in for the first assignment on 02.12.2022? - check Brightspace SEM page - deadlines for what we need to handle in

- Up to what point can a user specify availability (e.g. weekly and then repeat, monthly)?
    
    Design choice up to us (argument & document each design choice made in the first assignment / code) 
    
- Is each class with its own database considered a microservice?
    
    Each microservice should have its own isolated (internal) database
    
    Microservices discuss through API calls (not database connections) ⇒ No 
    
    ⚠️ No big database that stores multiple microservices  combined data 
    
    2 microservices should never comunicate directly one with another
    
- Does new boat type addition implicate new certificate addition?
    
    Depends on boat type 
    
    Manually define the constraints for a new certificate
    
    For current certificates → hardocde the requirements
    
- Should the available positions in Activity be a list or a separate table of objects with columns (“ActivityId -> Long, taken -> boolean, position -> String”)?
    
    Design choice once again + consider efficiency 
    
- Can users/competitions may have multiple organization affiliations/requirements?
    
    1 user → 1 organisation
    
    All members within a team (boat) needs to be from the same association
    
    Competitions can have competing teams from different organisations
    

### Remarks

- Textually explain the Bounded Context diagram
- Context Map - Mix between UML and Bounded Context (maybe create all 3 maps)
- Microservices communication - query using unique ID in order to get data from Entity A to Entity B
- For 2nd December deadline → every document will be pushed to GitLab, no need to upload anything on Brightspace
- Suggestion → List of endpoints that documented how to communicate within microservices
- No hard deadlines for code until 16 December
- Unit tests suggestion: try to get 100% branch coverage, write unit tests while we implement features
- Keep up the pace !

## Feedback

### General

- Great that we added the template, agenda W3
- Suggestion → add agenda & notes from W2
- Graded on how we separate the microservices (do not over-split)

### Bounded Context map

- Correct, keep them
- Suggest → Try joining Matching and Activity context (if you want to) → Design choice (discuss during unofficial team meeting)

### UML Diagram

- userID → unique String, not Long (written in Scenario)
- Boat → can be modeled inside of Activity microservice, also as a String (no need for Boat class as it has only 1 attribute)

## Requirements

- Functional:
    - Won’t have (users won’t delete/edit …) → move it as a Must have
    - Everything related to Notifications → Could have
    - Certificates → Should have
- Non-functional
    - Rephrase Spring security part - Authentification and Authorization should be done using Spring Security

## Context map

- Should be done in the next unofficial meeting